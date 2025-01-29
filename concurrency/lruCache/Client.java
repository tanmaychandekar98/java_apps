package concurrency.lruCache;

import java.util.concurrent.ExecutorService;

public class Client {
    
    public static void main(String[] a) {
        // LRUCache lruCache = new LRUCache(3);

        // lruCache.put(1, 1);
        // lruCache.put(2, 3);
        // lruCache.print();

        // lruCache.put(3, 4);
        // lruCache.print();
        // lruCache.get(1);
        // lruCache.print();
        
        // lruCache.put(4, 4);
        // lruCache.print();
        // lruCache.put(2, 22);
        

        // System.out.println(lruCache.get(1));
        // System.out.println(lruCache.get(2));
        // System.out.println(lruCache.get(3));
        // System.out.println(lruCache.get(4));


        testConcurrentLRU();
    }

    private static void testConcurrentLRU() {
        ConcurrentLRUCache<Integer, Integer> concurrentLRUCache = new ConcurrentLRUCache<Integer, Integer>(4);

        Thread writeExecutor = new Thread(() -> {
            for(int i=0; i<1; i++) {
                // int key = (int)(Math.random() * 10);
                // int key = i;
                Thread writer = new Thread(() -> {
                    while(true) {
                        int key = (int)(Math.random() * 10);
                        concurrentLRUCache.put(key, key*100);
                        try {
                            Thread.sleep(6000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });

                writer.start();
            }
        });


        Thread readExecutor = new Thread(() -> {
            for(int i=0; i<1; i++) {
                int key = (int)(Math.random() * 10);
                Thread reader = new Thread(() -> {
                    while(true) {
                        // System.out.println(key + ", " + concurrentLRUCache.get(key));
                        concurrentLRUCache.print();
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });

                reader.start();
            }
        });

        writeExecutor.start();
        readExecutor.start();
    }
}
