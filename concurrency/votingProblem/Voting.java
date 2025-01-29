package concurrency.votingProblem;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// https://leetcode.com/discuss/interview-experience/5110856/Rubrik-or-G5-or-Selected/

public class Voting {
    public static void main(String[] a) throws InterruptedException {

        Bathroom bathroom = new Bathroom();
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        executorService.submit(() -> {
            for(int i=1; i<=10; i++) {
                final int per = i;
                executorService.submit(() -> {
                        try {
                            bathroom.useDemo(per);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // democrats.start();
            }
        });

        // Thread.sleep(10000);

        executorService.submit(() -> {
            for(int i=1; i<=10; i++) {
                final int per = i;
                executorService.submit(() -> {
                        try {
                            bathroom.useRepub(per);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // democrats.start();
            }
        });

        


        // democrats.start();
        // Thread.sleep(5000);
        // republics.start();
    }

}


class Bathroom {
    Semaphore sem[];
    Lock lock1 = new ReentrantLock();
    Lock lock2 = new ReentrantLock();
    // Object lock = new Object();
    volatile AtomicInteger curr = new AtomicInteger(0);
    
    
    Bathroom() {
        sem = new Semaphore[2];
        sem[0] = new Semaphore(3, true);
        sem[1] = new Semaphore(3, true);

    }

    // public void useBathroom(int per, int party) {
    //     // System.out.println("Wating.. - Person : " + per + ", party : " + party);

    //     // lock.lock();
    //     int opp = Math.abs(party - curr.get());
    //     try {
    //         if(curr.get() != party) {
    //             lock.lock();
    //             if(curr.get() != party) {
    //                 sem[opp].acquire(3);
    //                 // sem[party].release(3 - sem[party].availablePermits());
    //                 // sem[party].release(0);
    //                 curr.set(party);
    //             }
    //             lock.unlock();
                
    //         }
    //         sem[party].acquire();
    //     } catch(Exception e){
    //         //
    //     }
    //     finally {
    //         // lock.unlock();
    //     }
        

    //     System.out.println("In bathroom ..." + per + " , party : " + party);
    //     doBusiness(per);
    //     System.out.println("Done.. - Person : " + per + ", party : " + party);

    //     sem[party].release();

        
        // synchronized(lock) {
        //     try {
        //         if(party == 0) {
        //             while (sem[0].availablePermits() != 0 && sem[1].availablePermits() != 3) {
        //                 lock.wait();
        //             }
        //         } else if(party == 1) {
        //             while (sem[1].availablePermits() != 0 && sem[0].availablePermits() != 3) {
        //                 lock.wait();
        //             }
        //         }
        //         sem[party].acquire();
        //     } catch (Exception e) {
        //         e.printStackTrace();
        //     }
        // // }

        // // use
        // System.out.println("In bathroom ..." + per + " , party : " + party);
        // doBusiness(per);
        // System.out.println("Done.. - Person : " + per + ", party : " + party);
        
        // // synchronized(lock) {
        //     // System.out.println("In lock.. - Person : " + per + ", party : " + party);
        //     sem[party].release();
        //     lock.notifyAll();
        // }

        // System.out.println("Done.. - Person : " + per + ", party : " + party);
    // }

    private void doBusiness(int per) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void useDemo(int per) throws InterruptedException {
        
        lock1.lock();
        if (curr.get() != 0) {
            System.out.println(per + "," + 0 + " waiting ...");
            sem[1].acquire(3);
            System.out.println("Changing to.." + 0);
            curr.set(0);
            sem[0].release(3);
            System.out.println("Permits 0 to.." + sem[0].availablePermits());
        }
        lock1.unlock();

        sem[0].acquire();

        System.out.println(per + "," + 0 + " in ...");
        doBusiness(per);
        System.out.println(per + "," + 0 + " done ...");

        sem[0].release();
    }

    public void useRepub(int per) throws InterruptedException{
        lock2.lock();
        if (curr.get() != 1) {
            System.out.println(per + "," + 1 + " waiting ...");
            sem[0].acquire(3);
            System.out.println("Changing to.." + 1);
            sem[1].release(3);
            curr.set(1);
        }
        lock2.unlock();

        sem[1].acquire();

        System.out.println(per + "," + 1 + " in ...");
        doBusiness(per);
        System.out.println(per + "," + 1 + " done ...");

        sem[1].release();
        // Condition condition = new ReentrantLock().newCondition().await(per, null)
        // wait(100);
    }
}
