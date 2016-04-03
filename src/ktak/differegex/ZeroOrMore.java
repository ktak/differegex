package ktak.differegex;

class ZeroOrMore<CharType> extends Regex<CharType> {
    
    public final Regex<CharType> regex;
    
    public ZeroOrMore(Regex<CharType> regex) { this.regex = regex; }
    
    @Override
    protected <R> R visit(Visitor<R, CharType> visitor) {
        return visitor.visitZeroOrMore(this);
    }
    
    @Override
    protected boolean matchesEmptyString() {
        return true;
    }
    
}
