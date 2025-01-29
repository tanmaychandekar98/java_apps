package concurrency.scheduledExecutors;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.*;

class Task {
    Runnable cmd;
    long timestamp;
    long delay;
    Type type;

    Task(Runnable cmd, long timestamp, long delay, Type type) {
        this.cmd = cmd;
        this.timestamp = timestamp;
        this.delay = delay;
        this.type = type;
    }
}

enum Type {
    SINGLE,
    PERIODIC,
    PERIODIC_DELAY;
}

public class SchedulerImplV2 implements CustomScheduledExecutorService{

    private PriorityQueue<Task> taskQueue;
    private Lock lock = new ReentrantLock();
    private Condition empty = lock.newCondition();

    SchedulerImplV2() {
        taskQueue = new PriorityQueue<>((a, b) -> Long.compare(a.timestamp, b.timestamp));

        // start executor/consumer thread
        Thread execThread = new Thread(new Executor(taskQueue, lock, empty));
        execThread.start();
    }

    @Override
    public void schedule(Runnable command, long delay, TimeUnit unit) {
        long timestamp = System.currentTimeMillis();
        lock.lock();
        try {
            taskQueue.add(new Task(command, timestamp, 0, Type.SINGLE));
            empty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        long timestamp = System.currentTimeMillis() + unit.toMillis(initialDelay);
        lock.lock();
        try {
            taskQueue.add(new Task(command, timestamp, unit.toMillis(period), Type.PERIODIC));
            empty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        long timestamp = System.currentTimeMillis() + unit.toMillis(initialDelay);
        lock.lock();
        try {
            taskQueue.add(new Task(command, timestamp, unit.toMillis(delay), Type.PERIODIC_DELAY));
            empty.signalAll();
        } finally {
            lock.unlock();
        }
    }
    
}

class Executor implements Runnable{

    PriorityQueue<Task> taskQueue;
    Lock lock;
    Condition empty;

    Predicate<Long> cannotPoll = (time) -> taskQueue.isEmpty() || taskQueue.peek().timestamp > time;

    Executor(PriorityQueue<Task> taskQueue, Lock lock, Condition empty) {
        this.taskQueue = taskQueue;
        this.lock = lock;
        this.empty = empty;
    }

    public void run() {
        while (true) {
            Task task = null;
            lock.lock();
            try {
                long currTime = System.currentTimeMillis();
                while (cannotPoll.test(currTime)) {
                    empty.await(100, TimeUnit.MILLISECONDS);
                    currTime = System.currentTimeMillis();
                }
                
                // retry every second
                // if (cannotPoll.test(currTime)) {
                //     lock.unlock();
                //     Thread.sleep(1000);
                //     continue;
                // }   
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                task = taskQueue.poll();
                lock.unlock();
            }
            

            // Add periodic tasks
            switch (task.type) {
                case SINGLE:
                    executeTask(task.cmd);
                    break;
                case PERIODIC:
                    executeTask(task.cmd);
                    Task newTask = new Task(task.cmd, task.timestamp + task.delay,
                                             task.delay, Type.PERIODIC);
                    lock.lock();
                    taskQueue.add(newTask);
                    lock.unlock();
                    break;
                case PERIODIC_DELAY :
                    executeAndAddDelayedTask(task);
                    break;
                default:
                    break;
            }

        }
    }

    private void executeTask(Runnable cmd) {
        Thread t = new Thread(cmd);
        t.start();
    }

    private void executeAndAddDelayedTask(Task task) {
        Thread t = new Thread(() -> {
            // Thread exec = new Thread(task.cmd);
            // exec.start();
            // exec.join();

            task.cmd.run();
            long timestamp = System.currentTimeMillis() + task.delay;
            Task newTaskDelay = new Task(task.cmd, timestamp,
                                task.delay, Type.PERIODIC_DELAY);
            lock.lock();
            taskQueue.add(newTaskDelay);
            lock.unlock();
        });

        t.start();
    }
}
