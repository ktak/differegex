package ktak.differegex;

public abstract class Regex<CharType> {
    
    protected abstract <R> R visit(Visitor<R,CharType> visitor);
    
    protected interface Visitor<R,CharType> {
        R visitEmptySet(EmptySet<CharType> emptySet);
        R visitEmptyString(EmptyString<CharType> emptyString);
        R visitSingleChar(SingleChar<CharType> singleChar);
        R visitSequence(Sequence<CharType> sequence);
        R visitAlternation(Alternation<CharType> alternation);
        R visitZeroOrMore(ZeroOrMore<CharType> zeroOrMore);
        R visitConjunction(Conjunction<CharType> conjunction);
        R visitNegation(Negation<CharType> negation);
    }
    
    protected abstract boolean matchesEmptyString();
    
}
