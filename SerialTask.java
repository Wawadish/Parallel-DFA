package question2;

public class SerialTask extends DFATask{

    protected SerialTask(int pStartIndex, int pEndIndex) {
        super(pStartIndex, pEndIndex);
    }

    // Blanks out everything which is not a float from [startIndex, endIndex]
    // We need pass the execution to the next task to (potentially) merge and complete it.
    @Override
    public DFAExecution call() throws Exception {
        DFAExecution serialExecution = new DFAExecution(DFA.start, startIndex, true);
        for(int i = startIndex; i <= endIndex; i++){
            char c = q2.randChars[i];
            serialExecution.execute(i, c);
        }
        // Handling the case where this is the last task
        if(endIndex == q2.randChars.length - 1 && serialExecution.currentState != DFA.end){
            serialExecution.serialRemover(serialExecution.startBlankingAt, endIndex);
        }
        return serialExecution;
    }
}
