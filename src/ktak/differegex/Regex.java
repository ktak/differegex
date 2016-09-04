package ktak.differegex;

import java.util.Comparator;

import ktak.immutablejava.Function;

public abstract class Regex<CharType> {
    
    protected abstract <R> R match(
            Function<EmptySet<CharType>,R> emptySetCase,
            Function<EmptyString<CharType>,R> emptyStringCase,
            Function<SingleChar<CharType>,R> singleCharCase,
            Function<Sequence<CharType>,R> sequenceCase,
            Function<Alternation<CharType>,R> alternationCase,
            Function<ZeroOrMore<CharType>,R> zeroOrMoreCase,
            Function<Conjunction<CharType>,R> conjunctionCase,
            Function<Negation<CharType>,R> negationCase,
            Function<AnyChar<CharType>,R> anyCharCase);
    
    protected <R> R matchEmptySet(
            Function<EmptySet<CharType>,R> emptySetCase,
            Function<Regex<CharType>,R> otherwise) {
        return otherwise.apply(this);
    }
    
    protected <R> R matchEmptyString(
            Function<EmptyString<CharType>,R> emptyStringCase,
            Function<Regex<CharType>,R> otherwise) {
        return otherwise.apply(this);
    }
    
    protected <R> R matchSingleChar(
            Function<SingleChar<CharType>,R> singleCharCase,
            Function<Regex<CharType>,R> otherwise) {
        return otherwise.apply(this);
    }
    
    protected <R> R matchSequence(
            Function<Sequence<CharType>,R> sequenceCase,
            Function<Regex<CharType>,R> otherwise) {
        return otherwise.apply(this);
    }
    
    protected <R> R matchAlternation(
            Function<Alternation<CharType>,R> alternationCase,
            Function<Regex<CharType>,R> otherwise) {
        return otherwise.apply(this);
    }
    
    protected <R> R matchZeroOrMore(
            Function<ZeroOrMore<CharType>,R> zeroOrMoreCase,
            Function<Regex<CharType>,R> otherwise) {
        return otherwise.apply(this);
    }
    
    protected <R> R matchConjunction(
            Function<Conjunction<CharType>,R> conjunctionCase,
            Function<Regex<CharType>,R> otherwise) {
        return otherwise.apply(this);
    }
    
    protected <R> R matchNegation(
            Function<Negation<CharType>,R> negationCase,
            Function<Regex<CharType>,R> otherwise) {
        return otherwise.apply(this);
    }
    
    protected abstract boolean matchesEmptyString();
    
    protected abstract Partition<CharType> partition(Comparator<CharType> cmp);
    
    protected abstract Regex<CharType> differentiate(CharType matchChar, Comparator<CharType> cmp);
    
    protected abstract Regex<CharType> nullDerivative();
    
    protected abstract Regex<CharType> normalize(RegexComparator<CharType> cmp);
    
    public static <CharType> Regex<CharType> emptyString(Class<CharType> x) {
        return new EmptyString<CharType>();
    }
    
    public static <CharType> Regex<CharType> emptyString() {
        return new EmptyString<CharType>();
    }
    
    public static <CharType> Regex<CharType> singleChar(CharType matchChar) {
        return new SingleChar<CharType>(matchChar);
    }
    
    public static <CharType> Regex<CharType> anyChar(Class<CharType> x) {
        return new AnyChar<CharType>();
    }
    
    public static <CharType> Regex<CharType> anyChar() {
        return new AnyChar<CharType>();
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
    
    public Regex<CharType> oneOrMore() {
        return this.seq(this.zeroOrMore());
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
