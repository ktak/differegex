package ktak.differegex;

import java.util.Comparator;

import ktak.immutablejava.Function;
import ktak.immutablejava.Unit;

class Alternation<CharType> extends Regex<CharType> {
    
    public final Regex<CharType> first;
    public final Regex<CharType> second;
    public final boolean matchesEmptyString;
    
    public Alternation(Regex<CharType> first, Regex<CharType> second) {
        this.first = first;
        this.second = second;
        matchesEmptyString = first.matchesEmptyString() || second.matchesEmptyString();
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
        return alternationCase.apply(this);
    }
    
    @Override
    protected <R> R matchAlternation(
            Function<Alternation<CharType>,R> alternationCase,
            Function<Unit,R> otherwise) {
        return alternationCase.apply(this);
    }
    
    @Override
    protected Regex<CharType> differentiate(CharType matchChar, Comparator<CharType> cmp) {
        return first.differentiate(matchChar, cmp).alt(
                second.differentiate(matchChar, cmp));
    }
    
    private Regex<CharType> orderedAlt(
            Regex<CharType> first, Regex<CharType> second, RegexComparator<CharType> cmp) {
        // order (non alternation) subcomponents to normalize symmetry
        return cmp.compare(first, second) < 0 ? first.alt(second) : second.alt(first);
        
    }
    
    private Regex<CharType> checkSecond(
            Regex<CharType> first, Regex<CharType> second, RegexComparator<CharType> cmp) {
        
        return second.match(
                // r | NULL = r
                (emptySet) -> first,
                (emptyString) -> orderedAlt(first, second, cmp),
                (singleChar) -> orderedAlt(first, second, cmp),
                (sequence) -> orderedAlt(first, second, cmp),
                // order (non alternation) subcomponents to normalize symmetry
                (alternation) -> cmp.compare(first, alternation.first) < 0 ?
                        first.alt(second) :
                        alternation.first.alt(
                                first.alt(alternation.second).normalize(cmp)),
                (zeroOrMore) -> orderedAlt(first, second, cmp),
                (conjunction) -> orderedAlt(first, second, cmp),
                (negation) -> negation.regex.matchEmptySet(
                        // r | NOT(NULL) = NOT(NULL)
                        (emptySet) -> second,
                        (unit) -> orderedAlt(first, second, cmp)));
        
    }
    
    @Override
    protected Regex<CharType> normalize(RegexComparator<CharType> cmp) {
        
        Regex<CharType> firstNormalized = first.normalize(cmp);
        Regex<CharType> secondNormalized = second.normalize(cmp);
        
        // r | r = r
        if (cmp.compare(firstNormalized, secondNormalized) == 0)
            return firstNormalized;
        
        return firstNormalized.match(
                // NULL | r = r
                (emptySet) -> secondNormalized,
                (emptyString) -> checkSecond(firstNormalized, secondNormalized, cmp),
                (singleChar) -> checkSecond(firstNormalized, secondNormalized, cmp),
                (sequence) -> checkSecond(firstNormalized, secondNormalized, cmp),
                // make alternation chains right associative to normalize associativity
                (alternation) ->
                        alternation.first.alt(
                                alternation.second.alt(secondNormalized).normalize(cmp))
                        .normalize(cmp),
                (zeroOrMore) -> checkSecond(firstNormalized, secondNormalized, cmp),
                (conjunction) -> checkSecond(firstNormalized, secondNormalized, cmp),
                (negation) -> negation.regex.matchEmptySet(
                        // NOT(NULL) | r = NOT(NULL)
                        (emptySet) -> firstNormalized,
                        (unit) -> checkSecond(firstNormalized, secondNormalized, cmp)));
        
    }
    
}
