package ktak.differegex;

import java.util.Comparator;

import ktak.immutablejava.AATreeMap;
import ktak.immutablejava.AATreeSet;
import ktak.immutablejava.Tuple;

public class FiniteStateMachine<Ch,Lbl> {
    
    private final State<Ch> initialState;
    private final AATreeSet<State<Ch>> acceptingStates;
    private final AATreeMap<Long,AATreeSet<Lbl>> acceptingStateLabels;
    private final Transitions<State<Ch>,Ch> transitions;
    private final Comparator<State<Ch>> stateCmp =
            (s1, s2) -> s1.id.compareTo(s2.id);
    
    protected FiniteStateMachine(
            Long initialState,
            AATreeSet<Long> acceptingStates,
            AATreeMap<Long,AATreeSet<Lbl>> acceptingStateLabels,
            Transitions<Long,Ch> transitions) {
        
        this.initialState = wrapIntialState(initialState);
        this.acceptingStates = wrapAcceptingStates(acceptingStates);
        this.transitions = wrapTransitions(transitions);
        this.acceptingStateLabels = acceptingStateLabels;
        
    }
    
    public static class State<Ch> {
        
        private final Long id;
        
        private State(Long id) {
            this.id = id;
        }
        
    }
    
    private State<Ch> wrapIntialState(Long initialState) {
        return new State<Ch>(initialState);
    }
    
    private AATreeSet<State<Ch>> wrapAcceptingStates(AATreeSet<Long> acceptingStates) {
        
        return acceptingStates.sortedList().foldRight(
                AATreeSet.emptySet(stateCmp),
                (l) -> (set) -> set.insert(new State<Ch>(l)));
        
    }
    
    private Transitions<State<Ch>, Ch> wrapTransitions(
            Transitions<Long, Ch> transitions) {
        
        return new Transitions<State<Ch>, Ch>(
                transitions.delta.mapKV(
                        (kv) -> Tuple.create(
                                new State<Ch>(kv.left),
                                kv.right.mapValues((to) -> new State<Ch>(to))),
                        stateCmp),
                transitions.defaults.mapKV(
                        (kv) -> Tuple.create(
                                new State<Ch>(kv.left),
                                new State<Ch>(kv.right)),
                        stateCmp),
                transitions.charCmp);
        
    }
    
    public State<Ch> initialState() {
        return initialState;
    }
    
    public boolean isAcceptingState(State<Ch> state) {
        return acceptingStates.contains(state);
    }
    
    public State<Ch> nextState(State<Ch> currentState, Ch nextChar) {
        return transitions.getNextState(currentState, nextChar);
    }
    
    public AATreeSet<Lbl> acceptingStateLabels(State<Ch> state) {
        return acceptingStateLabels.get(state.id).match(
                (unit) -> { throw new RuntimeException(); },
                (lbls) -> lbls);
    }
    
}
