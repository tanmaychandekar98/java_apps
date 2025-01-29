package concurrency.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LockDemo {

    public static void main(String[] a) throws InterruptedException {
        EmployeeStockManager employeeStockManager = new EmployeeStockManager("Nitin", 1000);
        
        Runnable writeTask = new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<5; i++) {
                    System.out.println("Writer.. Requesting withdrawal from - " + Thread.currentThread().getName());
                    employeeStockManager.withdrawAmount(100);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        };

        Runnable readTask = new Runnable() {
            @Override
            public void run() {
                
                for(int i=0; i<5; i++) {
                    System.out.println("Reader .. Thread - " + Thread.currentThread().getName() 
                        + " , balance : " 
                        + employeeStockManager.getAmount());
                }
            }
        };
        
        Thread t1 = new Thread(writeTask);
        Thread t2 = new Thread(writeTask);
        Thread t3 = new Thread(readTask);
        Thread t4 = new Thread(readTask);

        t1.start();
        // t2.start();
        t3.start();
        t4.start();

        // t1.join();
        // t2.join();

        // System.out.println("Balance : " + employeeStockManager.getAmount());
    }
    
}

class EmployeeStockManager {
    String employee;
    int amount;

    Lock lock = new ReentrantLock();
    ReadWriteLock lock2 = new ReentrantReadWriteLock();

    Lock readLock = lock2.readLock();
    Lock writeLock = lock2.writeLock();
    
    

    EmployeeStockManager(final String name, final int amt) {
        this.employee = name;
        this.amount = amt;
    }

    public void withdrawAmount(int amt) {
        // System.out.println("start Withdrawing amount : " + amt);
        writeLock.lock();
        try {
            Thread.sleep(1000);

            if (amt > amount) {
                System.out.println("Insufficient balance...");
            } else {
                this.amount -= amt;
            }

            // System.out.println("Final balance : " + amount);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
    }

    public int getAmount() {
        readLock.lock();
        try {
            return this.amount;
        } finally {
            readLock.unlock();
        }
        
    }
}
