package concurrency.scheduledExecutors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class SchedulerDemo {
    public static void main(String[] a) {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);
        
        // Callable<Integer> callable = () -> {
        //     System.out.println("Running .. " + Thread.currentThread().getName());
        //     return 10;
        // };

        // Callable<Integer> callable2 = () -> {
        //     Thread.sleep(2000);
        //     System.out.println("Running .. " + Thread.currentThread().getName());
        //     return 20;
        // };

        List<ScheduledFuture<?>> futures = new ArrayList<>();

        for(int i=0; i<3 ;i++) {
            final int cnt = i+1;
            ScheduledFuture<?> f = executorService.scheduleAtFixedRate(() -> {
                System.out.println("Running schedule .. " + Thread.currentThread().getName());
            }, 1L, 2L,  TimeUnit.SECONDS);
            futures.add(f);
        }

        executorService.schedule(() -> {
            executorService.shutdown();
        }, 5, TimeUnit.SECONDS);

        for(ScheduledFuture<?> f : futures) {
            try {
                f.get();
                System.out.println("Completed : ");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        
    }
}
