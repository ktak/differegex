package ktak.differegex;

import java.util.Comparator;

import ktak.immutablejava.Function;

class SingleChar<CharType> extends Regex<CharType> {
    
    public final CharType matchChar;
    
    public SingleChar(CharType matchChar) { this.matchChar = matchChar; }
    
    @Override
    protected <R> R visit(Visitor<R, CharType> visitor) {
        return visitor.visitSingleChar(this);
    }
    
    @Override
    protected boolean matchesEmptyString() {
        return false;
    }
    
    @Override
    protected Partition<CharType> partition(Comparator<CharType> cmp) {
        return Partition.singleChar(cmp, matchChar);
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
        return singleCharCase.apply(this);
    }
    
    @Override
    protected Regex<CharType> differentiate(CharType matchChar, Comparator<CharType> cmp) {
        return cmp.compare(this.matchChar, matchChar) == 0 ?
                Regex.emptyString() : new EmptySet<CharType>();
    }
    
}
