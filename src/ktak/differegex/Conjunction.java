package ktak.differegex;

import java.util.Comparator;

import ktak.immutablejava.Function;
import ktak.immutablejava.Unit;

class Conjunction<CharType> extends Regex<CharType> {
    
    public final Regex<CharType> first;
    public final Regex<CharType> second;
    public final boolean matchesEmptyString;
    
    public Conjunction(Regex<CharType> first, Regex<CharType> second) {
        this.first = first;
        this.second = second;
        matchesEmptyString = first.matchesEmptyString() && second.matchesEmptyString();
    }
    
    @Override
    protected boolean matchesEmptyString() {
        return matchesEmptyString;
    }
    
    @Override
    protected Partition<CharType> partition(Comparator<CharType> cmp) {
        return first.partition(cmp).intersect(second.partition(cmp));
    }
    
    @Override
    protected <R> R match(
            Function<EmptySet<CharType>, R> emptySetCase,
            Function<EmptyString<CharType>, R> emptyStringCase,
            Function<SingleChar<CharType>, R> singleCharCase,
            Function<Sequence<CharType>, R> sequenceCase,
            Function<Alternation<CharType>, R> alternationCase,
            Function<ZeroOrMore<CharType>, R> zeroOrMoreCase,
            Function<Conjunction<CharType>, R> conjunctionCase,
            Function<Negation<CharType>, R> negationCase) {
        return conjunctionCase.apply(this);
    }
    
    @Override
    protected <R> R matchConjunction(
            Function<Conjunction<CharType>,R> conjunctionCase,
            Function<Unit,R> otherwise) {
        return conjunctionCase.apply(this);
    }
    
    @Override
    protected Regex<CharType> differentiate(CharType matchChar, Comparator<CharType> cmp) {
        return first.differentiate(matchChar, cmp).conj(
                second.differentiate(matchChar, cmp));
    }
    
}
