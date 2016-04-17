package ktak.differegex;

import java.util.Comparator;

import ktak.immutablejava.AATreeMap;
import ktak.immutablejava.AATreeSet;
import ktak.immutablejava.Either;

public class RegexToFiniteStateMachine {
    
    private static final Comparator<Long> longCmp =
            (l1, l2) -> l1.compareTo(l2);
    
    private RegexToFiniteStateMachine() {}
    
    public static <CharType> FiniteStateMachine<CharType> construct(
            Regex<CharType> regex, Comparator<CharType> charCmp) {
        
        RegexComparator<CharType> regexCmp = new RegexComparator<CharType>(charCmp);
        
        Transitions<Regex<CharType>, CharType> regexTransitions =
                explore(regex.normalize(regexCmp),
                        new Transitions<Regex<CharType>, CharType>(regexCmp, charCmp),
                        regexCmp);
        
        AATreeMap<Regex<CharType>, Long> regexToInteger =
                regexTransitions.mapStatesToIntegers();
        
        AATreeSet<Long> acceptingStates =
                replaceRegexesWithIntegers(
                        findAcceptingRegexes(regexTransitions, regexCmp),
                        regexToInteger);
        
        Long initialState = getElseException(regexToInteger, regex.normalize(regexCmp));
        
        return new FiniteStateMachine<CharType>(
                initialState,
                acceptingStates,
                replaceStatesWithIntegers(regexTransitions, regexToInteger));
        
    }
    
    private static <CharType> Transitions<Regex<CharType>, CharType> explore(
            Regex<CharType> state,
            Transitions<Regex<CharType>, CharType> transitions,
            RegexComparator<CharType> regexCmp) {
        
        return state.partition(regexCmp.charCmp).val.sortedList().foldRight(
                transitions,
                (charClass) -> (newTransitions) ->
                    updateTransitions(state, charClass, newTransitions, regexCmp));
        
    }
    
    private static <CharType> Transitions<Regex<CharType>, CharType> updateTransitions(
            Regex<CharType> state,
            Either<AATreeSet<CharType>, AATreeSet<CharType>> charClass,
            Transitions<Regex<CharType>, CharType> transitions,
            RegexComparator<CharType> regexCmp) {
        
        Regex<CharType> derivative = charClass.match(
                (negativeSubset) -> state.nullDerivative(),
                (positiveSubset) -> positiveSubset.sortedList().match(
                        (unit) -> state.nullDerivative(),
                        (tuple) -> state.differentiate(tuple.left, regexCmp.charCmp)))
                .normalize(regexCmp);
        
        Transitions<Regex<CharType>, CharType> updatedTransitions = charClass.match(
                (negativeSubset) -> transitions.addDefaultTransition(
                        state, derivative),
                (positiveSubset) -> positiveSubset.sortedList().foldRight(
                        transitions,
                        (charVal) -> (transitionsTmp) ->
                            transitionsTmp.addTransition(
                                    state, charVal, derivative)));
        
        return transitions.delta.containsKey(derivative) ||
                transitions.defaults.containsKey(derivative) ?
                updatedTransitions :
                explore(derivative, updatedTransitions, regexCmp);
        
    }
    
    private static <CharType> AATreeSet<Regex<CharType>> findAcceptingRegexes(
            Transitions<Regex<CharType>, CharType> transitions,
            RegexComparator<CharType> cmp) {
        
        return transitions.defaults.sortedKeys().foldRight(
                AATreeSet.emptySet(cmp),
                (regex) -> (acceptingStates) -> regex.matchesEmptyString() ?
                        acceptingStates.insert(regex) : acceptingStates);
        
    }
    
    private static <CharType> AATreeSet<Long> replaceRegexesWithIntegers(
            AATreeSet<Regex<CharType>> regexes,
            AATreeMap<Regex<CharType>, Long> regexToInteger) {
        
        return regexes.sortedList().foldRight(
                AATreeSet.emptySet(longCmp),
                (regex) -> (set) -> set.insert(getElseException(regexToInteger, regex)));
        
    }
    
    private static <StateType, CharType> Transitions<Long, CharType> replaceStatesWithIntegers(
            Transitions<StateType, CharType> transitions,
            AATreeMap<StateType, Long> stateToInteger) {
        
        return new Transitions<Long, CharType>(
                transitions.delta.mapKV(
                        (from) -> getElseException(stateToInteger, from),
                        (outgoingArrows) -> outgoingArrows.mapValues(
                                (to) -> getElseException(stateToInteger, to)),
                        longCmp),
                transitions.defaults.mapKV(
                        (from) -> getElseException(stateToInteger, from),
                        (to) -> getElseException(stateToInteger, to),
                        longCmp),
                transitions.charCmp);
        
    }
    
    private static <K,V> V getElseException(AATreeMap<K,V> map, K key) {
        
        return map.get(key).match(
                (unit) -> { throw new RuntimeException(); },
                (val) -> val);
        
    }
    
}
