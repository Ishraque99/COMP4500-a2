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
    @Size(min = 0, max = 14)
    public @interface ValidSize {}

    @Target({PARAMETER, FIELD, ANNOTATION_TYPE, TYPE_USE})
    @Retention(RUNTIME)
    @InRange(min = "0", max = "100000")
    public @interface ValidRange {}

    // assuming a valid recursive implementation, generate test cases to check
    // dynamic solutions match recursive ones
    @Property public void validationTest(@ValidSize List<@ValidRange Integer> a1,
                                         @ValidSize List<@ValidRange Integer> a2,
                                         @ValidSize List<@ValidRange Integer> a3,
                                         @ValidSize List<@ValidRange Integer> a4
                                         ) {

        int[] a1Int = a1.stream().mapToInt(i->i).toArray();
        int[] a2Int = a2.stream().mapToInt(i->i).toArray();
        int[] a3Int = a3.stream().mapToInt(i->i).toArray();
        int[] a4Int = a4.stream().mapToInt(i->i).toArray();

        int r = Recursive.optimalLossRecursive(a1Int, a2Int, a3Int, a4Int);
        int d = Dynamic.optimalLossDynamic(a1Int, a2Int, a3Int, a4Int);
        Assert.assertEquals(r, d);
        checkServicesResult(a1Int, a2Int, a3Int, a4Int, r);
    }

    // ensure dynamic services list is valid for small input
    @Property public void dynServicesTest(@ValidSize List<@InRange(min = "0", max = "1000") Integer> a1,
                                                     @ValidSize List<@InRange(min = "0", max = "1000") Integer> a2,
                                                     @ValidSize List<@InRange(min = "0", max = "1000") Integer> a3,
                                                     @ValidSize List<@InRange(min = "0", max = "1000") Integer> a4
    ) {
        int[] hourlyVolume = a1.stream().mapToInt(i->i).toArray();
        int[] fullServiceCapacity = a2.stream().mapToInt(i->i).toArray();
        int[] regularServiceCapacity = a3.stream().mapToInt(i->i).toArray();
        int[] minorServiceCapacity = a4.stream().mapToInt(i->i).toArray();

        int expectedResult = Recursive.optimalLossRecursive(hourlyVolume, fullServiceCapacity, regularServiceCapacity,
                minorServiceCapacity);

        checkServicesResult(hourlyVolume, fullServiceCapacity, regularServiceCapacity, minorServiceCapacity, expectedResult);
    }

    @Target({PARAMETER, FIELD, ANNOTATION_TYPE, TYPE_USE})
    @Retention(RUNTIME)
    @Size(min = 0, max = 500)
    public @interface ValidSize2 {}

    // ensure dynamic services are valid for larger inputs
    @Property public void exampleDynamicServicesTest(@ValidSize2 List<@InRange(min = "0", max = "10") Integer> a1,
                                           @ValidSize2 List<@InRange(min = "0", max = "10") Integer> a2,
                                           @ValidSize2 List<@InRange(min = "0", max = "10") Integer> a3,
                                           @ValidSize2 List<@InRange(min = "0", max = "10") Integer> a4
    ) {
        int[] hourlyVolume = a1.stream().mapToInt(i->i).toArray();
        int[] fullServiceCapacity = a2.stream().mapToInt(i->i).toArray();
        int[] regularServiceCapacity = a3.stream().mapToInt(i->i).toArray();
        int[] minorServiceCapacity = a4.stream().mapToInt(i->i).toArray();

        int expectedResult = Dynamic.optimalLossDynamic(hourlyVolume, fullServiceCapacity, regularServiceCapacity,
                minorServiceCapacity);

        checkServicesResult(hourlyVolume, fullServiceCapacity, regularServiceCapacity, minorServiceCapacity, expectedResult);
    }

    // run performance tests here
    public static void main(String[] args) {
//        perfTests(2000, 14, 4, "out1.txt");
        perfTest2(2000, 2000, 2, "out2.txt");
    }

    // compare recursive and dynamic growth rates
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

    // get dynamic growth rates for much larger problems sizes
    public static void perfTest2(int maxVol, int arrSize, int timeoutSec, String outfile) {
        ArrayList<ArrayList<Long>> results = new ArrayList<>();
        for (int k = 1; k < arrSize; k+= 50) {
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
            if ((k-1)%100 == 0) {
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

    // get the time taken for a given recursive run
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

    // get time taken for given dynamic run
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

    /**
     * Check that Dynamic.optimalActivitiesDynamic produces a valid list of services that would
     * produce the expectedResult
     */
    private static void checkServicesResult(int[] hourlyVolume, int[] fullServiceCapacity, int[] regularServiceCapacity,
                                            int[] minorServiceCapacity, int expectedResult) {
        Service[] actualServices = Dynamic.optimalServicesDynamic(hourlyVolume, fullServiceCapacity,
                regularServiceCapacity, minorServiceCapacity);
//        System.out.println(Arrays.toString(actualServices)); //print the result, uncomment to see the result
        checkSolutionValidity(actualServices, hourlyVolume);
        int solutionCost = getCost(hourlyVolume, fullServiceCapacity, regularServiceCapacity,
                minorServiceCapacity, actualServices);
        Assert.assertEquals(expectedResult, solutionCost);
    }

    /**
     * Checks for basic validity of a solution, checks that it has the correct length and all full and
     * regular services come in appropriately sized blocks
     */
    private static void checkSolutionValidity(Service[] services, int[] hourlyVolume) {

        Assert.assertEquals(hourlyVolume.length, services.length); //they should be the same length

        //check that full services come in blocks of 4 and regular services come in blocks of 2
        int hour = 0;
        while (hour < services.length) {
            if (services[hour] == null || services[hour] == Service.MINOR_SERVICE) {
                hour += 1;
            } else if (services[hour] == Service.FULL_SERVICE) {
                for (int extra = 1; extra < 4; extra++) {
                    Assert.assertEquals(services[hour + extra], Service.FULL_SERVICE);
                }
                hour += 4; //skip over the full services
            } else if (services[hour] == Service.REGULAR_SERVICE) {
                Assert.assertEquals(services[hour + 1], Service.REGULAR_SERVICE);
                hour += 2; //skip over the next hour
            }
        }
    }

    /**
     * Returns the cost associated with the array of services returned. This determines the total cost
     * incurred by the company if they take the strategy listed in services for the problem described
     * by hourlyVolume, fullServiceCapacity, regularServiceCapacity and minorServiceCapacity
     */
    private static int getCost(int[] hourlyVolume, int[] fullServiceCapacity, int[] regularServiceCapacity,
                               int[] minorServiceCapacity, Service[] services) {

        Service lastService = Service.FULL_SERVICE;
        int cost = 0;
        int hoursSinceService = 0;

        for (int currentHour = 0; currentHour < hourlyVolume.length; currentHour++) {
            if (services[currentHour] == null) {
                cost += getHourlyCost(hourlyVolume, fullServiceCapacity, regularServiceCapacity, minorServiceCapacity,
                        currentHour, lastService, hoursSinceService);
                hoursSinceService++; //another hour since a service
            } else {
                cost += hourlyVolume[currentHour]; //forfeit all liquid in this hour
                hoursSinceService = 0; //reset the counter
                lastService = services[currentHour]; //update the last service type
            }
        }
        return cost;
    }

    /**
     * Returns the hourly cost for the current hour given that the last service was of type 'lastService' and
     * it has been 'hoursSinceService' hours since that service.
     */
    private static int getHourlyCost(int[] hourlyVolume, int[] fullServiceCapacity, int[] regularServiceCapacity,
                                     int[] minorServiceCapacity, int currentHour, Service lastService, int hoursSinceService) {

        int[] ServiceCapacity = getServiceArray(fullServiceCapacity, regularServiceCapacity, minorServiceCapacity, lastService);
        if (hoursSinceService >= ServiceCapacity.length) {
            Assert.fail("Solution does not perform a service in time");
        }
        return Math.max(hourlyVolume[currentHour] - ServiceCapacity[hoursSinceService], 0);
    }

    /**
     * Returns the volume array that is relevant given the last service
     */
    private static int[] getServiceArray(int[] fullServiceCapacity, int[] regularServiceCapacity, int[] minorServiceCapacity,
                                         Service lastService) {
        switch (lastService) {
            case FULL_SERVICE:
                return fullServiceCapacity;
            case REGULAR_SERVICE:
                return regularServiceCapacity;
            case MINOR_SERVICE:
            default:
                return minorServiceCapacity;
        }
    }
}
