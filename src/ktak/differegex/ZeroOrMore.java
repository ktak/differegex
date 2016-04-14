package ktak.differegex;

import java.util.Comparator;

import ktak.immutablejava.Function;
import ktak.immutablejava.Unit;

class ZeroOrMore<CharType> extends Regex<CharType> {
    
    public final Regex<CharType> regex;
    
    public ZeroOrMore(Regex<CharType> regex) { this.regex = regex; }
    
    @Override
    protected boolean matchesEmptyString() {
        return true;
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
        return zeroOrMoreCase.apply(this);
    }
    
    @Override
    protected <R> R matchZeroOrMore(
            Function<ZeroOrMore<CharType>,R> zeroOrMoreCase,
            Function<Unit,R> otherwise) {
        return zeroOrMoreCase.apply(this);
    }
    
    @Override
    protected Regex<CharType> differentiate(CharType matchChar, Comparator<CharType> cmp) {
        return regex.differentiate(matchChar, cmp).seq(this);
    }
    
    @Override
    protected Regex<CharType> normalize(RegexComparator<CharType> cmp) {
        
        Regex<CharType> normalized = regex.normalize(cmp);
        
        return normalized.match(
                // NULL* = ''
                (emptySet) -> new EmptyString<CharType>(),
                // ''* = ''
                (emptyString) -> emptyString,
                (singleChar) -> normalized.zeroOrMore(),
                (sequence) -> normalized.zeroOrMore(),
                (alternation) -> normalized.zeroOrMore(),
                // r** = r*
                (zeroOrMore) -> zeroOrMore,
                (conjunction) -> normalized.zeroOrMore(),
                (negation) -> normalized.zeroOrMore());
        
    }
    
}
