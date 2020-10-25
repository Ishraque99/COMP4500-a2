package assignment2.test;

import assignment2.Dynamic;
import assignment2.Recursive;
import org.junit.Assert;
import org.junit.Test;

public class RecursiveTest {

    @Test
    public void exampleRecursiveTest() {
        int[] hourlyVolume =       {50,40,90,10,5,100,40,20,50};
        int[] fullServiceCapacity = {100,90,80,70,60,50,40,30,20,10};
        int[] regularServiceCapacity = {70,50,40,30,20,10};
        int[] minorServiceCapacity = {50,40,20,10};
        int expectedResult = 75;

        Assert.assertEquals(expectedResult, Recursive.optimalLossRecursive(hourlyVolume, fullServiceCapacity, regularServiceCapacity, minorServiceCapacity));
    }

    @Test
    public void exampleBreakingTest1() {
        int[] hourlyVolume =       {220, 476, 215, 293, 557, 719, 346, 457, 865};
        int[] fullServiceCapacity = {277, 960, 673, 763, 576};
        int[] regularServiceCapacity = {262};
        int[] minorServiceCapacity = {};
        int expectedResult = 1773;

        Assert.assertEquals(expectedResult, Recursive.optimalLossRecursive(hourlyVolume, fullServiceCapacity, regularServiceCapacity, minorServiceCapacity));
    }

    @Test
    public void dynTest() {
        int[] hourlyVolume =       {100, 100};
        int[] fullServiceCapacity = {10, 10};
        int[] regularServiceCapacity = {70,50,40,30,20,10};
        int[] minorServiceCapacity = {100, 100};
        int expectedResult = 100;

        Assert.assertEquals(expectedResult, Recursive.optimalLossRecursive(hourlyVolume, fullServiceCapacity, regularServiceCapacity, minorServiceCapacity));
    }
}
