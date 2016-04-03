package ktak.differegex;

import java.util.Comparator;

class EmptySet<CharType> extends Regex<CharType> {
    
    @Override
    protected <R> R visit(Visitor<R, CharType> visitor) {
        return visitor.visitEmptySet(this);
    }
    
    @Override
    protected boolean matchesEmptyString() {
        return false;
    }
    
    @Override
    protected Partition<CharType> partition(Comparator<CharType> cmp) {
        return Partition.trivial(cmp);
    }
    
}
