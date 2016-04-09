package ktak.differegex;

import java.util.Comparator;

import org.junit.Assert;
import org.junit.Test;

public class RegexComparatorTest {
    
    private static final Comparator<Character> charCmp = new Comparator<Character>() {
        
        @Override
        public int compare(Character o1, Character o2) {
            return o1.compareTo(o2);
        }
        
    };
    
    private static final RegexComparator<Character> regexCmp =
            new RegexComparator<Character>(charCmp);
    
    private static final Regex<Character> emptySet = new EmptySet<Character>();
    private static final Regex<Character> emptyString = Regex.emptyString();
    private static final Regex<Character> char_a = Regex.singleChar('a');
    private static final Regex<Character> char_b = Regex.singleChar('b');
    private static final Regex<Character> char_c = Regex.singleChar('c');
    
    @Test
    public void emptySetTest() {
        
        Assert.assertEquals(0, regexCmp.compare(emptySet, new EmptySet<Character>()));
        Assert.assertEquals(-1, regexCmp.compare(emptySet, emptyString));
        Assert.assertEquals(-1, regexCmp.compare(emptySet, char_a));
        Assert.assertEquals(-1, regexCmp.compare(emptySet, char_a.seq(char_b)));
        Assert.assertEquals(-1, regexCmp.compare(emptySet, char_c.alt(char_b)));
        Assert.assertEquals(-1, regexCmp.compare(emptySet, char_a.zeroOrMore()));
        Assert.assertEquals(-1, regexCmp.compare(emptySet, char_a.conj(char_b)));
        Assert.assertEquals(-1, regexCmp.compare(emptySet, char_a.negate()));
        
    }
    
    @Test
    public void emptyStringTest() {
        
        Assert.assertEquals(1, regexCmp.compare(emptyString, emptySet));
        Assert.assertEquals(0, regexCmp.compare(emptyString, new EmptyString<Character>()));
        Assert.assertEquals(-1, regexCmp.compare(emptyString, char_a));
        Assert.assertEquals(-1, regexCmp.compare(emptyString, char_a.seq(char_b)));
        Assert.assertEquals(-1, regexCmp.compare(emptyString, char_c.alt(char_b)));
        Assert.assertEquals(-1, regexCmp.compare(emptyString, char_a.zeroOrMore()));
        Assert.assertEquals(-1, regexCmp.compare(emptyString, char_a.conj(char_b)));
        Assert.assertEquals(-1, regexCmp.compare(emptyString, char_a.negate()));
        
    }
    
    @Test
    public void singleCharTest() {
        
        Assert.assertEquals(1, regexCmp.compare(char_b, emptySet));
        Assert.assertEquals(1, regexCmp.compare(char_b, emptyString));
        Assert.assertEquals(1, regexCmp.compare(char_b, char_a));
        Assert.assertEquals(0, regexCmp.compare(char_b, char_b));
        Assert.assertEquals(-1, regexCmp.compare(char_b, char_c));
        Assert.assertEquals(-1, regexCmp.compare(char_b, char_a.seq(char_b)));
        Assert.assertEquals(-1, regexCmp.compare(char_b, char_c.alt(char_b)));
        Assert.assertEquals(-1, regexCmp.compare(char_b, char_a.zeroOrMore()));
        Assert.assertEquals(-1, regexCmp.compare(char_b, char_a.conj(char_b)));
        Assert.assertEquals(-1, regexCmp.compare(char_b, char_a.negate()));
        
    }
    
    @Test
    public void sequenceTest() {
        
        Assert.assertEquals(1, regexCmp.compare(
                char_b.seq(char_b.alt(char_c)), emptySet));
        Assert.assertEquals(1, regexCmp.compare(
                char_b.seq(char_b.alt(char_c)), emptyString));
        Assert.assertEquals(1, regexCmp.compare(
                char_b.seq(char_b.alt(char_c)), char_a));
        Assert.assertEquals(1, regexCmp.compare(
                char_b.seq(char_b.alt(char_c)), char_a.seq(char_b)));
        Assert.assertEquals(0, regexCmp.compare(
                char_b.seq(char_b.alt(char_c)), char_b.seq(char_b.alt(char_c))));
        Assert.assertEquals(-1, regexCmp.compare(
                char_b.seq(char_b.alt(char_c)), char_c.seq(char_b)));
        Assert.assertEquals(-1, regexCmp.compare(
                char_b.seq(char_b.alt(char_c)), char_a.alt(char_b)));
        Assert.assertEquals(-1, regexCmp.compare(
                char_b.seq(char_b.alt(char_c)), char_a.zeroOrMore()));
        Assert.assertEquals(-1, regexCmp.compare(
                char_b.seq(char_b.alt(char_c)), char_a.conj(char_b)));
        Assert.assertEquals(-1, regexCmp.compare(
                char_b.seq(char_b.alt(char_c)), char_a.negate()));
        
    }
    
    @Test
    public void alternationTest() {
        
        Assert.assertEquals(1, regexCmp.compare(char_b.alt(char_a), emptySet));
        Assert.assertEquals(1, regexCmp.compare(char_b.alt(char_a), emptyString));
        Assert.assertEquals(1, regexCmp.compare(char_b.alt(char_a), char_a));
        Assert.assertEquals(1, regexCmp.compare(char_b.alt(char_a), char_a.seq(char_b)));
        Assert.assertEquals(1, regexCmp.compare(char_b.alt(char_a), char_a.alt(char_b)));
        Assert.assertEquals(1, regexCmp.compare(
                char_b.alt(char_a.conj(char_c.alt(char_a))),
                char_b.alt(char_a.conj(char_b.alt(char_c)))));
        Assert.assertEquals(0, regexCmp.compare(char_b.alt(char_a), char_b.alt(char_a)));
        Assert.assertEquals(0, regexCmp.compare(
                char_b.alt(char_a.zeroOrMore().conj(char_c)),
                char_b.alt(char_a.zeroOrMore().conj(char_c))));
        Assert.assertEquals(-1, regexCmp.compare(
                char_b.alt(char_a.alt(char_a)), char_b.alt(char_a.alt(char_b))));
        Assert.assertEquals(-1, regexCmp.compare(char_b.alt(char_a), char_c.alt(char_a)));
        Assert.assertEquals(-1, regexCmp.compare(char_b.alt(char_a), char_a.zeroOrMore()));
        Assert.assertEquals(-1, regexCmp.compare(char_b.alt(char_a), char_a.conj(char_b)));
        Assert.assertEquals(-1, regexCmp.compare(char_b.alt(char_a), char_a.negate()));
        
    }
    
    @Test
    public void zeroOrMoreTest() {
        
        Assert.assertEquals(1, regexCmp.compare(char_b.zeroOrMore(), emptySet));
        Assert.assertEquals(1, regexCmp.compare(char_b.zeroOrMore(), emptyString));
        Assert.assertEquals(1, regexCmp.compare(char_b.zeroOrMore(), char_a));
        Assert.assertEquals(1, regexCmp.compare(char_b.zeroOrMore(), char_a.seq(char_b)));
        Assert.assertEquals(1, regexCmp.compare(char_b.zeroOrMore(), char_a.alt(char_b)));
        Assert.assertEquals(1, regexCmp.compare(char_b.zeroOrMore(), char_a.zeroOrMore()));
        Assert.assertEquals(0, regexCmp.compare(char_b.zeroOrMore(), char_b.zeroOrMore()));
        Assert.assertEquals(-1, regexCmp.compare(char_b.zeroOrMore(), char_c.zeroOrMore()));
        Assert.assertEquals(-1, regexCmp.compare(char_b.zeroOrMore(), char_a.conj(char_b)));
        Assert.assertEquals(-1, regexCmp.compare(char_b.zeroOrMore(), char_a.negate()));
        
    }
    
    @Test
    public void conjunctionTest() {
        
        Assert.assertEquals(1, regexCmp.compare(char_b.conj(char_a), emptySet));
        Assert.assertEquals(1, regexCmp.compare(char_b.conj(char_a), emptyString));
        Assert.assertEquals(1, regexCmp.compare(char_b.conj(char_a), char_a));
        Assert.assertEquals(1, regexCmp.compare(char_b.conj(char_a), char_a.seq(char_b)));
        Assert.assertEquals(1, regexCmp.compare(char_b.conj(char_a), char_a.alt(char_b)));
        Assert.assertEquals(1, regexCmp.compare(char_b.conj(char_a), char_a.zeroOrMore()));
        Assert.assertEquals(1, regexCmp.compare(char_b.conj(char_a), char_a.conj(char_b)));
        Assert.assertEquals(1, regexCmp.compare(
                char_b.conj(char_a.conj(char_a.negate()).conj(char_b.alt(char_a))),
                char_b.conj(char_a.conj(char_a.negate()).conj(char_a.alt(char_c)))));
        Assert.assertEquals(0, regexCmp.compare(char_b.conj(char_a), char_b.conj(char_a)));
        Assert.assertEquals(-1, regexCmp.compare(
                char_a.conj(char_b.seq(char_b.alt(char_a)).conj(char_a)),
                char_a.conj(char_b.seq(char_b.alt(char_a)).conj(char_b))));
        Assert.assertEquals(-1, regexCmp.compare(char_b.conj(char_a), char_c.conj(char_a)));
        Assert.assertEquals(-1, regexCmp.compare(char_b.conj(char_a), char_a.negate()));
        
    }
    
    @Test
    public void negationTest() {
        
        Assert.assertEquals(1, regexCmp.compare(char_b.negate(), emptySet));
        Assert.assertEquals(1, regexCmp.compare(char_b.negate(), emptyString));
        Assert.assertEquals(1, regexCmp.compare(char_b.negate(), char_a));
        Assert.assertEquals(1, regexCmp.compare(char_b.negate(), char_a.seq(char_b)));
        Assert.assertEquals(1, regexCmp.compare(char_b.negate(), char_a.alt(char_b)));
        Assert.assertEquals(1, regexCmp.compare(char_b.negate(), char_a.zeroOrMore()));
        Assert.assertEquals(1, regexCmp.compare(char_b.negate(), char_a.conj(char_b)));
        Assert.assertEquals(1, regexCmp.compare(char_b.negate(), char_a.negate()));
        Assert.assertEquals(0, regexCmp.compare(char_b.negate(), char_b.negate()));
        Assert.assertEquals(-1, regexCmp.compare(char_b.negate(), char_c.negate()));
        
    }
    
}
