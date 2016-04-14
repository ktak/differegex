package ktak.differegex;

import java.util.Comparator;

import org.junit.Assert;
import org.junit.Test;

public class NegationNormalizationTest {
    
    private static final Comparator<Character> charCmp =
            (c1, c2) -> c1.compareTo(c2);
    
    private static final RegexComparator<Character> regexCmp =
            new RegexComparator<Character>(charCmp);
    
    @Test
    public void doubleNegationEliminationTest() {
        
        Regex<Character> regex =
                Regex.singleChar('a')
                .alt(
                        Regex.singleChar('b').seq(Regex.singleChar('c')))
                .zeroOrMore()
                .alt(
                        Regex.singleChar('x').conj(
                                Regex.singleChar('y').seq(Regex.singleChar('z'))));
        
        Assert.assertEquals(0, regexCmp.compare(
                regex.negate().negate().normalize(regexCmp),
                regex.normalize(regexCmp)));
        
    }
    
}
