package concurrency.uberSharing;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

/*
 * Problem - 
 * Imagine at the end of a political conference, republicans
 *  and democrats are trying to leave the venue and ordering Uber rides at the same time.
 *  However, to make sure no fight breaks out in an Uber ride, the software developers
 *  at Uber come up with an algorithm whereby either an Uber ride can have all democrats
 *  or republicans or two Democrats and two Republicans. All other combinations can
 *  result in a fist-fight.
 */

public class RideSharing {
    
    public static void main(String[] a) {
        Ride ride = new Ride();

        bookDem(ride);
        bookDem(ride);
        

        bookRep(ride);
        bookRep(ride);

        bookDem(ride);
        bookDem(ride);

        bookRep(ride);
        bookRep(ride);
    }

    private static void bookDem(Ride ride) {
        Thread t = new Thread(() -> {
            ride.bookForDem();
        });
        t.start();
    }

    private static void bookRep(Ride ride) {
        Thread t = new Thread(() -> {
            ride.bookForRep();
        });
        t.start();
    }
}

class Ride {
    volatile int demSeated, repSeated, currId;
    CyclicBarrier barrier = new CyclicBarrier(4);
    Lock lock = new ReentrantLock();
    Condition waiting = lock.newCondition();

    Predicate<Boolean> canSitRep = (x) -> (demSeated <= 2 && repSeated<=1) || (repSeated < 4 && demSeated == 0);
    Predicate<Boolean> canSitDem = (x) -> (demSeated <= 1 && repSeated<=2) || (demSeated < 4 && repSeated == 0);

    Ride() {
        demSeated = 0;
        repSeated = 0;
        currId = 0;
    }

    public void bookForDem() {
        int rideId;
        lock.lock();
        try {
            while (!canSitDem.test(true)) {
                waiting.await();
            }
            demSeated++;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            rideId = currId;
            lock.unlock();
        }

        // seated
        waitInCar();
        System.out.println("Ride started with Dem.. " + rideId);

        lock.lock();
        try {
            demSeated--;
            if(demSeated + repSeated == 0) {
                currId++;
                waiting.signalAll();
            }
        } finally {
            lock.unlock();
        }   
    }

    public void bookForRep() {
        int rideId;
        lock.lock();
        try {
            while (!canSitRep.test(true)) {
                waiting.await();
            }
            repSeated++;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            rideId = currId;
            lock.unlock();
        }

        // seated
        waitInCar();
        System.out.println("Ride started with Rep.. " +  rideId);

        lock.lock();
        try {
            repSeated--;
            if(demSeated + repSeated == 0) {
                currId++;
                waiting.signalAll();
            }
        } finally {
            lock.unlock();
        }   
    }

    private void waitInCar() {
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
