package ktak.differegex;

import java.util.Comparator;

import org.junit.Assert;
import org.junit.Test;

public class RegexDifferentiateTest {
    
    private static final Comparator<Character> charCmp =
            (c1, c2) -> c1.compareTo(c2);
    
    private static final RegexComparator<Character> regexCmp =
            new RegexComparator<Character>(charCmp);
    
    private static final Regex<Character> regex1 =
            Regex.singleChar('n')
            .zeroOrMore()
            .alt(Regex.singleChar('m').conj(Regex.singleChar('o')))
            .alt(
                    Regex.singleChar('a')
                    .zeroOrMore()
                    .alt(Regex.singleChar('b').negate())
                    .conj(
                            Regex.singleChar('t')
                            .seq(Regex.singleChar('u').seq(Regex.singleChar('v')))));
    
    private static final Regex<Character> regex2 =
            Regex.singleChar('a')
            .seq(
                    Regex.singleChar('d')
                    .conj(Regex.singleChar('c'))
                    .alt(Regex.singleChar('b').negate().zeroOrMore()))
            .conj(Regex.singleChar('w').seq(Regex.singleChar('x')));
    
    @Test
    public void emptySetTest() {
        
        Assert.assertEquals(0, regexCmp.compare(
                new EmptySet<Character>().differentiate('a', charCmp),
                new EmptySet<Character>()));
        
    }
    
    @Test
    public void emptyStringTest() {
        
        Assert.assertEquals(0, regexCmp.compare(
                new EmptyString<Character>().differentiate('a', charCmp),
                new EmptySet<Character>()));
        
    }
    
    @Test
    public void singleCharTest() {
        
        Assert.assertEquals(0, regexCmp.compare(
                Regex.singleChar('a').differentiate('a', charCmp),
                new EmptyString<Character>()));
        
        Assert.assertEquals(0, regexCmp.compare(
                Regex.singleChar('a').differentiate('b', charCmp),
                new EmptySet<Character>()));
        
    }
    
    @Test
    public void sequenceTest() {
        
        Assert.assertEquals(0, regexCmp.compare(
                regex1.seq(regex2).differentiate('a', charCmp),
                regex1.differentiate('a', charCmp).seq(regex2).alt(
                        regex1.matchesEmptyString() ?
                                regex2.differentiate('a', charCmp) :
                                new EmptySet<Character>())));
        
        Assert.assertEquals(0, regexCmp.compare(
                regex1.seq(regex2).differentiate('w', charCmp),
                regex1.differentiate('w', charCmp).seq(regex2).alt(
                        regex1.matchesEmptyString() ?
                                regex2.differentiate('w', charCmp) :
                                new EmptySet<Character>())));
        
        Assert.assertEquals(0, regexCmp.compare(
                regex1.seq(regex2).differentiate('x', charCmp),
                regex1.differentiate('x', charCmp).seq(regex2).alt(
                        regex1.matchesEmptyString() ?
                                regex2.differentiate('x', charCmp) :
                                new EmptySet<Character>())));
        
        Assert.assertEquals(0, regexCmp.compare(
                regex1.seq(regex2).differentiate('z', charCmp),
                regex1.differentiate('z', charCmp).seq(regex2).alt(
                        regex1.matchesEmptyString() ?
                                regex2.differentiate('z', charCmp) :
                                new EmptySet<Character>())));
        
    }
    
    @Test
    public void alternationTest() {
        
        Assert.assertEquals(0, regexCmp.compare(
                regex1.alt(regex2).differentiate('a', charCmp),
                regex1.differentiate('a', charCmp).alt(regex2.differentiate('a', charCmp))));
        
        Assert.assertEquals(0, regexCmp.compare(
                regex1.alt(regex2).differentiate('w', charCmp),
                regex1.differentiate('w', charCmp).alt(regex2.differentiate('w', charCmp))));
        
    }
    
    @Test
    public void zeroOrMoreTest() {
        
        Assert.assertEquals(0, regexCmp.compare(
                regex1.zeroOrMore().differentiate('a', charCmp),
                regex1.differentiate('a', charCmp).seq(regex1.zeroOrMore())));
        
        Assert.assertEquals(0, regexCmp.compare(
                regex1.zeroOrMore().differentiate('b', charCmp),
                regex1.differentiate('b', charCmp).seq(regex1.zeroOrMore())));
        
    }
    
    @Test
    public void conjunctionTest() {
        
        Assert.assertEquals(0, regexCmp.compare(
                regex1.conj(regex2).differentiate('a', charCmp),
                regex1.differentiate('a', charCmp).conj(regex2.differentiate('a', charCmp))));
        
        Assert.assertEquals(0, regexCmp.compare(
                regex1.conj(regex2).differentiate('z', charCmp),
                regex1.differentiate('z', charCmp).conj(regex2.differentiate('z', charCmp))));
        
        Assert.assertEquals(0, regexCmp.compare(
                regex2.conj(regex1).differentiate('a', charCmp),
                regex2.differentiate('a', charCmp).conj(regex1.differentiate('a', charCmp))));
        
        Assert.assertEquals(0, regexCmp.compare(
                regex1.conj(regex1).differentiate('a', charCmp),
                regex1.differentiate('a', charCmp).conj(regex1.differentiate('a', charCmp))));
        
    }
    
    @Test
    public void negationTest() {
        
        Assert.assertEquals(0, regexCmp.compare(
                regex1.negate().differentiate('a', charCmp),
                regex1.differentiate('a', charCmp).negate()));
        
        Assert.assertEquals(0, regexCmp.compare(
                regex1.negate().differentiate('z', charCmp),
                regex1.differentiate('z', charCmp).negate()));
        
        Assert.assertEquals(0, regexCmp.compare(
                regex2.negate().differentiate('a', charCmp),
                regex2.differentiate('a', charCmp).negate()));
        
        Assert.assertEquals(0, regexCmp.compare(
                regex2.negate().differentiate('z', charCmp),
                regex2.differentiate('z', charCmp).negate()));
        
    }
    
    @Test
    public void anyCharTest() {
        
        Assert.assertEquals(0, regexCmp.compare(
                Regex.anyChar(Character.class).differentiate('a', charCmp),
                Regex.emptyString()));
        
        Assert.assertEquals(0, regexCmp.compare(
                Regex.anyChar(Character.class).differentiate('!', charCmp),
                Regex.emptyString()));
        
        Assert.assertEquals(0, regexCmp.compare(
                Regex.anyChar(Character.class).differentiate('.', charCmp),
                Regex.emptyString()));
        
    }
    
}
