package question2;

import java.util.concurrent.Callable;

public abstract class DFATask implements Callable<DFAExecution> {
    protected int startIndex;
    protected int endIndex;

    protected DFATask(int pStartIndex, int pEndIndex){
        startIndex = pStartIndex;
        endIndex = pEndIndex;
        //System.out.printf("(%s): (%d, %d)\n", this.getClass().getName(), startIndex, endIndex);
    }

}
