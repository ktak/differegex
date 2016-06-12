package ktak.differegex;

import java.util.Comparator;

import org.junit.Assert;
import org.junit.Test;

import ktak.immutablejava.AATreeSet;
import ktak.immutablejava.List;

public class PartitionTest {
    
    private static final Comparator<Character> charCmp = new Comparator<Character>() {
        
        @Override
        public int compare(Character arg0, Character arg1) {
            return arg0.compareTo(arg1);
        }
        
    };
    
    @Test
    public void testIntersectionWithTrivial() {
        
        Partition<Character> trivial = Partition.trivial(charCmp);
        Partition<Character> partition1 = Partition.singleChar(charCmp, 'a');
        
        Partition<Character> trivialTrivial = trivial.intersect(trivial);
        Partition<Character> trivialPartition1 = trivial.intersect(partition1);
        
        Assert.assertTrue(
                (trivial.negatedSubset.sortedList().compareTo(
                        trivialTrivial.negatedSubset.sortedList(),
                        charCmp) == 0) &&
                (trivial.subsets.sortedList().compareTo(
                        trivialTrivial.subsets.sortedList(),
                        trivial.subsets.getComparator()) == 0));
        
        Assert.assertTrue(
                (partition1.negatedSubset.sortedList().compareTo(
                        trivialPartition1.negatedSubset.sortedList(),
                        charCmp) == 0) &&
                (partition1.subsets.sortedList().compareTo(
                        trivialPartition1.subsets.sortedList(),
                        trivial.subsets.getComparator()) == 0));
        
    }
    
    @Test
    public void nonTrivialIntersection() {
        
        Partition<Character> partition1 = Partition.singleChar(charCmp, 'a');
        Partition<Character> partition2 = Partition.singleChar(charCmp, 'b');
        
        List<Character> negatedSubset = new List.Nil<Character>().cons('b').cons('a');
        List<AATreeSet<Character>> subsets = new List.Nil<AATreeSet<Character>>()
                .cons(AATreeSet.emptySet(charCmp).insert('b'))
                .cons(AATreeSet.emptySet(charCmp).insert('a'))
                .cons(AATreeSet.emptySet(charCmp));
        
        Assert.assertTrue(
                (negatedSubset.compareTo(
                        partition1.intersect(partition2).negatedSubset.sortedList(),
                        charCmp) == 0) &&
                (subsets.compareTo(
                        partition1.intersect(partition2).subsets.sortedList(),
                        partition1.subsets.getComparator()) == 0));
        
    }
    
}
