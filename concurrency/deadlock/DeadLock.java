package concurrency.deadlock;

class Employment {

    private Object l1 = new Object();
    private Object l2 = new Object();


    public void gainEx() throws InterruptedException {
        // 
        System.out.println(Thread.currentThread().getName() + " : Trying to get job for ex..");
        Thread.sleep(200);
        synchronized(l1) {
            this.getJob();
        }

    }

    public void getJob() throws InterruptedException {
        // 
        System.out.println(Thread.currentThread().getName() + " : Trying to get ex for job..");
        Thread.sleep(200);

        synchronized(l2) {
            this.gainEx();
        }
    }
}

public class DeadLock {

    public static void main(String[] a) {
        Employment employment = new Employment();

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    employment.getJob();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    employment.gainEx();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        t1.start();
        t2.start();

    }
    
}
