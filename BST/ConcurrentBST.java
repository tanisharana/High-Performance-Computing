import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrentBST {
    private Node root;
    private ReentrantReadWriteLock lock;

    private class Node {
        private int key;
        private int value;
        private Node left;
        private Node right;

        public Node(int key, int value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }

    public ConcurrentBST() {
        this.root = null;
        this.lock = new ReentrantReadWriteLock();
    }

    public void put(int key, int value) {
        lock.writeLock().lock();
        try {
            root = put(root, key, value);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private Node put(Node node, int key, int value) {
        if (node == null) {
            return new Node(key, value);
        }
        if (key < node.key) {
            node.left = put(node.left, key, value);
        } else if (key > node.key) {
            node.right = put(node.right, key, value);
        } else {
            node.value = value;
        }
        return node;
    }

    public boolean contains(int key) {
        lock.readLock().lock();
        try {
            return contains(root, key);
        } finally {
            lock.readLock().unlock();
        }
    }

    private boolean contains(Node node, int key) {
        if (node == null) {
            return false;
        }
        if (key < node.key) {
            return contains(node.left, key);
        } else if (key > node.key) {
            return contains(node.right, key);
        } else {
            return true;
        }
    }

    public boolean delete(int key) {
        lock.writeLock().lock();
        try {
            if (!contains(key)) {
                return false;
            }
            root = delete(root, key);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    private Node delete(Node node, int key) {
        if (node == null) {
            return null;
        }
        if (key < node.key) {
            node.left = delete(node.left, key);
        } else if (key > node.key) {
            node.right = delete(node.right, key);
        } else {
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }
            Node temp = node;
            node = min(temp.right);
            node.right = deleteMin(temp.right);
            node.left = temp.left;
        }
        return node;
    }

    private Node min(Node node) {
        if (node.left == null) {
            return node;
        } else {
            return min(node.left);
        }
    }

    private Node deleteMin(Node node) {
        if (node.left == null) {
            return node.right;
        }
        node.left = deleteMin(node.left);
        return node;
    }
}
