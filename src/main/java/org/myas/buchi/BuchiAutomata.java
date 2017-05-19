package org.myas.buchi;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mykhailo Yashchuk on 5/19/2017.
 */
public class BuchiAutomata {
    public static final String OR = "+";
    public static final String AND = ".";
    public static final String ITER = "*";
    public static final String OMEGA = "w";

    public Map<String, Map<String, Set<String>>> transitions;
    public Set<String> finalStates;
    public String initState;

    public BuchiAutomata() {
        this.transitions = new HashMap<>();
        this.finalStates = new HashSet<>();
        this.initState = "INIT";
    }

    public void addState(String from, String to, String symbol) {
        Map<String, Set<String>> toSymb = transitions.putIfAbsent(from, new HashMap<>());
        Set<String> symbols = toSymb.putIfAbsent(to, new HashSet<>());
        symbols.add(symbol);
    }

    public void setInitialState(String initState) {
        this.initState = initState;
    }

    public void addFinalStates(String ... states) {
        finalStates.addAll(Arrays.asList(states));
    }


}
