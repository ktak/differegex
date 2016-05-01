package ktak.differegex;

import java.util.Comparator;

import ktak.immutablejava.AATreeSet;
import ktak.immutablejava.List;
import ktak.immutablejava.Option;
import ktak.immutablejava.Tuple;

public class RegularVector<Ch,Lbl> {
    
    private final List<Tuple<Regex<Ch>,Lbl>> vec;
    
    public RegularVector() {
        this.vec = new List.Nil<>();
    }
    
    private RegularVector(List<Tuple<Regex<Ch>,Lbl>> vec) {
        this.vec = vec;
    }
    
    public RegularVector<Ch,Lbl> addRegex(
            Regex<Ch> regex, Lbl label) {
        return new RegularVector<>(vec.cons(Tuple.create(regex, label)));
    }
    
    protected Option<AATreeSet<Lbl>> acceptingLabels(Comparator<Lbl> labelCmp) {
        
        AATreeSet<Lbl> labels = vec.foldRight(
                AATreeSet.emptySet(labelCmp),
                (tup) -> (l) -> tup.left.matchesEmptyString() ?
                        l.insert(tup.right) : l);
        
        return labels.size() > 0 ? Option.some(labels) : Option.none();
        
    }
    
    protected boolean matchesEmptyString() {
        
        return vec.foldRight(
                false,
                (tup) -> (matches) -> matches ?
                        true : tup.left.matchesEmptyString());
        
    }
    
    protected Partition<Ch> partition(Comparator<Ch> cmp) {
        
        return vec.foldRight(
                Partition.trivial(cmp),
                (tup) -> (p) -> p.intersect(tup.left.partition(cmp)));
        
    }
    
    protected RegularVector<Ch,Lbl> differentiate(Ch matchChar, Comparator<Ch> cmp) {
        
        return vec.foldRight(
                new RegularVector<>(),
                (tup) -> (rv) -> rv.addRegex(
                        tup.left.differentiate(matchChar, cmp), tup.right));
        
    }
    
    protected RegularVector<Ch,Lbl> nullDerivative() {
        
        return vec.foldRight(
                new RegularVector<>(),
                (tup) -> (rv) -> rv.addRegex(
                        tup.left.nullDerivative(), tup.right));
        
    }
    
    protected RegularVector<Ch,Lbl> normalize(RegexComparator<Ch> cmp) {
        
        return vec.foldRight(
                new RegularVector<>(),
                (tup) -> (rv) -> rv.addRegex(
                        tup.left.normalize(cmp), tup.right));
        
    }
    
    protected int compareTo(RegularVector<Ch,Lbl> other, RegexComparator<Ch> cmp) {
        
        return vec.compareTo(
                other.vec,
                (tup1, tup2) -> cmp.compare(tup1.left, tup2.left));
        
    }
    
}
