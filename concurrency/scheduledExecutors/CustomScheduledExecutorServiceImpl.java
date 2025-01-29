package concurrency.scheduledExecutors;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;


enum SType {
    ONCE,
    PERIODIC,
    PERIODIC_DELAY;
}

class Event {
    Runnable runnable;
    Long timestamp;
    SType type;
    Long delay;

    Event(Runnable r, Long t, SType type, Long delay) {
        this.runnable = r;
        this.timestamp = t;
        this.type = type;
        this.delay = delay;
    }
}

class Executable implements Runnable{
    PriorityQueue<Event> taskQueue;
    Lock queueLock;
    ExecutorService executorService;
    Map<Runnable, Future<?>> periodicDelayMap;

    Executable(PriorityQueue<Event> q, Lock l, ExecutorService executorService) {
        this.taskQueue = q;
        this.queueLock = l;
        this.executorService = executorService;
        this.periodicDelayMap = new ConcurrentHashMap<>();
    }

    @Override
    public void run() {
        while (true) {
            queueLock.lock();
            try {
                Long currentTime = System.currentTimeMillis();
                // wait();
                Predicate<PriorityQueue<Event>> canFetchEvent = (q) -> !q.isEmpty() && q.peek().timestamp <= currentTime;

                while(canFetchEvent.test(taskQueue)) {
                    Event currEvent = taskQueue.poll();

                    System.out.println("Starting new cmd : " + Thread.currentThread().getName());

                    switch (currEvent.type) {
                        case ONCE:
                            executorService.submit(currEvent.runnable);
                            break;
                        case PERIODIC:
                            executorService.submit(currEvent.runnable);
                            taskQueue.add(
                                new Event(currEvent.runnable,
                                 currEvent.timestamp + currEvent.delay,
                                 SType.PERIODIC, currEvent.delay));
                            break;
                        case PERIODIC_DELAY:
                            
                            Future<String> future = executorService.submit(currEvent.runnable, "Ok");
                            periodicDelayMap.put(currEvent.runnable, future);

                            executorService.submit(() -> {
                                try {
                                    periodicDelayMap.get(currEvent.runnable).get();
                                    periodicDelayMap.remove(currEvent.runnable);
                                    taskQueue.add(
                                        new Event(currEvent.runnable,
                                        System.currentTimeMillis() + currEvent.delay,
                                        SType.PERIODIC_DELAY, currEvent.delay));
                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                }
                            });
                            break;
                        default:
                            break;
                    }
                    
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                queueLock.unlock();

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                executorService.shutdown();
            }
            
        }
    }
}

public class CustomScheduledExecutorServiceImpl implements CustomScheduledExecutorService {

    PriorityQueue<Event> taskQueue;
    Executable executable;
    Lock queueLock;
    ExecutorService executorService;

    CustomScheduledExecutorServiceImpl() {
        this.taskQueue = new PriorityQueue<>((a, b) -> Long.compare(a.timestamp, b.timestamp));
        this.queueLock = new ReentrantLock();
        this.executorService = Executors.newFixedThreadPool(10);
        this.executable = new Executable(taskQueue, queueLock, executorService);

        this.executorService.submit(this.executable);
    }

    @Override
	public void schedule(Runnable command, long delay, TimeUnit unit) {
        Long execTime = System.currentTimeMillis() + unit.toMillis(delay);
        queueLock.lock();
        try {
            taskQueue.add(new Event(command, execTime, SType.ONCE, 0L));
        } finally {
            queueLock.unlock();
        }
    }

    @Override
    public void scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        Long execTime = System.currentTimeMillis() + unit.toMillis(initialDelay);
        queueLock.lock();
        try {
            taskQueue.add(new Event(command, execTime, SType.PERIODIC, unit.toMillis(period)));
        } finally {
            queueLock.unlock();
        }
    }

    @Override
    public void scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        Long execTime = System.currentTimeMillis() + unit.toMillis(initialDelay);
        queueLock.lock();
        try {
            taskQueue.add(new Event(command, execTime, SType.PERIODIC_DELAY, unit.toMillis(delay)));
        } finally {
            queueLock.unlock();
        }
    }
}
