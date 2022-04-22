package question2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

public class DFA {

    // DFA state has a transition function f: character -> DFA state
    public static class State {
        private Function<Character, State> transitionFunction;
        public State(Function<Character, State> f) {transitionFunction = f;}
        public State transition(char c){
            return transitionFunction.apply(c);
        }
    }
    // transitioning to error implies the end of a (potential or actual) match.
    public static State start, end, error;
    private static State one, two, three, four;
    public static ArrayList<DFA.State> allStates; // in the DFA, does not include error
    public static int size = 6; // size of DFA

    static {
        start = new State(c -> {
            if(c == '0') return one;
            if(c >= '1' && c <= '9') return two;
            if(c == '-') return three;
            return DFA.error;
        });
        one = new State(c -> {
            if(c == '.') return four;
            return error;
        });
        two = new State(c ->{
            if(c >= '0' && c <= '9') return two;
            if(c == '.') return four;
            return error;
        });
        three = new State(c ->{
            if(c == '0') return one;
            if(c >= '1' && c <= '9') return two;
            return error;
        });
        four = new State(c -> {
            if(c >= '0' && c <= '9') return end;
            return error;
        });
        end = new State(c -> {
            if(c >= '0' && c <= '9') return end;
            return error;
        });
        error = new State(c -> null);
        allStates = new ArrayList<>(Arrays.asList(start, one, two, three, four, end));
    }
}
