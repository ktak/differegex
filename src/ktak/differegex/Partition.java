package ktak.differegex;

import java.util.Comparator;

import ktak.immutablejava.AATreeSet;
import ktak.immutablejava.Either;
import ktak.immutablejava.List;

class Partition<CharType> {
    
    // the left component of the either represents the negation of a subset of values from CharType
    // the right component of the either represents a subset of values from CharType
    protected final AATreeSet<Either<AATreeSet<CharType>, AATreeSet<CharType>>> val;
    
    private static final class SubsetComparator<CharType> implements Comparator<Either<AATreeSet<CharType>, AATreeSet<CharType>>> {
        
        @Override
        public int compare(
                Either<AATreeSet<CharType>, AATreeSet<CharType>> o1,
                Either<AATreeSet<CharType>, AATreeSet<CharType>> o2) {
            
            // this comparator orders the left components (representing set negation) less than
            //  the right components and uses list ordering when comparing two left or two right
            //  components with each other
            return o1.match(
                    negativeSubset1 -> o2.match(
                            negativeSubset2 -> negativeSubset1.sortedList().compareTo(
                                    negativeSubset2.sortedList(), negativeSubset1.getComparator()),
                            positiveSubset2 -> -1),
                    positiveSubset1 -> o2.match(
                            negativeSubset2 -> 1,
                            positiveSubset2 -> positiveSubset1.sortedList().compareTo(
                                    positiveSubset2.sortedList(), positiveSubset1.getComparator())));
            
        }
        
    }
    
    private Partition() {
        this.val = AATreeSet.emptySet(new SubsetComparator<CharType>());
    }
    
    private Partition(AATreeSet<Either<AATreeSet<CharType>, AATreeSet<CharType>>> val) {
        this.val = val;
    }
    
    // creates a partition with a single subset - the negation of the empty set
    public static <CharType> Partition<CharType> trivial(Comparator<CharType> cmp) {
        
        return new Partition<CharType>(AATreeSet.emptySet(new SubsetComparator<CharType>()).insert(
                new Either.Left<AATreeSet<CharType>, AATreeSet<CharType>>(AATreeSet.emptySet(cmp))));
        
    }
    
    // creates a partition with two subsets - one for a single char and one for the negation
    //  of that char
    public static <CharType> Partition<CharType> singleChar(Comparator<CharType> cmp, CharType charValue) {
        
        return new Partition<CharType>(AATreeSet.emptySet(new SubsetComparator<CharType>()).insert(
                new Either.Left<AATreeSet<CharType>, AATreeSet<CharType>>(
                        AATreeSet.emptySet(cmp).insert(charValue))).insert(
                new Either.Right<AATreeSet<CharType>, AATreeSet<CharType>>(
                        AATreeSet.emptySet(cmp).insert(charValue))));
        
    }
    
    // creates a new partition from the pairwise intersection of each
    //  subset of two partitions
    public Partition<CharType> intersect(final Partition<CharType> other) {
        
        return intersectAllWithAll(this.val.sortedList(), other.val.sortedList());
        
    }
    
    private Partition<CharType> intersectAllWithAll(
            List<Either<AATreeSet<CharType>, AATreeSet<CharType>>> one,
            List<Either<AATreeSet<CharType>, AATreeSet<CharType>>> two) {
        
        return one.foldRight(
                new Partition<CharType>(),
                subset -> partition -> new Partition<CharType>(
                        partition.val.union(intersectAllWithOne(two, subset).val)));
        
    }
    
    private Partition<CharType> intersectAllWithOne(
            List<Either<AATreeSet<CharType>, AATreeSet<CharType>>> l,
            Either<AATreeSet<CharType>, AATreeSet<CharType>> one) {
        
        return l.foldRight(
                new Partition<CharType>(),
                subset -> partition -> new Partition<CharType>(
                        partition.val.insert(intersectOneWithOne(subset, one))));
        
    }
    
    private Either<AATreeSet<CharType>, AATreeSet<CharType>> intersectOneWithOne(
            Either<AATreeSet<CharType>, AATreeSet<CharType>> one,
            Either<AATreeSet<CharType>, AATreeSet<CharType>> two) {
        
        return one.match(
                negativeSubset1 -> two.match(
                        // NOT(S1) AND NOT(S2) = NOT(S1 OR S2)
                        negativeSubset2 -> Either.left(negativeSubset1.union(negativeSubset2)),
                        // NOT(S1) AND S2 = S2 - S1
                        positiveSubset2 -> Either.right(positiveSubset2.difference(negativeSubset1))),
                positiveSubset1 -> two.match(
                        // S1 AND NOT S2 = S1 - S2
                        negativeSubset2 -> Either.right(positiveSubset1.difference(negativeSubset2)),
                        // S1 AND S2
                        positiveSubset2 -> Either.right(positiveSubset1.intersection(positiveSubset2))));
        
    }
    
}
