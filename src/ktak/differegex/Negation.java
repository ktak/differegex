package ktak.differegex;

import java.util.Comparator;

import ktak.immutablejava.Function;

class Negation<CharType> extends Regex<CharType> {
    
    public final Regex<CharType> regex;
    
    public Negation(Regex<CharType> regex) { this.regex = regex; }
    
    @Override
    protected <R> R visit(Visitor<R, CharType> visitor) {
        return visitor.visitNegation(this);
    }
    
    @Override
    protected boolean matchesEmptyString() {
        return !regex.matchesEmptyString();
    }
    
    @Override
    protected Partition<CharType> partition(Comparator<CharType> cmp) {
        return regex.partition(cmp);
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
        return negationCase.apply(this);
    }
    
    @Override
    protected Regex<CharType> differentiate(CharType matchChar, Comparator<CharType> cmp) {
        return regex.differentiate(matchChar, cmp).negate();
    }
    
}
