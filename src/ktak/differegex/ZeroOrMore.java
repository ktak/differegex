package ktak.differegex;

import java.util.Comparator;

import ktak.immutablejava.Function;

class ZeroOrMore<CharType> extends Regex<CharType> {
    
    public final Regex<CharType> regex;
    
    public ZeroOrMore(Regex<CharType> regex) { this.regex = regex; }
    
    @Override
    protected <R> R visit(Visitor<R, CharType> visitor) {
        return visitor.visitZeroOrMore(this);
    }
    
    @Override
    protected boolean matchesEmptyString() {
        return true;
    }
    
    @Override
    protected Partition<CharType> partition(Comparator<CharType> cmp) {
        return regex.partition(cmp);
    }
    
    @Override
    protected <R> R match(Function<EmptySet<CharType>, R> emptySetCase,
            Function<EmptyString<CharType>, R> emptyStringCase,
            Function<SingleChar<CharType>, R> singleCharCase,
            Function<Sequence<CharType>, R> sequenceCase,
            Function<Alternation<CharType>, R> alternationCase,
            Function<ZeroOrMore<CharType>, R> zeroOrMoreCase,
            Function<Conjunction<CharType>, R> conjunctionCase,
            Function<Negation<CharType>, R> negationCase) {
        return zeroOrMoreCase.apply(this);
    }
    
}
