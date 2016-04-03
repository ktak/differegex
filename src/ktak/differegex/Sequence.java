package ktak.differegex;

import java.util.Comparator;

class Sequence<CharType> extends Regex<CharType> {
    
    public final Regex<CharType> first;
    public final Regex<CharType> second;
    public final boolean matchesEmptyString;
    
    public Sequence(Regex<CharType> first, Regex<CharType> second) {
        this.first = first;
        this.second = second;
        matchesEmptyString = first.matchesEmptyString() && second.matchesEmptyString();
    }
    
    @Override
    protected <R> R visit(Visitor<R, CharType> visitor) {
        return visitor.visitSequence(this);
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
    
}
