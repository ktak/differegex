package ktak.differegex;

import org.junit.Assert;
import org.junit.Test;

public class RegexMatchTest {
    
    @Test
    public void emptySetTest() {
        
        Assert.assertTrue(new EmptySet<Character>().matchEmptySet(
                (emptySet) -> true, (unit) -> false));
        
    }
    
    @Test
    public void emptyStringTest() {
        
        Assert.assertTrue(Regex.emptyString().matchEmptyString(
                (emptyString) -> true, (unit) -> false));
        
    }
    
    @Test
    public void singleCharTest() {
        
        Assert.assertTrue(Regex.singleChar('a').matchSingleChar(
                (singleChar) -> true, (unit) -> false));
        
    }
    
    @Test
    public void sequenceTest() {
        
        Assert.assertTrue(Regex.singleChar('a').seq(Regex.singleChar('b')).matchSequence(
                (sequence) -> true, (unit) -> false));
        
    }
    
    @Test
    public void alternationTest() {
        
        Assert.assertTrue(Regex.singleChar('a').alt(Regex.singleChar('b')).matchAlternation(
                (alternation) -> true, (unit) -> false));
        
    }
    
    @Test
    public void zeroOrMoreTest() {
        
        Assert.assertTrue(Regex.singleChar('a').zeroOrMore().matchZeroOrMore(
                (zeroOrMore) -> true, (unit) -> false));
        
    }
    
    @Test
    public void conjunctionTest() {
        
        Assert.assertTrue(Regex.singleChar('a').conj(Regex.singleChar('b')).matchConjunction(
                (conjunction) -> true, (unit) -> false));
        
    }
    
    @Test
    public void negationTest() {
        
        Assert.assertTrue(Regex.singleChar('a').negate().matchNegation(
                (negation) -> true, (unit) -> false));
        
    }
    
}
