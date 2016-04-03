package ktak.differegex;

class Sequence<CharType> extends Regex<CharType> {
    
    public final Regex<CharType> first;
    public final Regex<CharType> second;
    
    public Sequence(Regex<CharType> first, Regex<CharType> second) {
        this.first = first;
        this.second = second;
    }
    
    @Override
    protected <R> R visit(Visitor<R, CharType> visitor) {
        return visitor.visitSequence(this);
    }
    
}
