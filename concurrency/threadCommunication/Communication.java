package concurrency.threadCommunication;


class Restaurant {

    private boolean foodAvailable = false;
    
    public synchronized void consumeFood() {
        while (!foodAvailable) {
            System.out.println("Consumer : Waiting to be prepared ...");
            try {
                wait();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        System.out.println("Consumer : Consuming food .. 5 sec ..");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        foodAvailable = false;
        notify();
        consumeFood();
    }

    public synchronized void prepareFood() {
        while (foodAvailable) {
            System.out.println("Chef : Waiting to be consumed ...");
            try {
                wait();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        System.out.println("Chef : Preparing food .. 8 sec .. ");
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        foodAvailable = true;
        notify();
        prepareFood();
    }
}


public class Communication {
    
    public static void main(String[] a) throws InterruptedException {
        Restaurant restaurant = new Restaurant();

        System.out.println("Initially food is not there");

        Thread prepare = new Thread(() -> {
            restaurant.prepareFood();
        });

        Thread consume = new Thread(() -> {
            restaurant.consumeFood();
        });

        consume.start();
        // Thread.sleep(100);
        prepare.start();


        prepare.join();
        consume.join();

    }
}
