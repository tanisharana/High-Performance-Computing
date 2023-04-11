public class Peterson {
    private static PetersonLock lock = new PetersonLock();
    private static int counter = 0;

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> incrementCounter(0, 5000000), "Thread 1");
        Thread t2 = new Thread(() -> incrementCounter(1, 2000000), "Thread 2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Counter value: " + counter);
    }

    private static void incrementCounter(int threadId, int iterations) {
        long startTime = System.nanoTime();

        for (int i = 0; i < iterations; i++) {
            lock.lock(threadId);
            counter++;
            lock.unlock(threadId);
        }

        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        System.out.println("Thread " + (threadId + 1) + " execution time: " + elapsedTime + " ns");
    }

    private static class PetersonLock {
        private boolean[] flag = new boolean[2];
        private int victim;

        public void lock(int threadId) {
            int otherThread = 1 - threadId;
            flag[threadId] = true;
            victim = threadId;
            while (flag[otherThread] && victim == threadId) {
                // spin
            }
        }

        public void unlock(int threadId) {
            flag[threadId] = false;
        }
    }
}
