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
    public static final String DEFAULT_INIT_STATE = "INIT";

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
        this.initState = DEFAULT_INIT_STATE;
    }

    public static void main(String[] args) {
        BuchiAutomata automata = new BuchiAutomata();
        automata.addState("0", "0", "a", "b", "c");
        automata.addState("0", "1", "b", "c");
        automata.addState("1", "1", "b", "c");
        automata.addState("0", "2", "b");
        automata.addState("2", "2", "b");
        automata.addState("2", "0", "a", "c");

        automata.setInitialState("0");
        automata.addFinalStates("1", "2");

        System.out.println(automata.getRegex());
    }

    public void addState(String from, String to, String symbol) {
        transitions.putIfAbsent(from, new HashMap<>());
        Map<String, Set<String>> toSymb = transitions.get(from);
        toSymb.putIfAbsent(to, new HashSet<>());
        Set<String> symbols = toSymb.get(to);
        symbols.add(symbol);
    }

    public void addState(String from, String to, String... symbols) {
        for (String symbol : symbols) {
            addState(from, to, symbol);
        }
    }

    public void setInitialState(String initState) {
        this.initState = initState;
    }

    public void addFinalStates(String... states) {
        finalStates.addAll(Arrays.asList(states));
    }

    public String getRegex() {
        if (DEFAULT_INIT_STATE.equals(initState) || finalStates.isEmpty() || transitions.isEmpty()) {
            throw new RuntimeException("Invalid automata parameters. Define all states and transitions");
        }

        StringBuilder sb = new StringBuilder("");
        for (String finalState : finalStates) {
            String regex = new NFA(initState, finalState, transitions).getRegex();
            String wRegex = new NFA(finalState, finalState, transitions).getRegex();

            if (sb.length() != 0) sb.append(" ").append(OR).append(" ");
            sb.append(regex).append(AND).append('[').append(wRegex).append(']').append(OMEGA);
        }
        return sb.toString();
    }
}
