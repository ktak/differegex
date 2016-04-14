package ktak.differegex;

import java.util.Comparator;

import org.junit.Assert;
import org.junit.Test;

public class ConjunctionNormalizationTest {
    
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
    public void idempotenceTest() {
        
        Assert.assertEquals(0, regexCmp.compare(
                testRegex.conj(testRegex).normalize(regexCmp),
                testRegex.normalize(regexCmp)));
        
    }
    
    @Test
    public void emptySetZeroElementTest() {
        
        Assert.assertEquals(0, regexCmp.compare(
                testRegex.conj(new EmptySet<Character>()).normalize(regexCmp),
                new EmptySet<Character>()));
        
        Assert.assertEquals(0, regexCmp.compare(
                new EmptySet<Character>().conj(testRegex).normalize(regexCmp),
                new EmptySet<Character>()));
        
    }
    
    @Test
    public void emptySetNegationIdentityElementTest() {
        
        Assert.assertEquals(0, regexCmp.compare(
                testRegex.conj(new EmptySet<Character>().negate()).normalize(regexCmp),
                testRegex.normalize(regexCmp)));
        
        Assert.assertEquals(0, regexCmp.compare(
                new EmptySet<Character>().negate().conj(testRegex).normalize(regexCmp),
                testRegex.normalize(regexCmp)));
        
    }
    
    @Test
    public void rightAssociativityTest() {
        
        Regex<Character> rightAssociative =
                Regex.singleChar('a').conj(
                        Regex.singleChar('b').conj(
                                Regex.singleChar('c').conj(
                                        Regex.singleChar('d'))));
        
        Assert.assertEquals(0, regexCmp.compare(
                rightAssociative.normalize(regexCmp), rightAssociative));
        
        Assert.assertEquals(0, regexCmp.compare(
                Regex.singleChar('a')
                .conj(Regex.singleChar('b'))
                .conj(Regex.singleChar('c'))
                .conj(Regex.singleChar('d'))
                .normalize(regexCmp),
                rightAssociative));
        
        Assert.assertEquals(0, regexCmp.compare(
                Regex.singleChar('c')
                .conj(Regex.singleChar('d'))
                .conj(Regex.singleChar('a').conj(Regex.singleChar('b')))
                .normalize(regexCmp),
                rightAssociative));
    }
    
    @Test
    public void subcomponentOrderingTest() {
        
        Regex<Character> regex =
                Regex.singleChar('q')
                .seq(
                        Regex.singleChar('r').alt(Regex.singleChar('s')))
                .conj(
                        Regex.singleChar('t').seq(Regex.singleChar('u')))
                .zeroOrMore()
                .alt(Regex.singleChar('v'));
        
        Regex<Character> lesser =
                regexCmp.compare(regex.normalize(regexCmp), testRegex.normalize(regexCmp)) == -1 ?
                        regex.normalize(regexCmp) : testRegex.normalize(regexCmp);
        
        Regex<Character> greater =
                regexCmp.compare(regex.normalize(regexCmp), testRegex.normalize(regexCmp)) == -1 ?
                        testRegex.normalize(regexCmp) : regex.normalize(regexCmp);
        
        Assert.assertEquals(0, regexCmp.compare(
                lesser.conj(greater).normalize(regexCmp),
                greater.conj(lesser).normalize(regexCmp)));
        
    }
    
}
