package ktak.differegex;

import java.util.Comparator;

import org.junit.Assert;
import org.junit.Test;

import ktak.differegex.FiniteStateMachine.State;

public class RegexToFiniteStateMachineTest {
    
    private static final Comparator<Character> charCmp =
            (c1, c2) -> c1.compareTo(c2);
    
    @Test
    public void emptySetTest() {
        
        Regex<Character> regex = new EmptySet<Character>();
        FiniteStateMachine<Character> fsm = RegexToFiniteStateMachine.construct(regex, charCmp);
        
        Assert.assertFalse(FSMRecognizes(fsm, ""));
        Assert.assertFalse(FSMRecognizes(fsm, "a"));
        
    }
    
    @Test
    public void emptyStringTest() {
        
        Regex<Character> regex = Regex.emptyString();
        FiniteStateMachine<Character> fsm = RegexToFiniteStateMachine.construct(regex, charCmp);
        
        Assert.assertTrue(FSMRecognizes(fsm, ""));
        
        Assert.assertFalse(FSMRecognizes(fsm, "a"));
        
    }
    
    @Test
    public void singleCharTest() {
        
        Regex<Character> regex = Regex.singleChar('a');
        FiniteStateMachine<Character> fsm = RegexToFiniteStateMachine.construct(regex, charCmp);
        
        Assert.assertTrue(FSMRecognizes(fsm, "a"));
        
        Assert.assertFalse(FSMRecognizes(fsm, ""));
        Assert.assertFalse(FSMRecognizes(fsm, "aa"));
        Assert.assertFalse(FSMRecognizes(fsm, "ab"));
        Assert.assertFalse(FSMRecognizes(fsm, "ba"));
        
    }
    
    @Test
    public void sequenceTest() {
        
        Regex<Character> regex = stringToSeq("hello");
        FiniteStateMachine<Character> fsm = RegexToFiniteStateMachine.construct(regex, charCmp);
        
        Assert.assertTrue(FSMRecognizes(fsm, "hello"));
        
        Assert.assertFalse(FSMRecognizes(fsm, ""));
        Assert.assertFalse(FSMRecognizes(fsm, "h"));
        Assert.assertFalse(FSMRecognizes(fsm, "hell"));
        Assert.assertFalse(FSMRecognizes(fsm, "hello "));
        Assert.assertFalse(FSMRecognizes(fsm, "helloh"));
        Assert.assertFalse(FSMRecognizes(fsm, "helloo"));
        Assert.assertFalse(FSMRecognizes(fsm, "hellohello"));
        
    }
    
    @Test
    public void alternationTest() {
        
        Regex<Character> regex =
                Regex.singleChar('a')
                .alt(Regex.singleChar('b'))
                .alt(Regex.singleChar('c'));
        FiniteStateMachine<Character> fsm = RegexToFiniteStateMachine.construct(regex, charCmp);
        
        Assert.assertTrue(FSMRecognizes(fsm, "a"));
        Assert.assertTrue(FSMRecognizes(fsm, "b"));
        Assert.assertTrue(FSMRecognizes(fsm, "c"));
        
        Assert.assertFalse(FSMRecognizes(fsm, ""));
        Assert.assertFalse(FSMRecognizes(fsm, "aa"));
        Assert.assertFalse(FSMRecognizes(fsm, "ab"));
        Assert.assertFalse(FSMRecognizes(fsm, "ba"));
        Assert.assertFalse(FSMRecognizes(fsm, "cc"));
        
    }
    
    @Test
    public void zeroOrMoreTest() {
        
        Regex<Character> regex = stringToSeq("test").zeroOrMore();
        FiniteStateMachine<Character> fsm = RegexToFiniteStateMachine.construct(regex, charCmp);
        
        Assert.assertTrue(FSMRecognizes(fsm, ""));
        Assert.assertTrue(FSMRecognizes(fsm, "test"));
        Assert.assertTrue(FSMRecognizes(fsm, "testtest"));
        Assert.assertTrue(FSMRecognizes(fsm, "testtesttest"));
        
        Assert.assertFalse(FSMRecognizes(fsm, "a"));
        Assert.assertFalse(FSMRecognizes(fsm, "t"));
        Assert.assertFalse(FSMRecognizes(fsm, "tes"));
        Assert.assertFalse(FSMRecognizes(fsm, "testt"));
        
    }
    
    @Test
    public void conjunctionTest() {
        
        Regex<Character> regex =
                Regex.singleChar('a').zeroOrMore()
                .conj(stringToSeq("aa").zeroOrMore());
        FiniteStateMachine<Character> fsm = RegexToFiniteStateMachine.construct(regex, charCmp);
        
        Assert.assertTrue(FSMRecognizes(fsm, ""));
        Assert.assertTrue(FSMRecognizes(fsm, "aa"));
        Assert.assertTrue(FSMRecognizes(fsm, "aaaa"));
        
        Assert.assertFalse(FSMRecognizes(fsm, "a"));
        Assert.assertFalse(FSMRecognizes(fsm, "aaa"));
        
    }
    
    @Test
    public void negationTest() {
        
        Regex<Character> regex = stringToSeq("hello").negate();
        FiniteStateMachine<Character> fsm = RegexToFiniteStateMachine.construct(regex, charCmp);
        
        Assert.assertTrue(FSMRecognizes(fsm, ""));
        Assert.assertTrue(FSMRecognizes(fsm, "a"));
        Assert.assertTrue(FSMRecognizes(fsm, "h"));
        Assert.assertTrue(FSMRecognizes(fsm, "hell"));
        Assert.assertTrue(FSMRecognizes(fsm, "helloo"));
        Assert.assertTrue(FSMRecognizes(fsm, "hellohello"));
        
        Assert.assertFalse(FSMRecognizes(fsm, "hello"));
        
    }
    
    private Regex<Character> stringToSeq(String s) {
        
        Regex<Character> regex = Regex.emptyString();
        for (Character c : s.toCharArray()) {
            regex = regex.seq(Regex.singleChar(c));
        }
        return regex;
        
    }
    
    private boolean FSMRecognizes(FiniteStateMachine<Character> fsm, String inputString) {
        
        State<Character> state = fsm.initialState();
        for (Character c : inputString.toCharArray()) {
            state = fsm.nextState(state, c);
        }
        return fsm.isAcceptingState(state);
        
    }
    
}
