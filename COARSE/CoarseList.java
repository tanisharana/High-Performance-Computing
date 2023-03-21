import java.util.concurrent.locks.ReentrantLock;

public class CoarseList {
    private final Node head;
    private final Node tail;
    private final ReentrantLock lock;
    private volatile long totalTime;

    public CoarseList() {
        head = new Node(Integer.MIN_VALUE);
        tail = new Node(Integer.MAX_VALUE);
        head.next = tail;
        tail.prev = head;
        lock = new ReentrantLock();
        totalTime = 0;
    }

    public boolean add(int value) {
        long startTime = System.nanoTime();
        Node pred, curr;
        int key = value;
        lock.lock();
        try {
            pred = head;
            curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            if (key == curr.key) {
                return false;
            } else {
                Node node = new Node(key);
                node.prev = pred;
                node.next = curr;
                curr.prev = node;
                pred.next = node;
                return true;
            }
        } finally {
            lock.unlock();
            totalTime += (System.nanoTime() - startTime);
        }
    }

    public boolean remove(int value) {
        long startTime = System.nanoTime();
        Node pred, curr;
        int key = value;
        lock.lock();
        try {
            pred = head;
            curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            if (key == curr.key) {
                curr.prev.next = curr.next;
                curr.next.prev = curr.prev;
                return true;
            } else {
                return false;
            }
        } finally {
            lock.unlock();
            totalTime += (System.nanoTime() - startTime);
        }
    }

    public boolean contains(int value) {
        long startTime = System.nanoTime();
        Node pred, curr;
        int key = value;
        lock.lock();
        try {
            pred = head;
            curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            return key == curr.key;
        } finally {
            lock.unlock();
            totalTime += (System.nanoTime() - startTime);
        }
    }

    public long getTotalTime() {
        return totalTime / 1000000; // convert nanoseconds to milliseconds
    }

    private static class Node {
        private final int key;
        private Node prev;
        private Node next;

        public Node(int key) {
            this.key = key;
            this.prev = null;
            this.next = null;
        }
    }
}
