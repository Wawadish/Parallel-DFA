package question2;

import java.awt.*;
import java.util.HashMap;
import java.util.concurrent.Future;

public class SpeculativeTask extends DFATask{

    HashMap<DFA.State, DFAExecution> speculativeExecutions = new HashMap<>();
    Future<DFAExecution> previousExecution;

    protected SpeculativeTask(int pStartIndex, int pEndIndex, Future<DFAExecution> pPreviousExecution) {
        super(pStartIndex, pEndIndex);
        previousExecution = pPreviousExecution;
    }

    @Override
    public DFAExecution call() throws Exception {
        // Execute on index at a time round-robin style until we find 'x', then switch to sequential execution
        DFA.allStates.forEach(state -> speculativeExecutions.put(state, new DFAExecution(state, startIndex, false)));
        DFAExecution finalExecution = null;
        for(int i = startIndex; i <= endIndex; i++){
            char c = q2.randChars[i];

            for (DFAExecution execution : speculativeExecutions.values()){
                execution.execute(i, c);
            }

            // Switch to a serial result since all speculations will now converge
            if(c == 'x'){
                SerialTask serialTask = new SerialTask(i, endIndex);
                finalExecution = serialTask.call(); // the final result is the sequential execution
                break;
            }
        }

        DFAExecution pExecution = previousExecution.get();
        DFAExecution speculativeExecution = speculativeExecutions.get(pExecution.currentState);

        // Handles the edge case where this is the last task
        if(finalExecution == null && endIndex == q2.randChars.length - 1 && speculativeExecution.currentState != DFA.end) {
            speculativeExecution.intervalsToBlank.add(new Point(speculativeExecution.startBlankingAt, endIndex));
        }

        merge(pExecution, speculativeExecution);
        blank(speculativeExecution);


        // Handles the slim possibility of no 'x' being found
        if(finalExecution == null){
            finalExecution = speculativeExecution;
        }
        
        return finalExecution;
    }

    // Merge the speculative execution with the previous one.
    private void merge(DFAExecution pExecution, DFAExecution speculativeExecution){
        // Nothing to merge, move along sir
        if(pExecution.currentState == DFA.end){
            return;
        }

        // The previous execution did not end at DFA.end, therefore if we did not remove anything we should carry over
        // the startBlank index for safety
        if(speculativeExecution.intervalsToBlank.size() == 0) {
            speculativeExecution.startBlankingAt = pExecution.startBlankingAt;
            return;
        }

        // The previous execution did not end at DFA.end and we need to blank stuff
        // If we the first index we need to blank is the start index, then it means the previousExecution did not
        // find a match, therefore we blank it out
        Point firstInterval = speculativeExecution.intervalsToBlank.get(0);
        if(firstInterval.x == startIndex){
            speculativeExecution.serialRemover(pExecution.startBlankingAt, startIndex - 1);
        }
    }

    // Realizes a speculative execution
    private void blank(DFAExecution speculativeExecution){
        speculativeExecution.intervalsToBlank.forEach(interval -> {
            speculativeExecution.serialRemover(interval.x, interval.y);
        });
    }
}
