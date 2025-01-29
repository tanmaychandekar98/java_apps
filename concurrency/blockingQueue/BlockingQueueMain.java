package concurrency.blockingQueue;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Consumer implements Runnable{

    BlockingQueue queue;

    Consumer(BlockingQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("Consumed ... - " + queue.consume() + ", size - " + queue.queue.size());

            // simulate ops
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}


class Producer implements Runnable{

    BlockingQueue queue;

    Producer(BlockingQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {

        while (true) {
            try {
                // Simulate ops
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            int n  = (int) (Math.random()*100);
            queue.produce(n);
            System.out.println("Produced ... - " + n + ", size - " + queue.queue.size());
        }
    }
}



class BlockingQueue {

    Queue<Integer> queue;
    Semaphore empty, full;
    Lock lock = new ReentrantLock(true);
    int size;

    Object l1 = new Object();
    Object l2 = new Object();

    BlockingQueue(int n) {
        this.queue = new LinkedList<>();
        this.full = new Semaphore(0);
        this.empty = new Semaphore(n);
        this.size = n;
    }

    public synchronized int consume() {
        int value = -1;
        try {
            while (queue.isEmpty()) {
                System.out.print("Consumer going into waiting ..");
                wait();
            }
            // this.full.acquire();
            // lock.lock();
            value = queue.remove();
            // lock.unlock();
            // this.empty.release();
            notifyAll();
        } catch (InterruptedException e) {
            //
            Thread.currentThread().interrupt();
        }

        return value;
    }

    public synchronized void produce(int n) {
        try {
            while (queue.size() >= size) {
                System.out.print("Producer going into waiting ..");
                wait();
            }
            // this.empty.acquire();
            // lock.lock();
            queue.add(n);
            // lock.unlock();
            // this.full.release();
            notifyAll();
        } catch (InterruptedException e) {
            //
            Thread.currentThread().interrupt();
        }
    }
}



public class BlockingQueueMain {
    
    public static void main(String[] a) {
        BlockingQueue blockingQueue = new BlockingQueue(5);

        Thread producer = new Thread(new Producer(blockingQueue));
        Thread consumer = new Thread(new Consumer(blockingQueue));

        producer.start();
        consumer.start();
        
        // producer.join();
        // consumer.join();
    }
}
