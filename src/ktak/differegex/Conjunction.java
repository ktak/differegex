package ktak.differegex;

import java.util.Comparator;

import ktak.immutablejava.Function;

class Conjunction<CharType> extends Regex<CharType> {
    
    protected final Regex<CharType> first;
    protected final Regex<CharType> second;
    private final boolean matchesEmptyString;
    
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
            Function<Regex<CharType>,R> otherwise) {
        return conjunctionCase.apply(this);
    }
    
    @Override
    protected Regex<CharType> differentiate(CharType matchChar, Comparator<CharType> cmp) {
        return first.differentiate(matchChar, cmp).conj(
                second.differentiate(matchChar, cmp));
    }
    
    @Override
    protected Regex<CharType> nullDerivative() {
        return first.nullDerivative().conj(second.nullDerivative());
    }
    
    private static <CharType> Regex<CharType> normalizeZeroElement(
            Regex<CharType> first, Regex<CharType> second) {
        
        return first.matchEmptySet(
                // NULL & r = NULL
                (emptySet) -> emptySet,
                (unit1) -> second.matchEmptySet(
                        // r & NULL = NULL
                        (emptySet) -> emptySet,
                        (unit2) -> first.conj(second)));
        
    }
    
    private static <CharType> Regex<CharType> normalizeUnitElement(
            Regex<CharType> first, Regex<CharType> second) {
        
        return first.matchNegation(
                (neg1) -> neg1.regex.matchEmptySet(
                        // NOT(NULL) & r = r
                        (emptySet) -> second,
                        (unit1) -> second.matchNegation(
                                (neg2) -> neg2.regex.matchEmptySet(
                                        // r & NOT(NULL) = r
                                        (emptySet) -> first,
                                        (unit2) -> first.conj(second)),
                                (unit2) -> first.conj(second))),
                (unit1) -> second.matchNegation(
                        (neg) -> neg.regex.matchEmptySet(
                                // r & NOT(NULL) = r
                                (emptySet) -> first,
                                (unit2) -> first.conj(second)),
                        (unit2) -> first.conj(second)));
        
    }
    
    private static <CharType> Regex<CharType> normalizeAssociativity(
            Regex<CharType> first, Regex<CharType> second, RegexComparator<CharType> cmp) {
        
        return first.matchConjunction(
                (conj1) ->
                    // make conjunction right associative
                    conj1.first.conj(
                            conj1.second.conj(second).normalize(cmp))
                    .normalize(cmp),
                (unit) -> first.conj(second));
        
    }
    
    private static <CharType> Regex<CharType> normalizeSymmetry(
            Regex<CharType> first, Regex<CharType> second, RegexComparator<CharType> cmp) {
        
        return second.matchConjunction(
                (conj2) -> cmp.compare(first, conj2.first) < 0 ?
                        first.conj(second) :
                        conj2.first.conj(
                                first.conj(conj2.second).normalize(cmp)),
                (unit) -> cmp.compare(first, second) < 0 ?
                        first.conj(second) :
                        second.conj(first));
        
    }
    
    private Regex<CharType> normalizeIdempotence(
            Regex<CharType> first, Regex<CharType> second, RegexComparator<CharType> cmp) {
        
        return cmp.compare(first, second) == 0 ?
                // r & r = r
                first :
                second.matchConjunction(
                        (conj2) -> cmp.compare(first, conj2.first) == 0 ?
                                // r & (r & x) = r & x
                                second :
                                first.conj(second),
                        (unit) -> first.conj(second));
        
    }
    
    @Override
    protected Regex<CharType> normalize(RegexComparator<CharType> cmp) {
        
        Regex<CharType> firstNormalized = first.normalize(cmp);
        Regex<CharType> secondNormalized = second.normalize(cmp);
        
        return normalizeZeroElement(firstNormalized, secondNormalized).matchConjunction(
                (conj1) -> normalizeUnitElement(conj1.first, conj1.second).matchConjunction(
                        (conj2) -> normalizeAssociativity(conj2.first, conj2.second, cmp).matchConjunction(
                                (conj3) -> normalizeSymmetry(conj3.first, conj3.second, cmp).matchConjunction(
                                        (conj4) -> normalizeIdempotence(conj4.first, conj4.second, cmp),
                                        (regex) -> regex),
                                (regex) -> regex),
                        (regex) -> regex),
                (regex) -> regex);
        
    }
    
}
