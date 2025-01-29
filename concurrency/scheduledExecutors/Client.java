package concurrency.scheduledExecutors;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Client {
    public static void main(String[] g) throws InterruptedException {
        CustomScheduledExecutorService scheduler = new CustomScheduledExecutorServiceImpl();
        System.out.println("Starting app ......");

        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Executing scheduled fix rate - " + Thread.currentThread().getName() + " : " + new Date().toString());
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }, 2, 5, TimeUnit.SECONDS);


        scheduler.scheduleWithFixedDelay(() -> {
            System.out.println("Executing scheduled after delay - " + Thread.currentThread().getName() + " : " + new Date().toString());
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }, 2, 3, TimeUnit.SECONDS);

        // Thread.sleep(10000);
    }
}
