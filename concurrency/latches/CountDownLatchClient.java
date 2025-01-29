package concurrency.latches;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.*;

public class CountDownLatchClient {
    
    public static void main(String[] a) {
        int noServices = 5;
        CountDownLatch latch = new CountDownLatch(noServices);

        ExecutorService executor = Executors.newFixedThreadPool(noServices);

        for(int i=0 ; i<noServices; i++) {
            int station = i;
            Callable<Integer> count  = () -> {
                try {
                    // Some process
                    Thread.sleep(1000 * (station+1));
                    System.out.println("Station " + station + " complete.. ");
                } catch (InterruptedException e) {
                    System.out.println("Thread iterrupted.." + station);
                } finally {
                    latch.countDown();
                }
    
                return station;
            };

            executor.submit(count);
        }


        try {
            latch.await();
            System.out.println("All stations ready.. Launching.. ");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            executor.shutdown();
        }
        
    }
}
