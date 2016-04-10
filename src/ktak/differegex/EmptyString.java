package ktak.differegex;

import java.util.Comparator;

import ktak.immutablejava.Function;
import ktak.immutablejava.Unit;

class EmptyString<CharType> extends Regex<CharType> {
    
    @Override
    protected boolean matchesEmptyString() {
        return true;
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
        return emptyStringCase.apply(this);
    }
    
    @Override
    protected <R> R matchEmptyString(
            Function<EmptyString<CharType>,R> emptyStringCase,
            Function<Unit,R> otherwise) {
        return emptyStringCase.apply(this);
    }
    
    @Override
    protected Regex<CharType> differentiate(CharType matchChar, Comparator<CharType> cmp) {
        return new EmptySet<CharType>();
    }
    
}
