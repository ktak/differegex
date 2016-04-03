package ktak.differegex;

class EmptyString<CharType> extends Regex<CharType> {
    
    @Override
    protected <R> R visit(Visitor<R, CharType> visitor) {
        return visitor.visitEmptyString(this);
    }
    
}
