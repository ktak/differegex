package ktak.differegex;

import org.junit.Assert;
import org.junit.Test;

import ktak.differegex.FiniteStateMachine.State;
import ktak.immutablejava.Eq;
import ktak.immutablejava.List;

public class LexerTest {
    
    private static final Regex<Character> digit = digitRegex();
    private static final Regex<Character> lower = lowerLetterRegex();
    private static final Regex<Character> upper = upperLetterRegex();
    private static final Eq<String> strEq = (s1, s2) -> s1.equals(s2);
    
    private static Regex<Character> digitRegex() {
        return Regex.singleChar('0')
                .alt(Regex.singleChar('1'))
                .alt(Regex.singleChar('2'))
                .alt(Regex.singleChar('3'))
                .alt(Regex.singleChar('4'))
                .alt(Regex.singleChar('5'))
                .alt(Regex.singleChar('6'))
                .alt(Regex.singleChar('7'))
                .alt(Regex.singleChar('8'))
                .alt(Regex.singleChar('9'));
    }
    
    private static Regex<Character> lowerLetterRegex() {
        return Regex.singleChar('a')
                .alt(Regex.singleChar('b'))
                .alt(Regex.singleChar('c'))
                .alt(Regex.singleChar('d'))
                .alt(Regex.singleChar('e'))
                .alt(Regex.singleChar('f'))
                .alt(Regex.singleChar('g'))
                .alt(Regex.singleChar('h'))
                .alt(Regex.singleChar('i'))
                .alt(Regex.singleChar('j'))
                .alt(Regex.singleChar('k'))
                .alt(Regex.singleChar('l'))
                .alt(Regex.singleChar('m'))
                .alt(Regex.singleChar('n'))
                .alt(Regex.singleChar('o'))
                .alt(Regex.singleChar('p'))
                .alt(Regex.singleChar('q'))
                .alt(Regex.singleChar('r'))
                .alt(Regex.singleChar('s'))
                .alt(Regex.singleChar('t'))
                .alt(Regex.singleChar('u'))
                .alt(Regex.singleChar('v'))
                .alt(Regex.singleChar('w'))
                .alt(Regex.singleChar('x'))
                .alt(Regex.singleChar('y'))
                .alt(Regex.singleChar('z'));
    }
    
    private static Regex<Character> upperLetterRegex() {
        return Regex.singleChar('A')
                .alt(Regex.singleChar('B'))
                .alt(Regex.singleChar('C'))
                .alt(Regex.singleChar('D'))
                .alt(Regex.singleChar('E'))
                .alt(Regex.singleChar('F'))
                .alt(Regex.singleChar('G'))
                .alt(Regex.singleChar('H'))
                .alt(Regex.singleChar('I'))
                .alt(Regex.singleChar('J'))
                .alt(Regex.singleChar('K'))
                .alt(Regex.singleChar('L'))
                .alt(Regex.singleChar('M'))
                .alt(Regex.singleChar('N'))
                .alt(Regex.singleChar('O'))
                .alt(Regex.singleChar('P'))
                .alt(Regex.singleChar('Q'))
                .alt(Regex.singleChar('R'))
                .alt(Regex.singleChar('S'))
                .alt(Regex.singleChar('T'))
                .alt(Regex.singleChar('U'))
                .alt(Regex.singleChar('V'))
                .alt(Regex.singleChar('W'))
                .alt(Regex.singleChar('X'))
                .alt(Regex.singleChar('Y'))
                .alt(Regex.singleChar('Z'));
    }
    
    private static Regex<Character> identifierRegex() {
        return upper.alt(lower).seq(upper.alt(lower).alt(digit).zeroOrMore());
    }
    
    private static Regex<Character> lambdaRegex() {
        return Regex.singleChar('\\');
    }
    
    private static Regex<Character> dotRegex() {
        return Regex.singleChar('.');
    }
    
    private static Regex<Character> whitespaceRegex() {
        return Regex.singleChar(' ')
                .alt(Regex.singleChar('\t'))
                .alt(Regex.singleChar('\n'))
                .oneOrMore();
    }
    
    private static RegularVector<Character,String> lambdaCalculusTokens() {
        return new RegularVector<Character,String>()
                .addRegex(identifierRegex(), "id")
                .addRegex(lambdaRegex(), "lam")
                .addRegex(dotRegex(), "dot")
                .addRegex(whitespaceRegex(), "ws");
    }
    
    private static final FiniteStateMachine<Character,String> lexer =
            RegexToFiniteStateMachine.construct(
                    lambdaCalculusTokens(),
                    (c1, c2) -> c1.compareTo(c2),
                    (s1, s2) -> s1.compareTo(s2));
    
    private boolean FSMRecognizesWithLabel(
            FiniteStateMachine<Character,String> fsm, String label, String inputString) {
        
        State<Character> state = fsm.initialState();
        for (Character c : inputString.toCharArray()) {
            state = fsm.nextState(state, c);
        }
        return fsm.isAcceptingState(state) &&
                fsm.acceptingStateLabels(state).contains(label);
        
    }
    
    private List<String> lex(String input) {
        
        List<String> tokens = new List.Nil<String>();
        State<Character> state = lexer.initialState();
        char[] chars = input.toCharArray();
        for (int i=0; i < chars.length; i++) {
            state = lexer.nextState(state, chars[i]);
            if (lexer.isAcceptingState(state)) {
                // find longest match
                State<Character> lastAcceptState = state;
                for (int j=i+1; j < chars.length; j++) {
                    state = lexer.nextState(state, chars[j]);
                    if (lexer.isAcceptingState(state)) {
                        lastAcceptState = state;
                        i++;
                    } else {
                        break;
                    }
                }
                tokens = tokens.cons(lexer.acceptingStateLabels(lastAcceptState)
                        .sortedList().match(
                                (unit) -> { throw new RuntimeException(); },
                                (cons) -> cons.left));
                state = lexer.initialState();
            }
        }
        return tokens.reverse();
        
    }
    
    @Test
    public void identifierTest() {
        String input = "a";
        Assert.assertEquals(true, FSMRecognizesWithLabel(lexer, "id", input));
        Assert.assertEquals(false, FSMRecognizesWithLabel(lexer, "lam", input));
        Assert.assertEquals(false, FSMRecognizesWithLabel(lexer, "dot", input));
        Assert.assertEquals(false, FSMRecognizesWithLabel(lexer, "ws", input));
    }
    
    @Test
    public void lambdaTest() {
        String input = "\\";
        Assert.assertEquals(false, FSMRecognizesWithLabel(lexer, "id", input));
        Assert.assertEquals(true, FSMRecognizesWithLabel(lexer, "lam", input));
        Assert.assertEquals(false, FSMRecognizesWithLabel(lexer, "dot", input));
        Assert.assertEquals(false, FSMRecognizesWithLabel(lexer, "ws", input));
    }
    
    @Test
    public void dotTest() {
        String input = ".";
        Assert.assertEquals(false, FSMRecognizesWithLabel(lexer, "id", input));
        Assert.assertEquals(false, FSMRecognizesWithLabel(lexer, "lam", input));
        Assert.assertEquals(true, FSMRecognizesWithLabel(lexer, "dot", input));
        Assert.assertEquals(false, FSMRecognizesWithLabel(lexer, "ws", input));
    }
    
    @Test
    public void whitespaceTest() {
        String input = " ";
        Assert.assertEquals(false, FSMRecognizesWithLabel(lexer, "id", input));
        Assert.assertEquals(false, FSMRecognizesWithLabel(lexer, "lam", input));
        Assert.assertEquals(false, FSMRecognizesWithLabel(lexer, "dot", input));
        Assert.assertEquals(true, FSMRecognizesWithLabel(lexer, "ws", input));
    }
    
    @Test
    public void invalidTokensTest() {
        Assert.assertTrue(
                new List.Nil<String>().equalTo(
                        lex("+"), strEq));
        Assert.assertTrue(
                new List.Nil<String>().equalTo(
                        lex("~"), strEq));
        Assert.assertTrue(
                new List.Nil<String>().equalTo(
                        lex("?"), strEq));
    }
    
    @Test
    public void tokenizeTest1() {
        Assert.assertTrue(
                new List.Nil<String>()
                .cons("id").cons("ws").cons("id").equalTo(
                        lex("a \t \n\na"), strEq));
    }
    
    @Test
    public void tokenizeTest2() {
        Assert.assertTrue(
                new List.Nil<String>()
                .cons("id").cons("ws").cons("id").cons("dot")
                .cons("id").cons("lam").cons("ws").cons("dot")
                .cons("ws").cons("id").cons("ws").cons("lam").equalTo(
                        lex("\\ x . \\y.x y"), strEq));
    }
    
    @Test
    public void partialMatchTest() {
        Assert.assertTrue(
                new List.Nil<String>()
                .cons("id").cons("ws").cons("dot").cons("ws")
                .cons("id").cons("ws").cons("lam").equalTo(
                        lex("\\ x . x?"), strEq));
    }
    
}
