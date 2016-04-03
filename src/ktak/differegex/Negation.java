package ktak.differegex;

import java.util.Comparator;

class Negation<CharType> extends Regex<CharType> {
    
    public final Regex<CharType> regex;
    
    public Negation(Regex<CharType> regex) { this.regex = regex; }
    
    @Override
    protected <R> R visit(Visitor<R, CharType> visitor) {
        return visitor.visitNegation(this);
    }
    
    @Override
    protected boolean matchesEmptyString() {
        return !regex.matchesEmptyString();
    }
    
    @Override
    protected Partition<CharType> partition(Comparator<CharType> cmp) {
        return regex.partition(cmp);
    }
    
}
