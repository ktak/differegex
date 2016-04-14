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
    
    private Regex<CharType> orderedConj(
            Regex<CharType> first, Regex<CharType> second, RegexComparator<CharType> cmp) {
        // order (non conjunction) subcomponents to normalize symmetry
        return cmp.compare(first, second) < 0 ? first.conj(second) : second.conj(first);
        
    }
    
    private Regex<CharType> checkSecond(
            Regex<CharType> first, Regex<CharType> second, RegexComparator<CharType> cmp) {
        
        return second.match(
                // r & NULL = NULL
                (emptySet) -> emptySet,
                (emptyString) -> orderedConj(first, second, cmp),
                (singleChar) -> orderedConj(first, second, cmp),
                (sequence) -> orderedConj(first, second, cmp),
                (alternation) -> orderedConj(first, second, cmp),
                (zeroOrMore) -> orderedConj(first, second, cmp),
                // order (non conjunction) subcomponents to normalize symmetry
                (conjunction) -> cmp.compare(first, conjunction.first) < 0 ?
                        first.conj(second) :
                        conjunction.first.conj(
                                first.conj(conjunction.second).normalize(cmp)),
                (negation) -> negation.regex.matchEmptySet(
                        // r & NOT(NULL) = r
                        (emptySet) -> first,
                        (unit) -> orderedConj(first, second, cmp)));
        
    }
    
    @Override
    protected Regex<CharType> normalize(RegexComparator<CharType> cmp) {
        
        Regex<CharType> firstNormalized = first.normalize(cmp);
        Regex<CharType> secondNormalized = second.normalize(cmp);
        
        // r & r = r
        if (cmp.compare(firstNormalized, secondNormalized) == 0)
            return firstNormalized;
        
        return firstNormalized.match(
                // NULL & r = NULL
                (emptySet) -> emptySet,
                (emptyString) -> checkSecond(firstNormalized, secondNormalized, cmp),
                (singleChar) -> checkSecond(firstNormalized, secondNormalized, cmp),
                (sequence) -> checkSecond(firstNormalized, secondNormalized, cmp),
                (alternation) -> checkSecond(firstNormalized, secondNormalized, cmp),
                (zeroOrMore) -> checkSecond(firstNormalized, secondNormalized, cmp),
                // make conjunction chains right associative to normalize associativity
                (conjunction) ->
                        conjunction.first.conj(
                                conjunction.second.conj(secondNormalized).normalize(cmp))
                        .normalize(cmp),
                (negation) -> negation.regex.matchEmptySet(
                        // NOT(NULL) & r = r
                        (emptySet) -> secondNormalized,
                        (unit) -> checkSecond(firstNormalized, secondNormalized, cmp)));
        
    }
    
}
