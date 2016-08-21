package ktak.differegex;

import java.util.Comparator;

import org.junit.Assert;
import org.junit.Test;

public class AlternationNormalizationTest {
    
    private static final Comparator<Character> charCmp =
            (c1, c2) -> c1.compareTo(c2);
    
    private static final RegexComparator<Character> regexCmp =
            new RegexComparator<Character>(charCmp);
    
    Regex<Character> testRegex =
            Regex.singleChar('a')
            .seq(
                    Regex.singleChar('b').alt(Regex.singleChar('c')))
            .negate()
            .zeroOrMore()
            .conj(
                    Regex.singleChar('x')
                    .seq(Regex.singleChar('y'))
                    .seq(Regex.singleChar('z')));
    
    @Test
    public void idempotenceTest() {
        
        Assert.assertEquals(0, regexCmp.compare(
                testRegex.alt(testRegex).normalize(regexCmp),
                testRegex.normalize(regexCmp)));
        
        Assert.assertEquals(0, regexCmp.compare(
                Regex.singleChar('b')
                .alt(Regex.singleChar('a'))
                .alt(Regex.singleChar('b').alt(
                        Regex.singleChar('c'))
                        .alt(Regex.singleChar('a')))
                .normalize(regexCmp),
                Regex.singleChar('a').alt(
                        Regex.singleChar('b').alt(
                                Regex.singleChar('c')))));
        
    }
    
    @Test
    public void emptySetIdentityElementTest() {
        
        Assert.assertEquals(0, regexCmp.compare(
                testRegex.alt(new EmptySet<Character>()).normalize(regexCmp),
                testRegex.normalize(regexCmp)));
        
        Assert.assertEquals(0, regexCmp.compare(
                new EmptySet<Character>().alt(testRegex).normalize(regexCmp),
                testRegex.normalize(regexCmp)));
        
    }
    
    @Test
    public void emptySetNegationZeroElementTest() {
        
        Assert.assertEquals(0, regexCmp.compare(
                testRegex.alt(new EmptySet<Character>().negate()).normalize(regexCmp),
                new EmptySet<Character>().negate()));
        
        Assert.assertEquals(0, regexCmp.compare(
                new EmptySet<Character>().negate().alt(testRegex).normalize(regexCmp),
                new EmptySet<Character>().negate()));
        
    }
    
    @Test
    public void rightAssociativityTest() {
        
        Regex<Character> rightAssociative =
                Regex.singleChar('a').alt(
                        Regex.singleChar('b').alt(
                                Regex.singleChar('c').alt(
                                        Regex.singleChar('d'))));
        
        Assert.assertEquals(0, regexCmp.compare(
                rightAssociative.normalize(regexCmp), rightAssociative));
        
        Assert.assertEquals(0, regexCmp.compare(
                Regex.singleChar('a')
                .alt(Regex.singleChar('b'))
                .alt(Regex.singleChar('c'))
                .alt(Regex.singleChar('d'))
                .normalize(regexCmp),
                rightAssociative));
        
        Assert.assertEquals(0, regexCmp.compare(
                Regex.singleChar('c')
                .alt(Regex.singleChar('d'))
                .alt(Regex.singleChar('a').alt(Regex.singleChar('b')))
                .normalize(regexCmp),
                rightAssociative));
        
    }
    
    @Test
    public void subComponentOrderingTest() {
        
        Regex<Character> regex =
                Regex.singleChar('x')
                .alt(
                        Regex.singleChar('y').zeroOrMore().conj(Regex.singleChar('z').negate()))
                .seq(
                        Regex.singleChar('a').seq(Regex.singleChar('b')));
        
        Regex<Character> lesser =
                regexCmp.compare(regex.normalize(regexCmp), testRegex.normalize(regexCmp)) == -1 ?
                        regex.normalize(regexCmp) : testRegex.normalize(regexCmp);
        
        Regex<Character> greater =
                regexCmp.compare(regex.normalize(regexCmp), testRegex.normalize(regexCmp)) == -1 ?
                        testRegex.normalize(regexCmp) : regex.normalize(regexCmp);
        
        Assert.assertEquals(0, regexCmp.compare(
                lesser.alt(greater).normalize(regexCmp),
                greater.alt(lesser).normalize(regexCmp)));
        
    }
    
}
