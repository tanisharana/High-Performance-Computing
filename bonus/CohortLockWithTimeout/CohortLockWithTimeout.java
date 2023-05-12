import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CohortLockWithTimeout {
    private static final int NUM_COHORTS = 4;
    private static final int TIMEOUT_MS = 100;

    private static Lock globalLock = new ReentrantLock();
    private static Lock[] cohortLocks = new Lock[NUM_COHORTS];
    private static int[] cohortCounters = new int[NUM_COHORTS];
    private static Condition[] cohortConditions = new Condition[NUM_COHORTS];
    private static int counter = 0;

    static {
        for (int i = 0; i < NUM_COHORTS; i++) {
            cohortLocks[i] = new ReentrantLock();
            cohortConditions[i] = cohortLocks[i].newCondition();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int numThreads = 8;
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
        int cohortId = threadId % NUM_COHORTS;
        for (int i = 0; i < iterations; i++) {
            boolean acquiredCohortLock = false;
            boolean acquiredGlobalLock = false;
            try {
                acquiredCohortLock = cohortLocks[cohortId].tryLock(TIMEOUT_MS, java.util.concurrent.TimeUnit.MILLISECONDS);
                if (!acquiredCohortLock) {
                    System.out.println("Thread " + threadId + " timed out while waiting for cohort lock");
                    continue;
                }

                acquiredGlobalLock = globalLock.tryLock(TIMEOUT_MS, java.util.concurrent.TimeUnit.MILLISECONDS);
                if (!acquiredGlobalLock) {
                    System.out.println("Thread " + threadId + " timed out while waiting for global lock");
                    continue;
                }

                cohortCounters[cohortId]++;
                counter++;
            } catch (InterruptedException e) {
                // Ignore interruption and try again
                i--;
            } finally {
                if (acquiredGlobalLock) {
                    globalLock.unlock();
                }
                if (acquiredCohortLock) {
                    cohortLocks[cohortId].unlock();
                }
                // Signal all waiting threads in this cohort to check if the global lock is available
                if (cohortCounters[cohortId] == iterations) {
                    cohortCounters[cohortId] = 0;
                    cohortConditions[cohortId].signalAll();
                }
            }
        }
    }
}

