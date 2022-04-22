package question2;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.BiConsumer;

public class DFAExecution {
    int startBlankingAt;
    DFA.State currentState;
    // Aggregates intervals to blank in speculative executions
    ArrayList<Point> intervalsToBlank = new ArrayList<>();
    // Function which task an interval and "removes" it (stores it in intervals for speculative executions).
    BiConsumer<Integer, Integer> remover;

    public DFAExecution(DFA.State pCurrentState, int pStartIndex,  boolean isSerial){
        currentState = pCurrentState;
        startBlankingAt = pStartIndex;
        setSerial(isSerial);
    }

    public void setSerial(boolean isSerial){
        if(isSerial){
            remover = (a, b) -> serialRemover(a , b);
        }else {
            remover = (a, b) -> speculativeRemover(a , b);
        }
    }

    // blanks the interval
    public void serialRemover(int start, int end){
        for(int i = start; i <= end; i++){
            q2.randChars[i] = ' ';
        }
    }

    // stores the interval to be blanked later
    public void speculativeRemover(int start, int end){
        intervalsToBlank.add(new Point(start, end));
    }

    // Given an index and character, executes a DFA transition on the DFAExecution's currentState
    public void execute(int index, char c){
        currentState = currentState.transition(c);

        if(currentState == DFA.end){
            startBlankingAt = index + 1;
            return;
        }

        // Not a match, remove everything starting from startBlankingAt to
        // current index (inclusive or exclusive depending on if the current character is a valid "start character")
        if(currentState == DFA.error){

            currentState = DFA.start;

            // I realize now in hindsight that I could've just checked if c == 'x' or '.', but I am not changing this now
            currentState = currentState.transition(c);
            if(currentState == DFA.error){
                remover.accept(startBlankingAt, index);
                startBlankingAt = index + 1;
                currentState = DFA.start;
            }else{
                remover.accept(startBlankingAt, index - 1);
                startBlankingAt = index;
            }
        }
    }


}
