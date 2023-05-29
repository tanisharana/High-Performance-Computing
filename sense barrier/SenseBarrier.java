import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class SenseBarrier {
    private static final int MATRIX_SIZE = 4096;
    private static final int POWER = 64;

    private static double[][] matrix;
    private static double[][] result;
    private static CyclicBarrier barrier;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide the number of threads as a command line argument.");
            return;
        }

        int numThreads = Integer.parseInt(args[0]);
        System.out.println("Threads: " + numThreads);

        matrix = new double[MATRIX_SIZE][MATRIX_SIZE];
        result = new double[MATRIX_SIZE][MATRIX_SIZE];
        barrier = new CyclicBarrier(numThreads + 1);

        initializeMatrix();

        long startTime = System.currentTimeMillis();

        // Create and start worker threads
        Thread[] threads = new Thread[numThreads];
        int chunkSize = MATRIX_SIZE / numThreads;
        for (int i = 0; i < numThreads; i++) {
            int startRow = i * chunkSize;
            int endRow = (i == numThreads - 1) ? MATRIX_SIZE : (i + 1) * chunkSize;
            threads[i] = new Thread(new MatrixComputationWorker(startRow, endRow));
            threads[i].start();
        }

        // Wait for all worker threads to finish
        await();

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        System.out.println("Time taken: " + totalTime + "ms");
    }

    private static void initializeMatrix() {
        Random random = new Random();
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                matrix[i][j] = random.nextDouble();
            }
        }
    }

    private static void await() {
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    private static class MatrixComputationWorker implements Runnable {
        private final int startRow;
        private final int endRow;

        public MatrixComputationWorker(int startRow, int endRow) {
            this.startRow = startRow;
            this.endRow = endRow;
        }

        @Override
        public void run() {
            for (int row = startRow; row < endRow; row++) {
                for (int col = 0; col < MATRIX_SIZE; col++) {
                    result[row][col] = computeExpression(matrix[row][col]);
                }
            }

            await();
        }

        private double computeExpression(double value) {
            double sum = 0.0;
            double power = 1.0;
            for (int i = POWER; i >= 0; i--) {
                sum += power;
                power *= value;
            }
            return sum;
        }
    }
}
