package ktak.differegex;

import java.util.Comparator;

import ktak.immutablejava.Function;

class Sequence<CharType> extends Regex<CharType> {
    
    protected final Regex<CharType> first;
    protected final Regex<CharType> second;
    private final boolean matchesEmptyString;
    
    public Sequence(Regex<CharType> first, Regex<CharType> second) {
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
        return first.matchesEmptyString() ?
                first.partition(cmp).intersect(second.partition(cmp)) :
                first.partition(cmp);
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
            Function<Negation<CharType>, R> negationCase,
            Function<AnyChar<CharType>,R> anyCharCase) {
        return sequenceCase.apply(this);
    }
    
    @Override
    protected <R> R matchSequence(
            Function<Sequence<CharType>,R> sequenceCase,
            Function<Regex<CharType>,R> otherwise) {
        return sequenceCase.apply(this);
    }
    
    @Override
    protected Regex<CharType> differentiate(CharType matchChar, Comparator<CharType> cmp) {
        return first.differentiate(matchChar, cmp).seq(second).alt(
                first.matchesEmptyString() ?
                        second.differentiate(matchChar, cmp) : new EmptySet<CharType>());
    }
    
    @Override
    protected Regex<CharType> nullDerivative() {
        return first.nullDerivative().seq(second).alt(
                first.matchesEmptyString() ?
                        second.nullDerivative() : new EmptySet<CharType>());
    }
    
    private Regex<CharType> checkSecond(Regex<CharType> first, Regex<CharType> second) {
        
        return second.matchEmptySet(
                // r . NULL = NULL
                (emptySet) -> emptySet,
                (unit1) -> second.matchEmptyString(
                        // r . '' = r
                        (emptyString) -> first,
                        (unit2) -> first.seq(second)));
        
    }
    
    @Override
    protected Regex<CharType> normalize(RegexComparator<CharType> cmp) {
        
        Regex<CharType> firstNormalized = first.normalize(cmp);
        Regex<CharType> secondNormalized = second.normalize(cmp);
        
        return firstNormalized.match(
                // NULL . r = NULL
                (emptySet) -> emptySet,
                // '' . r = r
                (emptyString) -> secondNormalized,
                (singleChar) -> checkSecond(firstNormalized, secondNormalized),
                // make sequence chains right associative
                (sequence) ->
                        sequence.first.seq(
                                sequence.second.seq(secondNormalized).normalize(cmp))
                        .normalize(cmp),
                (alternation) -> checkSecond(firstNormalized, secondNormalized),
                (zeroOrMore) -> checkSecond(firstNormalized, secondNormalized),
                (conjunction) -> checkSecond(firstNormalized, secondNormalized),
                (negation) -> checkSecond(firstNormalized, secondNormalized),
                (anyChar) -> checkSecond(firstNormalized, secondNormalized));
        
    }
    
}
