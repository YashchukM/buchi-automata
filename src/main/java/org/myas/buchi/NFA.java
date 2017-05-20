package org.myas.buchi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mykhailo Yashchuk on 20.05.2017.
 */
public class NFA {
    public static final String OR = "+";
    public static final String AND = "";
    public static final String ITER = "*";
    public static final String EPS = "e";

    public Map<String, Map<String, String>> transitions;
    public String initState;
    public String finalState;
    public Set<String> states;

    public NFA(String initState, String finalState, Map<String, Map<String, Set<String>>> transitions) {
        this.initState = initState;
        this.finalState = finalState;
        this.states = new HashSet<>();
        this.transitions = new HashMap<>();

        initTransitions(transitions);
        initStates(transitions);
    }

    private void initTransitions(Map<String, Map<String, Set<String>>> transitions) {
        for (Map.Entry<String, Map<String, Set<String>>> entry : transitions.entrySet()) {
            Map<String, String> toMap = new HashMap<>();

            for (Map.Entry<String, Set<String>> innerEntry : entry.getValue().entrySet()) {
                StringBuilder sb = new StringBuilder();
                for (String symb : innerEntry.getValue()) {
                    if (sb.length() != 0) sb.append(OR);
                    sb.append(symb);
                }
                toMap.put(innerEntry.getKey(), sb.toString());
            }
            this.transitions.put(entry.getKey(), toMap);
        }
    }

    private void initStates(Map<String, Map<String, Set<String>>> transitions) {
        states.addAll(transitions.keySet());
        for (Map<String, Set<String>> entry : transitions.values()) {
            states.addAll(entry.keySet());
        }
        String newInitState = initState + "'";
        String newFinalState = finalState + "''";
        addOrUpdate(newInitState, initState, EPS);
        addOrUpdate(finalState, newFinalState, EPS);
        this.initState = newInitState;
        this.finalState = newFinalState;
    }

    public String getRegex() {
        for (String state : states) {
            removeState(state);
        }

        return getSymbol(initState, finalState);
    }

    private void removeState(String state) {
        Set<String> incoming = incomingStates(state);
        Set<String> outcoming = outcomingStates(state);

        for (String in : incoming) {
            for (String out : outcoming) {
                // Not count self-loops
                if ((!state.equals(out)) && (!state.equals(in))) {
                    String loopSymbol = loopSymbol(state);
                    String symb = and(getSymbol(in, state), iter(loopSymbol), getSymbol(state, out));
                    addOrUpdate(in, out, symb);
                }
            }
        }

        removeStateLinks(state);
    }

    private void removeStateLinks(String state) {
        transitions.remove(state);
        for (Map<String, String> out : transitions.values()) {
            out.remove(state);
        }
    }

    private String loopSymbol(String state) {
        if (edgeExists(state, state)) return getSymbol(state, state);
        return "";
    }

    private String getSymbol(String from, String to) {
        return transitions.get(from).get(to);
    }

    private boolean edgeExists(String from, String to) {
        if (!transitions.containsKey(from)) return false;
        Map<String, String> out = transitions.get(from);
        return out.containsKey(to);
    }

    private void addOrUpdate(String from, String to, String symb) {
        if (!transitions.containsKey(from)) {
            transitions.put(from, singletonMap(to, symb));
        } else {
            Map<String, String> out = transitions.get(from);
            if (!out.containsKey(to)) {
                out.put(to, symb);
            } else {
                String old = out.get(to);
                out.put(to, old + OR + symb);
            }
        }
    }

    public Set<String> incomingStates(String state) {
        Set<String> incoming = new HashSet<>();
        for (Map.Entry<String, Map<String, String>> entry : transitions.entrySet()) {
            if (entry.getValue().keySet().contains(state)) {
                incoming.add(entry.getKey());
            }
        }
        return incoming;
    }

    public Set<String> outcomingStates(String state) {
        Set<String> outcoming = new HashSet<>();
        for (Map.Entry<String, Map<String, String>> entry : transitions.entrySet()) {
            if (state.equals(entry.getKey())) {
                outcoming.addAll(entry.getValue().keySet());
            }
        }
        return outcoming;
    }

    private Map<String, String> singletonMap(String from, String to) {
        Map<String, String> res = new HashMap<>();
        res.put(from, to);
        return res;
    }

    private String and(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if ("".equals(part) || EPS.equals(part)) continue;
            if (sb.length() != 0) sb.append(AND);
            if (part.length() == 1
                    || part.charAt(part.length() - 1) == ITER.charAt(0)
                    || part.charAt(part.length() - 1) == ')') {
                sb.append(part);
            } else if ((part.length() > 1)) {
                sb.append('(').append(part).append(')');
            }
        }
        return sb.toString();
    }

    private String iter(String part) {
        if (part.isEmpty()) return "";
        if (part.length() == 1) return part + ITER;
        else {
            StringBuilder sb = new StringBuilder();
            sb.append('(').append(part).append(')').append(ITER);
            return sb.toString();
        }
    }
}
