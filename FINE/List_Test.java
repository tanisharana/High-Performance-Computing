
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ThreadLocalRandom;
import java.util.HashMap;
public class List_Test {
    private static int RANGE;
    private static int THREADS;
    private static int TIME;
    private static int PRE_FILL;
    //LazySkipList instance;
    //LockFreeList instance;
    //LazyList instance;
    //OptimisticList instance;
    FineList instance;
    //CoarseList instance;
    //SeqList instance;
    long[] opCount;
    long totalOps;
    Thread[] th;
    long start;
    int search_Limit, ins_Limit;
    public List_Test(int num_threads, int range, int time, int pre_fill, int s_Limit, int i_Limit) {
        //instance=new LazySkipList();
        //instance=new LockFreeList();
        //instance=new LazyList();
        //instance= new OptimisticList();
        instance = new FineList();
        //instance=new CoarseList();
        //instance=new SeqList();
        THREADS = num_threads;
        RANGE = range;
        TIME = time; //in milliseconds 
        PRE_FILL = pre_fill;

        th = new Thread[num_threads];
        //int nValues = 135;
        //opCount = new long[nValues];
        opCount = new long[num_threads];
        totalOps = 0;
        search_Limit = s_Limit;
        ins_Limit = i_Limit;
    }
    public void prefill() throws Exception {

        Random rd1 = new Random();

        for (int i = 0; i < 1; i++) {
            th[i] = new Fill();
        }
        for (int i = 0, time = 0; i < 1; i++) {
            th[i].start();
        }
        for (int i = 0; i < 1; i++) {
            th[i].join();
        }
    }
    class Fill extends Thread {
        int PER_THREAD_PREFILL = RANGE / 100 * PRE_FILL;
        int int_range = RANGE;
        public void run() {
            Random rd1 = new Random();
            for (int i = 0; i < PER_THREAD_PREFILL;) {
                //int val=rd1.nextInt(RANGE);
                int val1 = ThreadLocalRandom.current().nextInt(int_range);
                boolean ins = instance.add(val1);
                if (ins) {
                    i = i + 1;
                }
            }

        }
    }

    public void testParallel() throws Exception {

        for (int i = 0; i < THREADS; i++) {
            th[i] = new AllMethods();
        }
        start = System.currentTimeMillis();
        for (int i = 0; i < THREADS; i++) {
            th[i].start();
        }
        for (int i = 0; i < THREADS; i++) {
            th[i].join();
        }
    }

    class AllMethods extends Thread {
        int pri_range = Integer.MAX_VALUE - 10;
        int int_range = RANGE; //Integer.MAX_VALUE/2-10;
            
        public void run() {
            int j = ThreadID.get();
            long count = 0;
            long end = System.currentTimeMillis();
            for (;
                (end - start) <= TIME;) {

                int ch = 0;
                int chVal = ThreadLocalRandom.current().nextInt(99);

                if (chVal < search_Limit) {
                    ch = 0;
                } else if ((chVal >= search_Limit) && (chVal < ins_Limit)) {
                    ch = 1;
                } else {
                    ch = 2;
                }
                int val = ThreadLocalRandom.current().nextInt(int_range);
                switch (ch) {
                    case 0:
                        {
                            boolean exits = instance.contains(val);
                        }
                        break;
                    case 1:
                        {
                            boolean ins = instance.add(val);
                        }
                        break;
                    case 2:
                        {
                            boolean removed = instance.remove(val);
                        }
                        break;
                    default:
                        break;
                }
                count = count + 1;
                if (count % 100 == 0) {
                    end = System.currentTimeMillis();
                }
            }
             
            opCount[j] = count;
        }
    }
    public long totalOperations() {
        for (int i = 0; i < THREADS; i++) {
            totalOps = totalOps + opCount[i];
        }
        return totalOps;
    }
    void display() {
        //instance.display();
    }
    public static void main(String[] args) {
        Runtime runtime = Runtime.getRuntime();
        //runtime.gc();
        //int num_threads=Integer.parseInt(args[0]);
        //int range=Integer.parseInt(args[1]);
        //int time=Integer.parseInt(args[2]);
        //int pre_fill=Integer.parseInt(args[3]);
        //int s_Limit=Integer.parseInt(args[4]);//search interval
        //int i_Limit=Integer.parseInt(args[5]);//insertion interval
        int[][] values = {
            {
                100,
                0
            },
            {
                70,
                90
            },
            {
                50,
                75
            },
            {
                30,
                65
            },
            {
                0,
                50
            }
        };
        int num_threads;
        int range;
        int time = 1000;
        int pre_fill = 50;
        int s_Limit;
        int i_Limit;
        for (num_threads = 1; num_threads <= 16;) {
            for (range = 200000; range <= 20000000; range = range * 10) {
                for (int i = 0; i < values.length; i++) {
                    s_Limit = values[i][0];
                    i_Limit = values[i][1];
                    List_Test ob = new List_Test(num_threads, range, time, pre_fill, s_Limit, i_Limit);
                    try {
                        ob.prefill();
                        System.out.print("prefilling_done\t");
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    try {
                        ob.testParallel();
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    long total_Operations = ob.totalOperations();
                    double throughput = (total_Operations / (1000000.0 * time)) * 1000; // Millions of Operations per second
                    System.out.print(" c_name:" + ob.instance.getClass().getName());
                    System.out.print(":num_threads:" + num_threads + " :range:" + range + " :totalOps:" + total_Operations + " :throughput:" + throughput + "\n");
                    //System.out.println("Time: "+(end-start));
                    //ob.display();
                    System.out.println("This is for search limit: "+s_Limit);
                      System.out.println("This is for insertion limit: "+i_Limit);
                      System.out.println("");
                     ThreadID.reset();

                }

            }
            if (num_threads % 2 == 0) {
                num_threads = num_threads + 2;
            } else {
                num_threads = num_threads + 1;
            }
        }

    }
}
