import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        final int NUM_THREADS = 20;
        final int NUM_OPERATIONS_PER_THREAD = 50000;
        final int NUM_NODES = 1000000;
        final int MAX_KEY_VALUE = 100000000;
        ConcurrentBST tree = new ConcurrentBST();
        Random rand = new Random();

        // insert NUM_NODES random values
        long start = System.currentTimeMillis();
        for (int i = 0; i < NUM_NODES; i++) {
            int key = rand.nextInt(MAX_KEY_VALUE);
            int value = rand.nextInt(MAX_KEY_VALUE);
            tree.put(key, value);
        }
        long end = System.currentTimeMillis();
        System.out.printf("Time taken for inserting %d nodes: %d ms\n", NUM_NODES, end - start);

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        // submit tasks to the thread pool
        start = System.currentTimeMillis();
        for (int i = 0; i < NUM_THREADS; i++) {
            executor.submit(() -> {
                int numInsertions = 0;
                int numDeletions = 0;
                int numContains = 0;
                for (int j = 0; j < NUM_OPERATIONS_PER_THREAD; j++) {
                    int operation = rand.nextInt(3);
                    int key = rand.nextInt(MAX_KEY_VALUE);
                    int value = rand.nextInt(MAX_KEY_VALUE);
                    switch (operation) {
                        case 0:
                            tree.contains(key);
                            numContains++;
                            break;
                        case 1:
                            tree.put(key, value);
                            numInsertions++;
                            break;
                        case 2:
                            tree.delete(key);
                            numDeletions++;
                            break;
                    }
                }
                System.out.printf("Thread %d: %d insertions, %d deletions, %d contains\n",
                        Thread.currentThread().getId(), numInsertions, numDeletions, numContains);
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        end = System.currentTimeMillis();
        System.out.printf("Time taken for performing %d operations on %d nodes using %d threads: %d ms\n",
                NUM_OPERATIONS_PER_THREAD * NUM_THREADS, NUM_NODES, NUM_THREADS, end - start);

        // write output to dat file
        try {
            PrintWriter writer = new PrintWriter(new File("output.dat"));
            writer.printf("Time taken for inserting %d nodes: %d ms\n", NUM_NODES, end - start);
            writer.printf("Time taken for performing %d operations on %d nodes using %d threads: %d ms\n",
                    NUM_OPERATIONS_PER_THREAD * NUM_THREADS, NUM_NODES, NUM_THREADS, end - start);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
