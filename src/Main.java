import assignment2.Recursive;
import org.junit.Assert;

public class Main {
    public static void main(String[] args) {

        int[] fullServiceCapacity = {100,90,80,70,60,50,40,30,20,10};
        int[] regularServiceCapacity = {70,50,40,30,20,10};
        int[] minorServiceCapacity = {50,40,20,10};

        int[] hourlyVolume1 =       {50,40,90,10};
        int[] hourlyVolume2 =       {50,40,90,10,5,100};
        int[] hourlyVolume3 =       {50,40,90,10,5,100,40,20};
        int[] hourlyVolume4 =       {50,40,90,10,5,100,40,20,50};
        // TODO test if local optimum leads to global
        int r1 = Recursive.optimalLossRecursive(hourlyVolume1, fullServiceCapacity, regularServiceCapacity, minorServiceCapacity);
        int r2 = Recursive.optimalLossRecursive(hourlyVolume2, fullServiceCapacity, regularServiceCapacity, minorServiceCapacity);
        int r3 = Recursive.optimalLossRecursive(hourlyVolume3, fullServiceCapacity, regularServiceCapacity, minorServiceCapacity);
        int r4 = Recursive.optimalLossRecursive(hourlyVolume4, fullServiceCapacity, regularServiceCapacity, minorServiceCapacity);

        System.out.println(String.format("r1: %s, r2: %s, r3: %s, r4: %s", r1, r2, r3, r4));
//        exampleRecursiveTest();
    }

    public static void exampleRecursiveTest() {
        int[] hourlyVolume =       {50,40,90,10,5,100,40,20,50};
        int[] fullServiceCapacity = {100,90,80,70,60,50,40,30,20,10};
        int[] regularServiceCapacity = {70,50,40,30,20,10};
        int[] minorServiceCapacity = {50,40,20,10};
        int expectedResult = 75;

        Assert.assertEquals(expectedResult, Recursive.optimalLossRecursive(hourlyVolume, fullServiceCapacity, regularServiceCapacity, minorServiceCapacity));
    }

}
