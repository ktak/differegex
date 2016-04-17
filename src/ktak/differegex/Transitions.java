package ktak.differegex;

import java.util.Comparator;

import ktak.immutablejava.AATreeMap;

class Transitions<StateType, CharType> {
    
    public final AATreeMap<StateType, AATreeMap<CharType, StateType>> delta;
    public final AATreeMap<StateType, StateType> defaults;
    public final Comparator<CharType> charCmp;
    
    public Transitions(
            Comparator<StateType> stateCmp, Comparator<CharType> charCmp) {
        this.delta = AATreeMap.emptyMap(stateCmp);
        this.defaults = AATreeMap.emptyMap(stateCmp);
        this.charCmp = charCmp;
    }
    
    public Transitions(
            AATreeMap<StateType, AATreeMap<CharType, StateType>> delta,
            AATreeMap<StateType, StateType> defaults,
            Comparator<CharType> charCmp) {
        this.delta = delta;
        this.defaults = defaults;
        this.charCmp = charCmp;
    }
    
    public Transitions<StateType, CharType> addTransition(
            StateType sourceState, CharType transitionChar, StateType targetState) {
        
        AATreeMap<CharType, StateType> transitionsFromSource =
                delta.get(sourceState).match(
                        (unit) -> new AATreeMap<CharType, StateType>(charCmp),
                        (transitionsFrom) -> transitionsFrom);
        
        return new Transitions<StateType, CharType>(
                delta.insert(
                        sourceState,
                        transitionsFromSource.insert(transitionChar, targetState)),
                defaults,
                charCmp);
        
    }
    
    public Transitions<StateType, CharType> addDefaultTransition(
            StateType sourceState, StateType targetState) {
        
        return new Transitions<StateType, CharType>(
                delta,
                defaults.insert(sourceState, targetState),
                charCmp);
        
    }
    
    private StateType checkDefaults(StateType currentState) {
        
        return defaults.get(currentState).match(
                (unit) -> { throw new RuntimeException(); },
                (nextState) -> nextState);
        
    }
    
    public StateType getNextState(StateType currentState, CharType nextChar) {
        
        return delta.get(currentState).match(
                (unit1) -> checkDefaults(currentState),
                (transitionsFrom) -> transitionsFrom.get(nextChar).match(
                        (unit2) -> checkDefaults(currentState),
                        (nextState) -> nextState));
        
    }
    
    public AATreeMap<StateType, Long> mapStatesToIntegers() {
        
        return defaults.sortedKeys().foldRight(
                AATreeMap.emptyMap(defaults.getComparator()),
                (state) -> (stateToInteger) ->
                    stateToInteger.insert(state, stateToInteger.size()));
        
    }
    
}
