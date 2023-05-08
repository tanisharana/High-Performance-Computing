import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class TTASBackoff {
private static TTASLock lock = new TTASLock();
private static int counter = 0;
public static void main(String[] args) throws InterruptedException {
    int numThreads = 16;
    int iterationsPerThread = 5000000 / numThreads;

    Thread[] threads = new Thread[numThreads];

    long startTime = System.nanoTime();

    for (int i = 0; i < numThreads; i++) {
        int threadId = i;
        threads[i] = new Thread(() -> incrementCounter(threadId, iterationsPerThread), "Thread " + (i + 1));
        threads[i].start();
    }

    for (Thread t : threads) {
        t.join();
    }

    long endTime = System.nanoTime();
    double totalTime = (endTime - startTime) / 1000000000.0;

    System.out.println("Counter value: " + counter);
    System.out.println("Throughput: " + ((double) counter / totalTime) + " ops/s");
    System.out.println("Number of operations: " + counter);
}

private static void incrementCounter(int threadId, int iterations) {
    Random random = new Random();
    Backoff backoff = new Backoff(1, 100);
    
    for (int i = 0; i < iterations; i++) {
        while (true) {
            while (lock.isLocked()) {
                backoff.backoff();
            }

            if (!lock.lock()) {
                break;
            } else {
                backoff.reset();
            }
        }

        counter++;

        lock.unlock();
    }
}

private static class TTASLock {
    private AtomicBoolean state = new AtomicBoolean(false);

    public boolean isLocked() {
        return state.get();
    }

    public boolean lock() {
        return !state.getAndSet(true);
    }

    public void unlock() {
        state.set(false);
    }
}

private static class Backoff {
    private final int minDelay;
    private final int maxDelay;
    private int limit;
    private final Random random;

    public Backoff(int min, int max) {
        minDelay = min;
        maxDelay = max;
        limit = minDelay;
        random = new Random();
    }

    public void backoff() {
        int delay = random.nextInt(limit);
        limit = Math.min(maxDelay, 2 * limit);
        try {
            TimeUnit.MILLISECONDS.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        limit = minDelay;
    }
}
}