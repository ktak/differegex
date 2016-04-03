package ktak.differegex;

import java.util.Comparator;

import org.junit.Assert;
import org.junit.Test;

import ktak.immutablejava.AATreeSet;
import ktak.immutablejava.Either;
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
        
        
        Assert.assertEquals(0, trivial.val.sortedList().compareTo(
                trivial.intersect(trivial).val.sortedList(),
                trivial.val.getComparator()));
        
        Assert.assertEquals(0, partition1.val.sortedList().compareTo(
                trivial.intersect(partition1).val.sortedList(),
                trivial.val.getComparator()));
        
    }
    
    @Test
    public void nonTrivialIntersection() {
        
        Partition<Character> partition1 = Partition.singleChar(charCmp, 'a');
        Partition<Character> partition2 = Partition.singleChar(charCmp, 'b');
        
        List<Either<AATreeSet<Character>, AATreeSet<Character>>> intersection =
                new List.Nil<Either<AATreeSet<Character>, AATreeSet<Character>>>()
                .cons(new Either.Right<AATreeSet<Character>, AATreeSet<Character>>(
                        AATreeSet.emptySet(charCmp).insert('b')))
                .cons(new Either.Right<AATreeSet<Character>, AATreeSet<Character>>(
                        AATreeSet.emptySet(charCmp).insert('a')))
                .cons(new Either.Right<AATreeSet<Character>, AATreeSet<Character>>(
                        AATreeSet.emptySet(charCmp)))
                .cons(new Either.Left<AATreeSet<Character>, AATreeSet<Character>>(
                        AATreeSet.emptySet(charCmp).insert('a').insert('b')));
        
        Assert.assertEquals(0, intersection.compareTo(
                partition1.intersect(partition2).val.sortedList(),
                partition1.val.getComparator()));
        
        Assert.assertEquals(0, intersection.compareTo(
                partition2.intersect(partition1).val.sortedList(),
                partition1.val.getComparator()));
        
    }
    
}
