package ktak.differegex;

class SingleChar<CharType> extends Regex<CharType> {
    
    public final CharType matchChar;
    
    public SingleChar(CharType matchChar) { this.matchChar = matchChar; }
    
    @Override
    protected <R> R visit(Visitor<R, CharType> visitor) {
        return visitor.visitSingleChar(this);
    }
    
    @Override
    protected boolean matchesEmptyString() {
        return false;
    }
    
}
