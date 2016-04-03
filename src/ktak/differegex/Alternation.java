package ktak.differegex;

class Alternation<CharType> extends Regex<CharType> {
    
    public final Regex<CharType> first;
    public final Regex<CharType> second;
    
    public Alternation(Regex<CharType> first, Regex<CharType> second) {
        this.first = first;
        this.second = second;
    }
    
    @Override
    protected <R> R visit(Visitor<R, CharType> visitor) {
        return visitor.visitAlternation(this);
    }
    
}
