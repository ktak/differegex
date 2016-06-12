package ktak.differegex;

import java.util.Comparator;

import ktak.immutablejava.AATreeSet;

class Partition<CharType> {
    
    protected final AATreeSet<AATreeSet<CharType>> subsets;
    protected final AATreeSet<CharType> negatedSubset;
    
    private static final class SubsetComparator<CharType> implements Comparator<AATreeSet<CharType>> {
        
        @Override
        public int compare(AATreeSet<CharType> o1, AATreeSet<CharType> o2) {
            return o1.sortedList().compareTo(o2.sortedList(), o2.getComparator());
        }
        
    }
    
    private Partition(Comparator<CharType> cmp) {
        this.subsets = AATreeSet.emptySet(new SubsetComparator<CharType>());
        this.negatedSubset = AATreeSet.emptySet(cmp);
    }
    
    private Partition(
            AATreeSet<AATreeSet<CharType>> subsets, AATreeSet<CharType> negatedSubset) {
        this.subsets = subsets;
        this.negatedSubset = negatedSubset;
    }
    
    // creates a partition with a single subset - the negation of the empty set
    public static <CharType> Partition<CharType> trivial(Comparator<CharType> cmp) {
        
        return new Partition<CharType>(
                AATreeSet.emptySet(new SubsetComparator<CharType>()),
                AATreeSet.emptySet(cmp));
        
    }
    
    // creates a partition with two subsets - one for a single char and one for the negation
    //  of that char
    public static <CharType> Partition<CharType> singleChar(
            Comparator<CharType> cmp, CharType charValue) {
        
        return new Partition<CharType>(
                AATreeSet.emptySet(new SubsetComparator<CharType>()).insert(
                        AATreeSet.emptySet(cmp).insert(charValue)),
                AATreeSet.emptySet(cmp).insert(charValue));
        
    }
    
    // creates a new partition from the pairwise intersection of each
    //  subset of two partitions
    public Partition<CharType> intersect(final Partition<CharType> other) {
        
        // NOT(S1) AND NOT(S2) = NOT(S1 OR S2)
        AATreeSet<CharType> negatedSubset = this.negatedSubset.union(other.negatedSubset);
        // NOT(S1) AND S2 = S2 - S1
        AATreeSet<AATreeSet<CharType>> negativeThisPositiveOther =
                other.subsets.sortedList().foldRight(
                        AATreeSet.emptySet(this.subsets.getComparator()),
                        (subset) -> (subsets) ->
                        subsets.insert(subset.difference(this.negatedSubset)));
        // S1 AND NOT(S2) = S1 - S2
        AATreeSet<AATreeSet<CharType>> positiveThisNegativeOther =
                this.subsets.sortedList().foldRight(
                        negativeThisPositiveOther,
                        (subset) -> (subsets) ->
                        subsets.insert(subset.difference(other.negatedSubset)));
        // S1 AND S2
        AATreeSet<AATreeSet<CharType>> positiveThisPositiveOther =
                this.subsets.sortedList().foldRight(
                        positiveThisNegativeOther,
                        (thisSubset) -> (subsets) ->
                        other.subsets.sortedList().foldRight(
                                subsets,
                                (otherSubset) -> (subsets2) ->
                                subsets2.insert(thisSubset.intersection(otherSubset))));
        return new Partition<CharType>(positiveThisPositiveOther, negatedSubset);
        
    }
    
}
