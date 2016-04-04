package ktak.differegex;

import java.util.Comparator;

import ktak.immutablejava.Function;

class EmptyString<CharType> extends Regex<CharType> {
    
    @Override
    protected <R> R visit(Visitor<R, CharType> visitor) {
        return visitor.visitEmptyString(this);
    }
    
    @Override
    protected boolean matchesEmptyString() {
        return true;
    }
    
    @Override
    protected Partition<CharType> partition(Comparator<CharType> cmp) {
        return Partition.trivial(cmp);
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
        return emptyStringCase.apply(this);
    }
    
}
