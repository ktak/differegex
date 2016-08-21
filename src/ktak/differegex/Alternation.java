package ktak.differegex;

import java.util.Comparator;

import ktak.immutablejava.Function;

class Alternation<CharType> extends Regex<CharType> {
    
    protected final Regex<CharType> first;
    protected final Regex<CharType> second;
    private final boolean matchesEmptyString;
    
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
            Function<Regex<CharType>,R> otherwise) {
        return alternationCase.apply(this);
    }
    
    @Override
    protected Regex<CharType> differentiate(CharType matchChar, Comparator<CharType> cmp) {
        return first.differentiate(matchChar, cmp).alt(
                second.differentiate(matchChar, cmp));
    }
    
    @Override
    protected Regex<CharType> nullDerivative() {
        return first.nullDerivative().alt(second.nullDerivative());
    }
    
    private static <CharType> Regex<CharType> normalizeZeroElement(
            Regex<CharType> first, Regex<CharType> second) {
        
        return first.matchNegation(
                (neg1) -> neg1.regex.matchEmptySet(
                        // NOT(NULL) | r = NOT(NULL)
                        (emptySet) -> first,
                        (unit1) -> second.matchNegation(
                                (neg2) -> neg2.regex.matchEmptySet(
                                        // r | NOT(NULL) = NOT(NULL)
                                        (emptySet) -> second,
                                        (unit2) -> first.alt(second)),
                                (unit2) -> first.alt(second))),
                (unit1) -> second.matchNegation(
                        (neg2) -> neg2.regex.matchEmptySet(
                                // r | NOT(NULL) = NOT(NULL)
                                (emptySet) -> second,
                                (unit2) -> first.alt(second)),
                        (unit2) -> first.alt(second)));
        
    }
    
    private static <CharType> Regex<CharType> normalizeUnitElement(
            Regex<CharType> first, Regex<CharType> second) {
        
        return first.matchEmptySet(
                // NULL | r = r
                (emptySet) -> second,
                (unit1) -> second.matchEmptySet(
                        // r | NULL = r
                        (emptySet) -> first,
                        (unit2) -> first.alt(second)));
        
    }
    
    private static <CharType> Regex<CharType> normalizeAssociativity(
            Regex<CharType> first, Regex<CharType> second, RegexComparator<CharType> cmp) {
        
        return first.matchAlternation(
                (alt1) ->
                    // make alternation right associative
                    alt1.first.alt(
                            alt1.second.alt(second).normalize(cmp))
                    .normalize(cmp),
                (unit) -> first.alt(second));
        
    }
    
    private static <CharType> Regex<CharType> normalizeSymmetry(
            Regex<CharType> first, Regex<CharType> second, RegexComparator<CharType> cmp) {
        
        return second.matchAlternation(
                (alt2) -> cmp.compare(first, alt2.first) < 0 ?
                        first.alt(second) :
                        alt2.first.alt(
                                first.alt(alt2.second).normalize(cmp)),
                (unit) -> cmp.compare(first, second) < 0 ?
                        first.alt(second) :
                        second.alt(first));
        
    }
    
    private Regex<CharType> normalizeIdempotence(
            Regex<CharType> first, Regex<CharType> second, RegexComparator<CharType> cmp) {
        
        return cmp.compare(first, second) == 0 ?
                // r | r = r
                first :
                second.matchAlternation(
                        (alt2) -> cmp.compare(first, alt2.first) == 0 ?
                                // r | (r | x) = r | x
                                second :
                                first.alt(second),
                        (unit) -> first.alt(second));
        
    }
    
    @Override
    protected Regex<CharType> normalize(RegexComparator<CharType> cmp) {
        
        Regex<CharType> firstNormalized = first.normalize(cmp);
        Regex<CharType> secondNormalized = second.normalize(cmp);
        
        return normalizeZeroElement(firstNormalized, secondNormalized).matchAlternation(
                (alt1) -> normalizeUnitElement(alt1.first, alt1.second).matchAlternation(
                        (alt2) -> normalizeAssociativity(alt2.first, alt2.second, cmp).matchAlternation(
                                (alt3) -> normalizeSymmetry(alt3.first, alt3.second, cmp).matchAlternation(
                                        (alt4) -> normalizeIdempotence(alt4.first, alt4.second, cmp),
                                        (regex) -> regex),
                                (regex) -> regex),
                        (regex) -> regex),
                (regex) -> regex);
        
    }
    
}
