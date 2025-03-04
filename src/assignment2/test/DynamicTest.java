package assignment2.test;

import assignment2.Dynamic;
import assignment2.Recursive;
import assignment2.Service;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class DynamicTest {

    @Test
    public void exampleDynamicTest() {
        int[] hourlyVolume =       {50,40,90,10,5,100,40,20,50};
        int[] fullServiceCapacity = {100,90,80,70,60,50,40,30,20,10};
        int[] regularServiceCapacity = {70,50,40,30,20,10};
        int[] minorServiceCapacity = {50,40,20,10};
        int expectedResult = 75;

        Assert.assertEquals(expectedResult, Dynamic.optimalLossDynamic(hourlyVolume, fullServiceCapacity, regularServiceCapacity, minorServiceCapacity));
    }

    @Test
    public void exampleDynamicTest2() {
        int[] hourlyVolume =       {50,40,90,10,5,100};
        int[] fullServiceCapacity = {100,90,80,70,60,50,40,30,20,10};
        int[] regularServiceCapacity = {70,50,40,30,20,10};
        int[] minorServiceCapacity = {50,40,20,10};
        int expectedResult = 55;

        Assert.assertEquals(expectedResult, Dynamic.optimalLossDynamic(hourlyVolume, fullServiceCapacity, regularServiceCapacity, minorServiceCapacity));
    }

    @Test
    public void exampleBreakingTest1() {
        int[] hourlyVolume =       {220, 476, 215, 293, 557, 719, 346, 457, 865};
        int[] fullServiceCapacity = {277, 960, 673, 763, 576};
        int[] regularServiceCapacity = {262};
        int[] minorServiceCapacity = {};
        int expectedResult = 1773;

        Assert.assertEquals(expectedResult, Dynamic.optimalLossDynamic(hourlyVolume, fullServiceCapacity, regularServiceCapacity, minorServiceCapacity));
    }

    @Test
    public void exampleBreakingTest1Services() {
        int[] hourlyVolume =       {220, 476, 215, 293, 557, 719, 346, 457, 865};
        int[] fullServiceCapacity = {277, 960, 673, 763, 576};
        int[] regularServiceCapacity = {262};
        int[] minorServiceCapacity = {};
        int expectedResult = 1773;

        checkServicesResult(hourlyVolume, fullServiceCapacity, regularServiceCapacity, minorServiceCapacity, expectedResult);
    }

    @Test
    public void exampleBreakingServ2() {
        int[] hourlyVolume =       {332, 402, 130, 1, 313, 892, 367, 911};
        int[] fullServiceCapacity = {236};
        int[] regularServiceCapacity = {244, 737, 180, 134, 382, 489, 389, 739};
        int[] minorServiceCapacity = {153, 89, 396, 3, 137, 294, 143, 950};
        int expectedResult = Recursive.optimalLossRecursive(hourlyVolume, fullServiceCapacity, regularServiceCapacity, minorServiceCapacity);

        checkServicesResult(hourlyVolume, fullServiceCapacity, regularServiceCapacity, minorServiceCapacity, expectedResult);
    }



    @Test
    public void dynTest() {
        int[] hourlyVolume =       {100, 100};
        int[] fullServiceCapacity = {10, 10};
        int[] regularServiceCapacity = {70,50,40,30,20,10};
        int[] minorServiceCapacity = {100, 100};
        int expectedResult = 100;

        Assert.assertEquals(expectedResult, Dynamic.optimalLossDynamic(hourlyVolume, fullServiceCapacity, regularServiceCapacity, minorServiceCapacity));
    }

    @Test
    public void exampleDynamicServicesTest() {
        int[] hourlyVolume =       {50,40,90,10,5,100,40,20,50};
        int[] fullServiceCapacity = {100,90,80,70,60,50,40,30,20,10};
        int[] regularServiceCapacity = {70,50,40,30,20,10};
        int[] minorServiceCapacity = {50,40,20,10};
        int expectedResult = 75;

        checkServicesResult(hourlyVolume, fullServiceCapacity, regularServiceCapacity, minorServiceCapacity, expectedResult);
    }

    @Test
    public void exampleDynamicServicesTest2() {
        int[] hourlyVolume =       {100, 100};
        int[] fullServiceCapacity = {10, 10};
        int[] regularServiceCapacity = {70,50,40,30,20,10};
        int[] minorServiceCapacity = {100, 100};
        int expectedResult = 100;

        checkServicesResult(hourlyVolume, fullServiceCapacity, regularServiceCapacity, minorServiceCapacity, expectedResult);
    }

    /**
     * Check that Dynamic.optimalActivitiesDynamic produces a valid list of services that would
     * produce the expectedResult
     */
    private static void checkServicesResult(int[] hourlyVolume, int[] fullServiceCapacity, int[] regularServiceCapacity,
                                       int[] minorServiceCapacity, int expectedResult) {
        Service[] actualServices = Dynamic.optimalServicesDynamic(hourlyVolume, fullServiceCapacity,
                regularServiceCapacity, minorServiceCapacity);
        System.out.println(Arrays.toString(actualServices)); //print the result, uncomment to see the result
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
