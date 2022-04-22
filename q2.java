package question2;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.*;

public class q2 {

    private static int length = 100000000;
    private static char[] chars = {'-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', 'x'};
    public static char[] randChars;

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // Parsing arguments
        if(args.length != 1){
            throw new IllegalArgumentException("ERROR: use the program as follows\t q2 t");
        }
        int t = Integer.parseInt(args[0]);


        String randString = generateRandomString();
        System.out.println(randString);
        randChars = randString.toCharArray();

        ExecutorService executorService = Executors.newFixedThreadPool(t + 1);

        //long startTime = System.currentTimeMillis();

        // Splitting the array equally across all threads
        int startIndex = 0;
        int chunk = (q2.randChars.length / (t + 1)) + 1;
        int endIndex = Math.min(chunk, q2.randChars.length-1);

        // Each speculative thread needs to be aware of the previous Future result.
        ArrayList<Future<DFAExecution>> futures = new ArrayList<>();
        futures.add(executorService.submit(new SerialTask(startIndex, endIndex)));
        for(int i = 1; i < t + 1; i++){
            if(endIndex >= q2.randChars.length - 1) break;
            startIndex = endIndex + 1;
            endIndex = Math.min(startIndex + chunk, q2.randChars.length - 1);
            futures.add(executorService.submit(new SpeculativeTask(startIndex, endIndex, futures.get(futures.size() - 1))));
        }

        for(Future<?> f: futures){
            f.get();
        }
        futures.get(futures.size() - 1).get();
        String result = String.valueOf(randChars);

        //long time = System.currentTimeMillis() - startTime;
        System.out.println(result);
        //System.out.printf("Time: %d.%03d seconds\n", time/1000, time%1000);
        executorService.shutdown();

    }

    // Generates a random string
    private static String generateRandomString(){

        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        r.ints(length, 0, chars.length).forEach(num -> sb.append(chars[num]));

        return sb.toString();
    }

    private static void validateResults(String randString, String serialResult, String parallelResult){
        // Validation code used for debugging
        if(!serialResult.equals(parallelResult)){
            int differenceIndex = 0;
            int numErrors = 0;
            for(int i = 0; i < serialResult.length(); i++){
                if(serialResult.charAt(i) != parallelResult.charAt(i)){
                    differenceIndex = i;
                    numErrors++;
                }
            }
            System.out.println(randString.substring(differenceIndex - 20,  differenceIndex + 20));
            System.out.println(serialResult.substring(differenceIndex - 20,  differenceIndex + 20));
            System.out.println(parallelResult.substring(differenceIndex - 20,  differenceIndex + 20));

            throw new IllegalStateException(String.format("ERROR: Parralel result not the same as sequential result! error count: %d index: %d", numErrors, differenceIndex));
        }else{
            System.out.println("Validation successful");
        }
    }
}
