import assignment2.Dynamic;
import assignment2.Recursive;
import assignment2.Service;
import org.junit.Assert;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@RunWith(JUnitQuickcheck.class)
public class Main {

    @Target({PARAMETER, FIELD, ANNOTATION_TYPE, TYPE_USE})
    @Retention(RUNTIME)
    @Size(min = 0, max = 13)
    public @interface ValidSize {}
    @Property public void validationTest(@ValidSize List<@InRange(min = "0", max = "1000") Integer> a1,
                                         @ValidSize List<@InRange(min = "0", max = "1000") Integer> a2,
                                         @ValidSize List<@InRange(min = "0", max = "1000") Integer> a3,
                                         @ValidSize List<@InRange(min = "0", max = "1000") Integer> a4
                                         ) {

        int[] a1Int = a1.stream().mapToInt(i->i).toArray();
        int[] a2Int = a2.stream().mapToInt(i->i).toArray();
        int[] a3Int = a3.stream().mapToInt(i->i).toArray();
        int[] a4Int = a4.stream().mapToInt(i->i).toArray();

        int r = Recursive.optimalLossRecursive(a1Int, a2Int, a3Int, a4Int);
        int d = Dynamic.optimalLossDynamic(a1Int, a2Int, a3Int, a4Int);

        Assert.assertEquals(r, d);
    }

    public static void main(String[] args) {
//        perfTests(2000, 14, 4, "out1.txt");
        perfTest2(2000, 1500, 2, "out2.txt");
    }

    public static void perfTests(int maxVol, int arrSize, int timeoutSec, String outfile) {
        ArrayList<ArrayList<Long>> results = new ArrayList<>();
        for (int k = 1; k < arrSize; k++) {
            ArrayList<Integer> hourlyVol = new ArrayList<>();
            ArrayList<Integer> fullService = new ArrayList<>();
            ArrayList<Integer> regService = new ArrayList<>();
            ArrayList<Integer> minService = new ArrayList<>();
            for (int i=0; i<k; i++) {
                hourlyVol.add(ThreadLocalRandom.current().nextInt(0, maxVol+1));
                fullService.add(ThreadLocalRandom.current().nextInt(0, maxVol+1));
                regService.add(ThreadLocalRandom.current().nextInt(0, maxVol+1));
                minService.add(ThreadLocalRandom.current().nextInt(0, maxVol+1));
            }
            int[] hSV = hourlyVol.stream().mapToInt(i->i).toArray();
            int[] fSC = fullService.stream().mapToInt(i->i).toArray();
            int[] rSC = regService.stream().mapToInt(i->i).toArray();
            int[] mSC = minService.stream().mapToInt(i->i).toArray();

            long r = timeRecursive(timeoutSec, hSV, fSC, rSC, mSC);
            long d = timeDynamic(timeoutSec, hSV, fSC, rSC, mSC);
            ArrayList<Long> thisRes = new ArrayList<>();
            thisRes.add((long) k);
            thisRes.add(r);
            thisRes.add(d);
            results.add(thisRes);
        }

        try {
            FileWriter myWriter = new FileWriter(outfile);
            for(List<Long> res: results) {
                myWriter.write(String.format("%s, %s, %s\n",
                        res.get(0), res.get(1), res.get(2)));
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void perfTest2(int maxVol, int arrSize, int timeoutSec, String outfile) {
        ArrayList<ArrayList<Long>> results = new ArrayList<>();
        for (int k = 1; k < arrSize; k++) {
            ArrayList<Integer> hourlyVol = new ArrayList<>();
            ArrayList<Integer> fullService = new ArrayList<>();
            ArrayList<Integer> regService = new ArrayList<>();
            ArrayList<Integer> minService = new ArrayList<>();
            for (int i=0; i<k; i++) {
                hourlyVol.add(ThreadLocalRandom.current().nextInt(0, maxVol+1));
                fullService.add(ThreadLocalRandom.current().nextInt(0, maxVol+1));
                regService.add(ThreadLocalRandom.current().nextInt(0, maxVol+1));
                minService.add(ThreadLocalRandom.current().nextInt(0, maxVol+1));
            }
            int[] hSV = hourlyVol.stream().mapToInt(i->i).toArray();
            int[] fSC = fullService.stream().mapToInt(i->i).toArray();
            int[] rSC = regService.stream().mapToInt(i->i).toArray();
            int[] mSC = minService.stream().mapToInt(i->i).toArray();

            long r = k;
            long s = System.nanoTime();
            int res = Dynamic.optimalLossDynamic(hSV, fSC, rSC, mSC);
            long d = System.nanoTime() - s;
            if (k%100 == 0) {
                System.out.println(k);
            }
            ArrayList<Long> thisRes = new ArrayList<>();
            thisRes.add((long) k);
            thisRes.add(r);
            thisRes.add(d);
            results.add(thisRes);
        }

        try {
            FileWriter myWriter = new FileWriter(outfile);
            for(List<Long> res: results) {
                myWriter.write(String.format("%s, %s, %s\n",
                        res.get(0), res.get(1), res.get(2)));
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static long timeRecursive(int timeout, int[] hV, int[] fSC, int[] rSC, int[] mSC) {
        AtomicLong time = new AtomicLong(-1);
        final Runnable stuffToDo = new Thread(() -> {
            long startTime = System.nanoTime();
            Recursive.optimalLossRecursive(hV, fSC, rSC, mSC);
//            System.out.println(String.format("REC res: %s", res));
            time.set(System.nanoTime() - startTime);
        });

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future future = executor.submit(stuffToDo);
        executor.shutdown(); // This does not cancel the already-scheduled task.

        try {
            future.get(timeout, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            System.err.println(String.format("Recursion Error: %s", e));
        } finally {
            if (!executor.isTerminated()) {executor.shutdownNow();}
            return time.get();
        }
    }

    public static long timeDynamic(int timeout, int[] hV, int[] fSC, int[] rSC, int[] mSC) {
        AtomicLong time = new AtomicLong(-1);
        final Runnable stuffToDo = new Thread(() -> {
            long startTime = System.nanoTime();
            int res = Dynamic.optimalLossDynamic(hV, fSC, rSC, mSC);
//            System.out.println(String.format("DYN res: %s", res));
            time.set(System.nanoTime() - startTime);
        });

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future future = executor.submit(stuffToDo);
        executor.shutdown(); // This does not cancel the already-scheduled task.

        try {
            future.get(timeout, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            System.err.println(String.format("Dynamic Error: %s", e));
        } finally {
            if (!executor.isTerminated()) {executor.shutdownNow();}
            return time.get();
        }
    }
}
