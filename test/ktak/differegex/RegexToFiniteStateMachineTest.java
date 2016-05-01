package ktak.differegex;

import java.util.Comparator;

import org.junit.Assert;
import org.junit.Test;

import ktak.differegex.FiniteStateMachine.State;

public class RegexToFiniteStateMachineTest {
    
    private static final Comparator<Character> charCmp =
            (c1, c2) -> c1.compareTo(c2);
    private static final Comparator<String> strCmp =
            (s1, s2) -> s1.compareTo(s2);
    private static final String label = "label";
    
    @Test
    public void emptySetTest() {
        
        Regex<Character> regex = new EmptySet<Character>();
        FiniteStateMachine<Character,String> fsm =
                RegexToFiniteStateMachine.construct(regex, label, charCmp, strCmp);
        
        Assert.assertFalse(FSMRecognizesWithLabel(fsm, label, ""));
        Assert.assertFalse(FSMRecognizesWithLabel(fsm, label, "a"));
        
    }
    
    @Test
    public void emptyStringTest() {
        
        Regex<Character> regex = Regex.emptyString();
        FiniteStateMachine<Character,String> fsm =
                RegexToFiniteStateMachine.construct(regex, "label", charCmp, strCmp);
        
        Assert.assertTrue(FSMRecognizesWithLabel(fsm, label, ""));
        
        Assert.assertFalse(FSMRecognizesWithLabel(fsm, label, "a"));
        
    }
    
    @Test
    public void singleCharTest() {
        
        Regex<Character> regex = Regex.singleChar('a');
        FiniteStateMachine<Character,String> fsm =
                RegexToFiniteStateMachine.construct(regex, "label", charCmp, strCmp);
        
        Assert.assertTrue(FSMRecognizesWithLabel(fsm, label, "a"));
        
        Assert.assertFalse(FSMRecognizesWithLabel(fsm, label, ""));
        Assert.assertFalse(FSMRecognizesWithLabel(fsm, label, "aa"));
        Assert.assertFalse(FSMRecognizesWithLabel(fsm, label, "ab"));
        Assert.assertFalse(FSMRecognizesWithLabel(fsm, label, "ba"));
        
    }
    
    @Test
    public void sequenceTest() {
        
        Regex<Character> regex = stringToSeq("hello");
        FiniteStateMachine<Character,String> fsm =
                RegexToFiniteStateMachine.construct(regex, "label", charCmp, strCmp);
        
        Assert.assertTrue(FSMRecognizesWithLabel(fsm, label, "hello"));
        
        Assert.assertFalse(FSMRecognizesWithLabel(fsm, label, ""));
        Assert.assertFalse(FSMRecognizesWithLabel(fsm, label, "h"));
        Assert.assertFalse(FSMRecognizesWithLabel(fsm, label, "hell"));
        Assert.assertFalse(FSMRecognizesWithLabel(fsm, label, "hello "));
        Assert.assertFalse(FSMRecognizesWithLabel(fsm, label, "helloh"));
        Assert.assertFalse(FSMRecognizesWithLabel(fsm, label, "helloo"));
        Assert.assertFalse(FSMRecognizesWithLabel(fsm, label, "hellohello"));
        
    }
    
    @Test
    public void alternationTest() {
        
        Regex<Character> regex =
                Regex.singleChar('a')
                .alt(Regex.singleChar('b'))
                .alt(Regex.singleChar('c'));
        FiniteStateMachine<Character,String> fsm =
                RegexToFiniteStateMachine.construct(regex, "label", charCmp, strCmp);
        
        Assert.assertTrue(FSMRecognizesWithLabel(fsm, label, "a"));
        Assert.assertTrue(FSMRecognizesWithLabel(fsm, label, "b"));
        Assert.assertTrue(FSMRecognizesWithLabel(fsm, label, "c"));
        
        Assert.assertFalse(FSMRecognizesWithLabel(fsm, label, ""));
        Assert.assertFalse(FSMRecognizesWithLabel(fsm, label, "aa"));
        Assert.assertFalse(FSMRecognizesWithLabel(fsm, label, "ab"));
        Assert.assertFalse(FSMRecognizesWithLabel(fsm, label, "ba"));
        Assert.assertFalse(FSMRecognizesWithLabel(fsm, label, "cc"));
        
    }
    
    @Test
    public void zeroOrMoreTest() {
        
        Regex<Character> regex = stringToSeq("test").zeroOrMore();
        FiniteStateMachine<Character,String> fsm =
                RegexToFiniteStateMachine.construct(regex, "label", charCmp, strCmp);
        
        Assert.assertTrue(FSMRecognizesWithLabel(fsm, label, ""));
        Assert.assertTrue(FSMRecognizesWithLabel(fsm, label, "test"));
        Assert.assertTrue(FSMRecognizesWithLabel(fsm, label, "testtest"));
        Assert.assertTrue(FSMRecognizesWithLabel(fsm, label, "testtesttest"));
        
        Assert.assertFalse(FSMRecognizesWithLabel(fsm, label, "a"));
        Assert.assertFalse(FSMRecognizesWithLabel(fsm, label, "t"));
        Assert.assertFalse(FSMRecognizesWithLabel(fsm, label, "tes"));
        Assert.assertFalse(FSMRecognizesWithLabel(fsm, label, "testt"));
        
    }
    
    @Test
    public void conjunctionTest() {
        
        Regex<Character> regex =
                Regex.singleChar('a').zeroOrMore()
                .conj(stringToSeq("aa").zeroOrMore());
        FiniteStateMachine<Character,String> fsm =
                RegexToFiniteStateMachine.construct(regex, "label", charCmp, strCmp);
        
        Assert.assertTrue(FSMRecognizesWithLabel(fsm, label, ""));
        Assert.assertTrue(FSMRecognizesWithLabel(fsm, label, "aa"));
        Assert.assertTrue(FSMRecognizesWithLabel(fsm, label, "aaaa"));
        
        Assert.assertFalse(FSMRecognizesWithLabel(fsm, label, "a"));
        Assert.assertFalse(FSMRecognizesWithLabel(fsm, label, "aaa"));
        
    }
    
    @Test
    public void negationTest() {
        
        Regex<Character> regex = stringToSeq("hello").negate();
        FiniteStateMachine<Character,String> fsm =
                RegexToFiniteStateMachine.construct(regex, "label", charCmp, strCmp);
        
        Assert.assertTrue(FSMRecognizesWithLabel(fsm, label, ""));
        Assert.assertTrue(FSMRecognizesWithLabel(fsm, label, "a"));
        Assert.assertTrue(FSMRecognizesWithLabel(fsm, label, "h"));
        Assert.assertTrue(FSMRecognizesWithLabel(fsm, label, "hell"));
        Assert.assertTrue(FSMRecognizesWithLabel(fsm, label, "helloo"));
        Assert.assertTrue(FSMRecognizesWithLabel(fsm, label, "hellohello"));
        
        Assert.assertFalse(FSMRecognizesWithLabel(fsm, label, "hello"));
        
    }
    
    private Regex<Character> stringToSeq(String s) {
        
        Regex<Character> regex = Regex.emptyString();
        for (Character c : s.toCharArray()) {
            regex = regex.seq(Regex.singleChar(c));
        }
        return regex;
        
    }
    
    private boolean FSMRecognizesWithLabel(
            FiniteStateMachine<Character,String> fsm, String label, String inputString) {
        
        State<Character> state = fsm.initialState();
        for (Character c : inputString.toCharArray()) {
            state = fsm.nextState(state, c);
        }
        return fsm.isAcceptingState(state) &&
                fsm.acceptingStateLabels(state).contains(label);
        
    }
    
}
