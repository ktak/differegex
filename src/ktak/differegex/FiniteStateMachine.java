package ktak.differegex;

import java.util.Comparator;

import ktak.immutablejava.AATreeSet;

public class FiniteStateMachine<CharType> {
    
    private final State<CharType> initialState;
    private final AATreeSet<State<CharType>> acceptingStates;
    private final Transitions<State<CharType>, CharType> transitions;
    private final Comparator<State<CharType>> stateCmp =
            (s1, s2) -> s1.id.compareTo(s2.id);
    
    protected FiniteStateMachine(
            Long initialState,
            AATreeSet<Long> acceptingStates,
            Transitions<Long, CharType> transitions) {
        
        this.initialState = wrapIntialState(initialState);
        this.acceptingStates = wrapAcceptingStates(acceptingStates);
        this.transitions = wrapTransitions(transitions);
        
    }
    
    public static class State<CharType> {
        
        private final Long id;
        
        private State(Long id) {
            this.id = id;
        }
        
    }
    
    private State<CharType> wrapIntialState(Long initialState) {
        return new State<CharType>(initialState);
    }
    
    private AATreeSet<State<CharType>> wrapAcceptingStates(AATreeSet<Long> acceptingStates) {
        
        return acceptingStates.sortedList().foldRight(
                AATreeSet.emptySet(stateCmp),
                (l) -> (set) -> set.insert(new State<CharType>(l)));
        
    }
    
    private Transitions<State<CharType>, CharType> wrapTransitions(
            Transitions<Long, CharType> transitions) {
        
        return new Transitions<State<CharType>, CharType>(
                transitions.delta.mapKV(
                        (from) -> new State<CharType>(from),
                        (outgoingArrows) -> outgoingArrows.mapValues((to) -> new State<CharType>(to)),
                        stateCmp),
                transitions.defaults.mapKV(
                        (from) -> new State<CharType>(from),
                        (to) -> new State<CharType>(to),
                        stateCmp),
                transitions.charCmp);
        
    }
    
    public State<CharType> initialState() {
        return initialState;
    }
    
    public boolean isAcceptingState(State<CharType> state) {
        return acceptingStates.contains(state);
    }
    
    public State<CharType> nextState(State<CharType> currentState, CharType nextChar) {
        return transitions.getNextState(currentState, nextChar);
    }
    
}
