package concurrency.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Arrays;
import java.util.concurrent.*;

public class ExecutorDemo {
    public static void main(String[] a) {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        Callable<Integer> callable = () -> {
            System.out.println("Running .. " + Thread.currentThread().getName());
            return 10;
        };

        Callable<Integer> callable2 = () -> {
            Thread.sleep(2000);
            System.out.println("Running .. " + Thread.currentThread().getName());
            return 20;
        };

        try {
            executorService.invokeAll(Arrays.asList(callable, callable2));
            executorService.shutdownNow();
            executorService.awaitTermination(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        
    }
}
