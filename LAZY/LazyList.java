import java.util.concurrent.locks.ReentrantLock;

public class LazyList {
    private final Node head;
    private final Node tail;
    private volatile long totalTime;

    public LazyList() {
        head = new Node(Integer.MIN_VALUE);
        tail = new Node(Integer.MAX_VALUE);
        head.next = tail;
        tail.prev = head;
        totalTime = 0;
    }

    public boolean add(int value) {
        long startTime = System.nanoTime();
        Node pred, curr;
        int key = value;
        while (true) {
            pred = head;
            curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            pred.lock.lock();
            try {
                curr.lock.lock();
                try {
                    if (validate(pred, curr)) {
                        if (key == curr.key) {
                            return false;
                        } else {
                            Node node = new Node(key);
                            node.next = curr;
                            node.prev = pred;
                            curr.prev = node;
                            pred.next = node;
                            return true;
                        }
                    }
                } finally {
                    curr.lock.unlock();
                }
            } finally {
                pred.lock.unlock();
            }
        }
    }

    public boolean remove(int value) {
        long startTime = System.nanoTime();
        Node pred, curr;
        int key = value;
        while (true) {
            pred = head;
            curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            pred.lock.lock();
            try {
                curr.lock.lock();
                try {
                    if (validate(pred, curr)) {
                        if (key == curr.key) {
                            curr.marked = true;
                            curr.prev.next = curr.next;
                            curr.next.prev = curr.prev;
                            return true;
                        } else {
                            return false;
                        }
                    }
                } finally {
                    curr.lock.unlock();
                }
            } finally {
                pred.lock.unlock();
            }
        }
    }

    public boolean contains(int value) {
        long startTime = System.nanoTime();
        Node pred, curr;
        int key = value;
        while (true) {
            pred = head;
            curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            pred.lock.lock();
            try {
                curr.lock.lock();
                try {
                    if (validate(pred, curr)) {
                        return key == curr.key && !curr.marked;
                    }
                } finally {
                    curr.lock.unlock();
                }
            } finally {
                pred.lock.unlock();
            }
        }
    }

    public long getTotalTime() {
        return totalTime / 1000000; // convert nanoseconds to milliseconds
    }

    private static boolean validate(Node pred, Node curr) {
        return !pred.marked && !curr.marked && pred.next == curr && curr.prev == pred;
    }

    private static class Node {
        private final int key;
        private volatile boolean marked;
        private final ReentrantLock lock;
        private Node next;
        private Node prev;

        public Node(int key) {
            this.key = key;
            this.marked = false;
            this.lock = new ReentrantLock();
            this.next = null;
            this.prev = null;
        }
    }
}
