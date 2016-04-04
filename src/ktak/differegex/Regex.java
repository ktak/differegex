package ktak.differegex;

import java.util.Comparator;

import ktak.immutablejava.Function;

public abstract class Regex<CharType> {
    
    protected abstract <R> R visit(Visitor<R,CharType> visitor);
    
    protected abstract <R> R match(
            Function<EmptySet<CharType>,R> emptySetCase,
            Function<EmptyString<CharType>,R> emptyStringCase,
            Function<SingleChar<CharType>,R> singleCharCase,
            Function<Sequence<CharType>,R> sequenceCase,
            Function<Alternation<CharType>,R> alternationCase,
            Function<ZeroOrMore<CharType>,R> zeroOrMoreCase,
            Function<Conjunction<CharType>,R> conjunctionCase,
            Function<Negation<CharType>,R> negationCase);
    
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
    
    protected abstract Partition<CharType> partition(Comparator<CharType> cmp);
    
}
