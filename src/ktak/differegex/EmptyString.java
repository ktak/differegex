package ktak.differegex;

import java.util.Comparator;

class EmptyString<CharType> extends Regex<CharType> {
    
    @Override
    protected <R> R visit(Visitor<R, CharType> visitor) {
        return visitor.visitEmptyString(this);
    }
    
    @Override
    protected boolean matchesEmptyString() {
        return true;
    }
    
    @Override
    protected Partition<CharType> partition(Comparator<CharType> cmp) {
        return Partition.trivial(cmp);
    }
    
}
