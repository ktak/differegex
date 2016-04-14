package ktak.differegex;

import java.util.Comparator;

import org.junit.Assert;
import org.junit.Test;

public class SequenceNormalizationTest {
    
    private static final Comparator<Character> charCmp =
            (c1, c2) -> c1.compareTo(c2);
    
    private static final RegexComparator<Character> regexCmp =
            new RegexComparator<Character>(charCmp);
    
    private static final Regex<Character> testRegex =
            Regex.singleChar('t')
            .seq(
                    Regex.singleChar('u').conj(Regex.singleChar('v')).zeroOrMore())
            .conj(
                    Regex.singleChar('a').seq(Regex.singleChar('b')))
            .negate()
            .alt(
                    Regex.singleChar('c').negate().alt(Regex.singleChar('d')));
    
    @Test
    public void emptySetZeroElementTest() {
        
        Assert.assertEquals(0, regexCmp.compare(
                testRegex.seq(new EmptySet<Character>()).normalize(regexCmp),
                new EmptySet<Character>()));
        
        Assert.assertEquals(0, regexCmp.compare(
                new EmptySet<Character>().seq(testRegex).normalize(regexCmp),
                new EmptySet<Character>()));
        
    }
    
    @Test
    public void emptyStringIdentityElementTest() {
        
        Assert.assertEquals(0, regexCmp.compare(
                testRegex.seq(new EmptyString<Character>()).normalize(regexCmp),
                testRegex.normalize(regexCmp)));
        
        Assert.assertEquals(0, regexCmp.compare(
                new EmptyString<Character>().seq(testRegex).normalize(regexCmp),
                testRegex.normalize(regexCmp)));
        
    }
    
    @Test
    public void rightAssociativityTest() {
        
        Regex<Character> rightAssociative =
                Regex.singleChar('a').seq(
                        Regex.singleChar('b').seq(
                                Regex.singleChar('c').seq(
                                        Regex.singleChar('d'))));
        
        Assert.assertEquals(0, regexCmp.compare(
                rightAssociative.normalize(regexCmp), rightAssociative));
        
        Assert.assertEquals(0, regexCmp.compare(
                Regex.singleChar('a')
                .seq(Regex.singleChar('b'))
                .seq(Regex.singleChar('c'))
                .seq(Regex.singleChar('d'))
                .normalize(regexCmp),
                rightAssociative));
        
        Assert.assertEquals(0, regexCmp.compare(
                Regex.singleChar('a')
                .seq(Regex.singleChar('b'))
                .seq(Regex.singleChar('c').seq(Regex.singleChar('d')))
                .normalize(regexCmp),
                rightAssociative));
        
    }
    
}
