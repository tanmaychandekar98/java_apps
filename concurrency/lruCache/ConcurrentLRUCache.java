package concurrency.lruCache;

import java.util.*;

class Node<K, V> {
    K key;
    V value;
    Node<K, V> prev, next;

    public Node(K key, V value, Node<K, V> next, Node<K, V> prev) {
        this.key = key;
        this.value = value;
        this.next = next;
        this.prev = prev;
    }
}

public class ConcurrentLRUCache<K, V> {
    int size;
    volatile Map<K, Node<K, V>> cacheMap;
    Node<K, V> head, tail;

    ConcurrentLRUCache(int size) {
        this.size = size;
        cacheMap = new HashMap<>();
        head = new Node<K, V>(null, null, null, null);
        tail = new Node<K, V>(null, null, null, head);
        head.next = tail;
    }
    
    public synchronized V get(K key) {
        if(!cacheMap.containsKey(key)) return null;

        Node<K, V> curr = cacheMap.get(key);
        moveToHead(curr);

        return curr.value;
    }

    public synchronized void put(K key, V value) {

        if (cacheMap.containsKey(key)) {
            Node<K, V> curr = cacheMap.get(key);
            curr.value = value;
            moveToHead(curr);
        } else {
            Node<K, V> newNode = new Node<K, V>(key, value, head.next, head);
            head.next.prev = newNode;
            head.next = newNode;

            cacheMap.put(key, newNode);

            if (cacheMap.size() > size) {
                removeLRU();
            }
        }

    }

    private void moveToHead(Node<K, V> node) {
        if(node == null || node.prev==head) return;
        node.prev.next = node.next;
        node.next.prev = node.prev;

        node.prev = head;
        node.next = head.next;

        head.next.prev = node;
        head.next = node;
    }

    private void removeLRU() {
        if (cacheMap.size() == 0) {
            return;
        }

        Node<K, V> lastNode = tail.prev;

        lastNode.prev.next = tail;
        tail.prev = lastNode.prev;

        cacheMap.remove(lastNode.key);
    }

    public void print() {
        Node<K, V> temp = head.next;
        StringBuilder sb = new StringBuilder();
        while (temp != tail) {
            sb.append("[" + temp.key + ", ");
            sb.append(temp.value + "] --> ");
            temp = temp.next;
        }
        sb.append("end");
        System.out.println(sb.toString());
    }
}
