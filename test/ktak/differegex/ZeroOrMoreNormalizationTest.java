package ktak.differegex;

import java.util.Comparator;

import org.junit.Assert;
import org.junit.Test;

public class ZeroOrMoreNormalizationTest {
    
    private static final Comparator<Character> charCmp =
            (c1, c2) -> c1.compareTo(c2);
    
    private static final RegexComparator<Character> regexCmp =
            new RegexComparator<Character>(charCmp);
    
    @Test
    public void emptySetSimplificationTest() {
        
        Assert.assertEquals(0, regexCmp.compare(
                new EmptySet<Character>().zeroOrMore().normalize(regexCmp),
                new EmptyString<Character>()));
        
    }
    
    @Test
    public void emptyStringSimplificationTest() {
        
        Assert.assertEquals(0, regexCmp.compare(
                new EmptyString<Character>().zeroOrMore().normalize(regexCmp),
                new EmptyString<Character>()));
        
    }
    
    @Test
    public void idempotenceTest() {
        
        Regex<Character> regex =
                Regex.singleChar('a')
                .seq(
                        Regex.singleChar('x').alt(Regex.singleChar('y')))
                .conj(
                        Regex.singleChar('z').negate().zeroOrMore());
        
        Assert.assertEquals(0, regexCmp.compare(
                regex.zeroOrMore().zeroOrMore().zeroOrMore().normalize(regexCmp),
                regex.normalize(regexCmp).zeroOrMore()));
        
    }
    
}
