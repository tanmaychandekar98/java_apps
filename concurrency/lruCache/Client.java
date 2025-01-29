package concurrency.lruCache;

public class Client {
    
    public static void main(String[] a) {
        LRUCache lruCache = new LRUCache(3);

        lruCache.put(1, 1);
        lruCache.put(2, 3);
        lruCache.print();

        lruCache.put(3, 4);
        lruCache.print();
        lruCache.get(1);
        lruCache.print();
        
        lruCache.put(4, 4);
        lruCache.print();
        lruCache.put(2, 22);
        

        // System.out.println(lruCache.get(1));
        // System.out.println(lruCache.get(2));
        // System.out.println(lruCache.get(3));
        // System.out.println(lruCache.get(4));
    }
}
