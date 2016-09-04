package ktak.differegex;

import java.util.Comparator;

class RegexComparator<CharType> implements Comparator<Regex<CharType>> {
    
    protected final Comparator<CharType> charCmp;
    
    public RegexComparator(Comparator<CharType> charCmp) { this.charCmp = charCmp; }
    
    // defines an arbitrary ordering among the abstract syntax forms
    //  of a regex: EmptySet < EmptyString < SingleChar < Sequence <
    //  Alternation < ZeroOrMore < Conjunction < Negation < AnyChar
    @Override
    public int compare(Regex<CharType> o1, Regex<CharType> o2) {
        
        return o1.match(
                emptySet1 -> o2.match(
                        emptySet2 -> 0,
                        emptyString2 -> -1,
                        singleChar2 -> -1,
                        sequence2 -> -1,
                        alternation2 -> -1,
                        zeroOrMore2 -> -1,
                        conjunction2 -> -1,
                        negation2 -> -1,
                        anyChar2 -> -1),
                emptyString1 -> o2.match(
                        emptySet2 -> 1,
                        emptyString2 -> 0,
                        singleChar2 -> -1,
                        sequence2 -> -1,
                        alternation2 -> -1,
                        zeroOrMore2 -> -1,
                        conjunction2 -> -1,
                        negation2 -> -1,
                        anyChar2 -> -1),
                singleChar1 -> o2.match(
                        emptySet2 -> 1,
                        emptyString2 -> 1,
                        singleChar2 -> charCmp.compare(
                                singleChar1.matchChar, singleChar2.matchChar),
                        sequence2 -> -1,
                        alternation2 -> -1,
                        zeroOrMore2 -> -1,
                        conjunction2 -> -1,
                        negation2 -> -1,
                        anyChar2 -> -1),
                sequence1 -> o2.match(
                        emptySet2 -> 1,
                        emptyString2 -> 1,
                        singleChar2 -> 1,
                        sequence2 -> {
                            int firstCmp = this.compare(
                                    sequence1.first, sequence2.first);
                            return firstCmp != 0 ?
                                    firstCmp :
                                    this.compare(sequence1.second, sequence2.second);
                        },
                        alternation2 -> -1,
                        zeroOrMore2 -> -1,
                        conjunction2 -> -1,
                        negation2 -> -1,
                        anyChar2 -> -1),
                alternation1 -> o2.match(
                        emptySet2 -> 1,
                        emptyString2 -> 1,
                        singleChar2 -> 1,
                        sequence2 -> 1,
                        alternation2 -> {
                            int firstCmp = this.compare(
                                    alternation1.first, alternation2.first);
                            return firstCmp != 0 ?
                                    firstCmp :
                                    this.compare(alternation1.second, alternation2.second);
                        },
                        zeroOrMore2 -> -1,
                        conjunction2 -> -1,
                        negation2 -> -1,
                        anyChar2 -> -1),
                zeroOrMore1 -> o2.match(
                        emptySet2 -> 1,
                        emptyString2 -> 1,
                        singleChar2 -> 1,
                        sequence2 -> 1,
                        alternation2 -> 1,
                        zeroOrMore2 -> this.compare(
                                zeroOrMore1.regex, zeroOrMore2.regex),
                        conjunction2 -> -1,
                        negation2 -> -1,
                        anyChar2 -> -1),
                conjunction1 -> o2.match(
                        emptySet2 -> 1,
                        emptyString2 -> 1,
                        singleChar2 -> 1,
                        sequence2 -> 1,
                        alternation2 -> 1,
                        zeroOrMore2 -> 1,
                        conjunction2 -> {
                            int firstCmp = this.compare(
                                    conjunction1.first, conjunction2.first);
                            return firstCmp != 0 ?
                                    firstCmp :
                                    this.compare(conjunction1.second, conjunction2.second);
                        },
                        negation2 -> -1,
                        anyChar2 -> -1),
                negation1 -> o2.match(
                        emptySet2 -> 1,
                        emptyString2 -> 1,
                        singleChar2 -> 1,
                        sequence2 -> 1,
                        alternation2 -> 1,
                        zeroOrMore2 -> 1,
                        conjunction2 -> 1,
                        negation2 -> this.compare(
                                negation1.regex, negation2.regex),
                        anyChar2 -> -1),
                anyChar1 -> o2.match(
                        emptySet2 -> 1,
                        emptyString2 -> 1,
                        singleChar2 -> 1,
                        sequence2 -> 1,
                        alternation2 -> 1,
                        zeroOrMore2 -> 1,
                        conjunction2 -> 1,
                        negation2 -> 1,
                        anyChar2 -> 0));
        
    }
    
}
