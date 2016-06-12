package ktak.differegex;

import java.util.Comparator;

import ktak.immutablejava.AATreeMap;
import ktak.immutablejava.AATreeSet;
import ktak.immutablejava.Tuple;

public class RegexToFiniteStateMachine {
    
    private RegexToFiniteStateMachine() {}
    
    private static final Comparator<Long> longCmp =
            (l1, l2) -> l1.compareTo(l2);
    
    public static <Ch,Lbl> FiniteStateMachine<Ch,Lbl> construct(
            RegularVector<Ch,Lbl> regularVector,
            Comparator<Ch> charCmp,
            Comparator<Lbl> labelCmp) {
        
        RegexComparator<Ch> regexCmp = new RegexComparator<Ch>(charCmp);
        
        Transitions<RegularVector<Ch,Lbl>,Ch> regularVectorTransitions =
                explore(regularVector.normalize(regexCmp),
                        new Transitions<RegularVector<Ch,Lbl>,Ch>(
                                (rv1, rv2) -> rv1.compareTo(rv2, regexCmp),
                                charCmp),
                        regexCmp);
        
        AATreeMap<RegularVector<Ch,Lbl>,Long> regularVectorToInteger =
                regularVectorTransitions.mapStatesToIntegers();
        
        AATreeSet<RegularVector<Ch,Lbl>> acceptingRegularVectors =
                findAcceptingRegularVectors(regularVectorTransitions, regexCmp);
        
        AATreeSet<Long> acceptingStates =
                replaceRegularVectorsWithIntegers(
                        acceptingRegularVectors,
                        regularVectorToInteger);
        
        AATreeMap<Long,AATreeSet<Lbl>> acceptingStateLabels =
                acceptingStateLabels(
                        acceptingRegularVectors, regularVectorToInteger, labelCmp);
        
        Long initialState = getElseException(
                regularVectorToInteger, regularVector.normalize(regexCmp));
        
        return new FiniteStateMachine<Ch,Lbl>(
                initialState,
                acceptingStates,
                acceptingStateLabels,
                replaceStatesWithIntegers(
                        regularVectorTransitions, regularVectorToInteger));
        
    }
    
    public static <Ch,Lbl> FiniteStateMachine<Ch,Lbl> construct(
            Regex<Ch> regex,
            Lbl label,
            Comparator<Ch> charCmp,
            Comparator<Lbl> labelCmp) {
        
        return construct(
                new RegularVector<Ch,Lbl>().addRegex(regex, label),
                charCmp,
                labelCmp);
        
    }
    
    private static <Ch,Lbl> Transitions<RegularVector<Ch,Lbl>,Ch> explore(
            RegularVector<Ch,Lbl> state,
            Transitions<RegularVector<Ch,Lbl>,Ch> transitions,
            RegexComparator<Ch> regexCmp) {
        
        Partition<Ch> partition = state.partition(regexCmp.charCmp);
        
        return partition.subsets.sortedList().foldRight(
                updateDefaultTransitions(state, partition.negatedSubset, transitions, regexCmp),
                (charClass) -> (newTransitions) ->
                    updateTransitions(state, charClass, newTransitions, regexCmp));
        
    }
    
    private static <Ch,Lbl> Transitions<RegularVector<Ch,Lbl>,Ch> updateDefaultTransitions(
            RegularVector<Ch,Lbl> state,
            AATreeSet<Ch> negatedCharClass,
            Transitions<RegularVector<Ch,Lbl>,Ch> transitions,
            RegexComparator<Ch> regexCmp) {
        
        RegularVector<Ch,Lbl> derivative = state.nullDerivative().normalize(regexCmp);
        
        Transitions<RegularVector<Ch,Lbl>,Ch> updatedTransitions =
                transitions.addDefaultTransition(state, derivative);
        
        return transitions.defaults.containsKey(derivative) ?
                updatedTransitions : explore(derivative, updatedTransitions, regexCmp);
        
    }
    
    private static <Ch,Lbl> Transitions<RegularVector<Ch,Lbl>,Ch> updateTransitions(
            RegularVector<Ch,Lbl> state,
            AATreeSet<Ch> charClass,
            Transitions<RegularVector<Ch,Lbl>,Ch> transitions,
            RegexComparator<Ch> regexCmp) {
        
        RegularVector<Ch,Lbl> derivative =
                charClass.sortedList().match(
                        (unit) -> state.nullDerivative(),
                        (tuple) -> state.differentiate(tuple.left, regexCmp.charCmp))
                .normalize(regexCmp);
        
        Transitions<RegularVector<Ch,Lbl>,Ch> updatedTransitions =
                charClass.sortedList().foldRight(
                        transitions,
                        (charVal) -> (transitionsTmp) ->
                        transitionsTmp.addTransition(state, charVal, derivative));
        
        return transitions.delta.containsKey(derivative) ?
                updatedTransitions : explore(derivative, updatedTransitions, regexCmp);
        
    }
    
    private static <Ch,Lbl> AATreeSet<RegularVector<Ch,Lbl>> findAcceptingRegularVectors(
            Transitions<RegularVector<Ch,Lbl>,Ch> transitions,
            RegexComparator<Ch> cmp) {
        
        return transitions.defaults.sortedKeys().foldRight(
                AATreeSet.emptySet((rv1, rv2) -> rv1.compareTo(rv2, cmp)),
                (rv) -> (acceptingStates) -> rv.matchesEmptyString() ?
                        acceptingStates.insert(rv) : acceptingStates);
        
    }
    
    private static <Ch,Lbl> AATreeMap<Long,AATreeSet<Lbl>> acceptingStateLabels(
            AATreeSet<RegularVector<Ch,Lbl>> acceptingRegularVectors,
            AATreeMap<RegularVector<Ch,Lbl>,Long> regularVectorToInteger,
            Comparator<Lbl> labelCmp) {
        
        return acceptingRegularVectors.sortedList().foldRight(
                AATreeMap.emptyMap(longCmp),
                (rv) -> (stateToLabels) -> stateToLabels.insert(
                        getElseException(regularVectorToInteger, rv),
                        rv.acceptingLabels(labelCmp).match(
                                (unit) -> { throw new RuntimeException(); },
                                (labels) -> labels)));
        
    }
    
    private static <Ch,Lbl> AATreeSet<Long> replaceRegularVectorsWithIntegers(
            AATreeSet<RegularVector<Ch,Lbl>> regularVectors,
            AATreeMap<RegularVector<Ch,Lbl>, Long> regularVectorToInteger) {
        
        return regularVectors.sortedList().foldRight(
                AATreeSet.emptySet(longCmp),
                (rv) -> (set) -> set.insert(
                        getElseException(regularVectorToInteger, rv)));
        
    }
    
    private static <State,Ch> Transitions<Long,Ch> replaceStatesWithIntegers(
            Transitions<State,Ch> transitions,
            AATreeMap<State,Long> stateToInteger) {
        
        return new Transitions<Long,Ch>(
                transitions.delta.mapKV(
                        (kv) -> Tuple.create(
                                getElseException(stateToInteger, kv.left),
                                kv.right.mapValues(
                                        (to) -> getElseException(stateToInteger, to))),
                        longCmp),
                transitions.defaults.mapKV(
                        (kv) -> Tuple.create(
                                getElseException(stateToInteger, kv.left),
                                getElseException(stateToInteger, kv.right)),
                        longCmp),
                transitions.charCmp);
        
    }
    
    private static <K,V> V getElseException(AATreeMap<K,V> map, K key) {
        
        return map.get(key).match(
                (unit) -> { throw new RuntimeException(); },
                (val) -> val);
        
    }
    
}
