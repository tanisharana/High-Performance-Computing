import java.util.Random;

//import com.example.CoarseList;

public class CoarseListBenchmark {

    private static final int WORKLOADS[][] = {
            { 100, 0, 0 },
            { 70, 20, 10 },
            { 50, 25, 25 },
            { 30, 35, 35 },
            { 0, 50, 50 }
    };
    private static final int KEYS_RANGES[] = { 200_000, 2_000_000, 20_000_000 };
    private static final int EXECUTION_TIME = 100; // seconds

    public static void main(String[] args) {
        for (int[] workload : WORKLOADS) {
            System.out.println("Workload: " + workload[0] + "C-" + workload[1] + "I-" + workload[2] + "D");
            for (int keysRange : KEYS_RANGES) {
                System.out.println("Keys range: " + keysRange);
                CoarseList list = new CoarseList();
                fillList(list, keysRange / 2);
                for (int threads = 2; threads <= 16; threads += 2) {
                    long sequentialTime = runSequentially(list, workload, keysRange);
                    long parallelTime = runConcurrently(list, workload, keysRange, threads);
                    double speedup = (double) sequentialTime / parallelTime;
                    System.out.printf("Threads: %d, Sequential time: %d ms, Parallel time: %d ms, Speedup: %.2f\n",
                            threads, sequentialTime, parallelTime, speedup);
                }
            }
        }
    }

    private static void fillList(CoarseList list, int size) {
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            list.add(random.nextInt());
        }
    }

    private static long runSequentially(CoarseList list, int[] workload, int keysRange) {
        Random random = new Random();
        int numContains = workload[0];
        int numInserts = workload[1];
        int numDeletes = workload[2];
        int totalOps = numContains + numInserts + numDeletes;
        int[] ops = new int[totalOps];
        for (int i = 0; i < totalOps; i++) {
            if (i < numContains) {
                ops[i] = list.contains(random.nextInt(keysRange)) ? 1 : 0;
            } else if (i < numContains + numInserts) {
                ops[i] = list.add(random.nextInt(keysRange)) ? 1 : 0;
            } else {
                ops[i] = list.remove(random.nextInt(keysRange)) ? 1 : 0;
            }
        }
        long startTime = System.currentTimeMillis();
        for (int op : ops) {
            // Do nothing, just iterate over the array
            try {
                Thread.sleep(1); // Wait for 1ms between iterations
            } catch (InterruptedException e) {
                // Ignore the exception
            }
        }
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    private static long runConcurrently(CoarseList list, int[] workload, int keysRange, int threads) {
        Random random = new Random();
        int numContains = workload[0];
        int numInserts = workload[1];
        int numDeletes = workload[2];
        int totalOps = numContains + numInserts + numDeletes;
        int opsPerThread = totalOps / threads;
        Thread[] threadsArr = new Thread[threads];
        int[] ops = new int[totalOps];
        for (int i = 0; i < totalOps; i++) {
            if (i < numContains) {
                ops[i] = 0;
            } else if (i < numContains + numInserts) {
                ops[i] = 1;
            } else {
                ops[i] = 2;
            }
        }
        for (int i = 0; i < threads; i++) {
            int startIndex = i * opsPerThread;
            int endIndex = (i == threads - 1) ? totalOps : startIndex + opsPerThread;
            threadsArr[i] = new Thread(() -> {
                for (int j = startIndex; j < endIndex; j++) {
                    if (ops[j] == 0) {
                        list.contains(random.nextInt(keysRange));
                    } else if (ops[j] == 1) {
                        list.add(random.nextInt(keysRange));
                    } else {
                        list.remove(random.nextInt(keysRange));
                    }
                }
            });
            threadsArr[i].start();
        }
        try {
            for (Thread thread : threadsArr) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return list.getTotalTime();
    }
}
