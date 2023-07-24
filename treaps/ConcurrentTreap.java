import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.StampedLock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentTreap<Key extends Comparable<Key>, Value> {

    private class Node {
        private Key key;
        private Value value;
        private int priority;
        private Node left, right;
        private Lock lock;

        public Node(Key key, Value value) {
            this.key = key;
            this.value = value;
            this.priority = randomPriority();
            this.left = null;
            this.right = null;
            this.lock = new ReentrantLock();
        }
    }

    private Node root;
    private StampedLock treapLock;

    public ConcurrentTreap() {
        root = null;
        treapLock = new StampedLock();
    }

    private int randomPriority() {
        return (int) (Math.random() * Integer.MAX_VALUE);
    }

    public void insert(Key key, Value value) {
        Node newNode = new Node(key, value);

        long stamp = treapLock.writeLock();
        try {
            // Implementation omitted for brevity
        } finally {
            treapLock.unlockWrite(stamp);
        }
    }

    public void remove(Key key) {
        long stamp = treapLock.writeLock();
        try {
            // Implementation omitted for brevity
        } finally {
            treapLock.unlockWrite(stamp);
        }
    }

    public boolean contains(Key key) {
        long stamp = treapLock.tryOptimisticRead();
        Node current = root;

        while (current != null) {
            if (key.equals(current.key)) {
                if (treapLock.validate(stamp)) {
                    return true;
                } else {
                    stamp = treapLock.readLock();
                    try {
                        current = root;
                    } finally {
                        treapLock.unlockRead(stamp);
                    }
                    stamp = treapLock.tryOptimisticRead();
                }
            } else if (key.compareTo(current.key) < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        return false;
    }

    public void printInOrder() {
        long stamp = treapLock.tryOptimisticRead();
        Node current = root;

        while (current != null) {
            if (treapLock.validate(stamp)) {
                printInOrderHelper(current);
                break;
            } else {
                stamp = treapLock.readLock();
                try {
                    current = root;
                } finally {
                    treapLock.unlockRead(stamp);
                }
                stamp = treapLock.tryOptimisticRead();
            }
        }
    }

    private void printInOrderHelper(Node node) {
        if (node == null) {
            return;
        }

        long stamp = treapLock.tryOptimisticRead();
        Node current = root;
        printInOrderHelper(current.left);
        System.out.println(node.key + ": " + node.value);
        printInOrderHelper(current.right);

        if (!treapLock.validate(stamp)) {
            stamp = treapLock.readLock();
            try {
                current = root;
                printInOrderHelper(current.right);
            } finally {
                treapLock.unlockRead(stamp);
            }
        }
    }

    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println(
                    "Usage: java ConcurrentTreap <treeSize> <containsOperations> <insertOperations> <deleteOperations>");
            return;
        }

        int treeSize = Integer.parseInt(args[0]);
        int containsOperations = Integer.parseInt(args[1]);
        int insertOperations = Integer.parseInt(args[2]);
        int deleteOperations = Integer.parseInt(args[3]);

        ConcurrentTreap<Integer, String> treap = new ConcurrentTreap<>();

        long startTime = System.nanoTime();

        // Generate random treap
        for (int i = 0; i < treeSize; i++) {
            int key = (int) (Math.random() * Integer.MAX_VALUE);
            String value = "Value" + i;
            treap.insert(key, value);
        }

        long endTime = System.nanoTime();
        long insertionTime = endTime - startTime;

        startTime = System.nanoTime();

        int numThreads = Math.min(20, Runtime.getRuntime().availableProcessors());

        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < containsOperations; i++) {
            int key = (int) (Math.random() * Integer.MAX_VALUE);
            executorService.execute(() -> treap.contains(key));
        }
        for (int i = 0; i < insertOperations; i++) {
            int key = (int) (Math.random() * Integer.MAX_VALUE);
            String value = "Value" + i;
            executorService.execute(() -> treap.insert(key, value));
        }

        for (int i = 0; i < deleteOperations; i++) {
            int key = (int) (Math.random() * Integer.MAX_VALUE);
            executorService.execute(() -> treap.remove(key));
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            // Wait for all tasks to complete
        }

        endTime = System.nanoTime();
        long operationTime = endTime - startTime;

        treap.printInOrder();

        System.out.println();
        System.out.println("Total time to build the treap: " + insertionTime + " ms");
        System.out.println("Total contains/insert/remove time: " + operationTime + " ms");
    }

}
