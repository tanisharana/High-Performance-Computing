import java.util.concurrent.atomic.AtomicBoolean;

public class TAS {
    private static TASLock lock = new TASLock();
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
        for (int i = 0; i < iterations; i++) {
            lock.lock();
            counter++;
            lock.unlock();
        }
    }

    private static class TASLock {
        AtomicBoolean state = new AtomicBoolean(false);

        public void lock() {
            while (state.getAndSet(true)) {} // spin
        }

        public void unlock() {
            state.set(false);
        }
    }
}