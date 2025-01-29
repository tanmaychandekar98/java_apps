package concurrency.lruCache;

import java.util.*;

class Node {
    int key, value;
    Node prev, next;

    Node(int key, int value, Node next, Node prev) {
        this.key = key;
        this.value = value;
        this.next = next;
        this.prev = prev;
    }
}

public class LRUCache {
    int size;
    Map<Integer, Node> cacheMap;
    Node head, tail;

    LRUCache(int size) {
        this.size = size;
        cacheMap = new HashMap<>();
        head = new Node(0, 0, null, null);
        tail = new Node(0, 0, null, head);
        head.next = tail;
    }
    
    public int get(int key) {
        if(!cacheMap.containsKey(key)) return -1;

        Node curr = cacheMap.get(key);
        moveToHead(curr);

        return curr.value;
    }

    public void put(int key, int value) {

        if (cacheMap.containsKey(key)) {
            Node curr = cacheMap.get(key);
            curr.value = value;
            moveToHead(curr);
        } else {
            Node newNode = new Node(key, value, head.next, head);
            head.next.prev = newNode;
            head.next = newNode;

            cacheMap.put(key, newNode);

            if (cacheMap.size() > size) {
                removeLRU();
            }
        }

    }

    private void moveToHead(Node node) {
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

        Node lastNode = tail.prev;

        lastNode.prev.next = tail;
        tail.prev = lastNode.prev;

        cacheMap.remove(lastNode.key);
    }

    public void print() {
        Node temp = head.next;
        StringBuilder sb = new StringBuilder();
        while (temp != tail) {
            sb.append("[" + temp.key + ", ");
            sb.append(temp.value + "] -- ");
            temp = temp.next;
        }
        System.out.println(sb.toString());
    }
}
