package ktak.differegex;

import java.util.Comparator;

import ktak.immutablejava.Function;
import ktak.immutablejava.Unit;

public abstract class Regex<CharType> {
    
    protected abstract <R> R match(
            Function<EmptySet<CharType>,R> emptySetCase,
            Function<EmptyString<CharType>,R> emptyStringCase,
            Function<SingleChar<CharType>,R> singleCharCase,
            Function<Sequence<CharType>,R> sequenceCase,
            Function<Alternation<CharType>,R> alternationCase,
            Function<ZeroOrMore<CharType>,R> zeroOrMoreCase,
            Function<Conjunction<CharType>,R> conjunctionCase,
            Function<Negation<CharType>,R> negationCase);
    
    protected <R> R matchEmptySet(
            Function<EmptySet<CharType>,R> emptySetCase,
            Function<Unit,R> otherwise) {
        return otherwise.apply(Unit.unit);
    }
    
    protected <R> R matchEmptyString(
            Function<EmptyString<CharType>,R> emptyStringCase,
            Function<Unit,R> otherwise) {
        return otherwise.apply(Unit.unit);
    }
    
    protected <R> R matchSingleChar(
            Function<SingleChar<CharType>,R> singleCharCase,
            Function<Unit,R> otherwise) {
        return otherwise.apply(Unit.unit);
    }
    
    protected <R> R matchSequence(
            Function<Sequence<CharType>,R> sequenceCase,
            Function<Unit,R> otherwise) {
        return otherwise.apply(Unit.unit);
    }
    
    protected <R> R matchAlternation(
            Function<Alternation<CharType>,R> alternationCase,
            Function<Unit,R> otherwise) {
        return otherwise.apply(Unit.unit);
    }
    
    protected <R> R matchZeroOrMore(
            Function<ZeroOrMore<CharType>,R> zeroOrMoreCase,
            Function<Unit,R> otherwise) {
        return otherwise.apply(Unit.unit);
    }
    
    protected <R> R matchConjunction(
            Function<Conjunction<CharType>,R> conjunctionCase,
            Function<Unit,R> otherwise) {
        return otherwise.apply(Unit.unit);
    }
    
    protected <R> R matchNegation(
            Function<Negation<CharType>,R> negationCase,
            Function<Unit,R> otherwise) {
        return otherwise.apply(Unit.unit);
    }
    
    protected abstract boolean matchesEmptyString();
    
    protected abstract Partition<CharType> partition(Comparator<CharType> cmp);
    
    protected abstract Regex<CharType> differentiate(CharType matchChar, Comparator<CharType> cmp);
    
    protected abstract Regex<CharType> normalize(RegexComparator<CharType> cmp);
    
    public static <CharType> Regex<CharType> emptyString() {
        return new EmptyString<CharType>();
    }
    
    public static <CharType> Regex<CharType> singleChar(CharType matchChar) {
        return new SingleChar<CharType>(matchChar);
    }
    
    public Regex<CharType> seq(Regex<CharType> second) {
        return new Sequence<CharType>(this, second);
    }
    
    public Regex<CharType> alt(Regex<CharType> second) {
        return new Alternation<CharType>(this, second);
    }
    
    public Regex<CharType> zeroOrMore() {
        return new ZeroOrMore<CharType>(this);
    }
    
    public Regex<CharType> conj(Regex<CharType> second) {
        return new Conjunction<CharType>(this, second);
    }
    
    public Regex<CharType> negate() {
        return new Negation<CharType>(this);
    }
    
    public Regex<CharType> zeroOrOne() {
        return this.alt(emptyString());
    }
    
}
