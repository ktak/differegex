package ktak.differegex;

class EmptySet<CharType> extends Regex<CharType> {
    
    @Override
    protected <R> R visit(Visitor<R, CharType> visitor) {
        return visitor.visitEmptySet(this);
    }
    
    @Override
    protected boolean matchesEmptyString() {
        return false;
    }
    
}
