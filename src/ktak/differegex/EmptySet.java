package ktak.differegex;

import java.util.Comparator;

import ktak.immutablejava.Function;

class EmptySet<CharType> extends Regex<CharType> {
    
    @Override
    protected boolean matchesEmptyString() {
        return false;
    }
    
    @Override
    protected Partition<CharType> partition(Comparator<CharType> cmp) {
        return Partition.trivial(cmp);
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
        return emptySetCase.apply(this);
    }
    
    @Override
    protected <R> R matchEmptySet(
            Function<EmptySet<CharType>,R> emptySetCase,
            Function<Regex<CharType>,R> otherwise) {
        return emptySetCase.apply(this);
    }
    
    @Override
    protected Regex<CharType> differentiate(CharType matchChar, Comparator<CharType> cmp) {
        return this;
    }
    
    @Override
    protected Regex<CharType> nullDerivative() {
        return this;
    }
    
    @Override
    protected Regex<CharType> normalize(RegexComparator<CharType> cmp) {
        return this;
    }
    
}
