import assignment2.Dynamic;
import assignment2.Recursive;
import assignment2.Service;
import org.junit.Assert;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;
import java.lang.annotation.*;
import java.util.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@RunWith(JUnitQuickcheck.class)
public class Main {

    @Target({PARAMETER, FIELD, ANNOTATION_TYPE, TYPE_USE})
    @Retention(RUNTIME)
    @Size(min = 0, max = 1000)
    public @interface ValidSize { }
    @Property public void validationTest(@ValidSize List<@InRange(min = "0", max = "1000") Integer> a1,
                                         @ValidSize List<@InRange(min = "0", max = "1000") Integer> a2,
                                         @ValidSize List<@InRange(min = "0", max = "1000") Integer> a3,
                                         @ValidSize List<@InRange(min = "0", max = "1000") Integer> a4
                                         ) {

        int[] a1Int = a1.stream().mapToInt(i->i).toArray();
        int[] a2Int = a2.stream().mapToInt(i->i).toArray();
        int[] a3Int = a3.stream().mapToInt(i->i).toArray();
        int[] a4Int = a4.stream().mapToInt(i->i).toArray();

//        int r = Recursive.optimalLossRecursive(a1Int, a2Int, a3Int, a4Int);
        int d = Dynamic.optimalLossDynamic(a1Int, a2Int, a3Int, a4Int);

//        Assert.assertEquals(r, d);
        Object c = d;
        Assert.assertEquals(c instanceof Integer, true);
    }

    public static void main(String[] args) {
        int[] fullServiceCapacity = {};
        int[] regularServiceCapacity = {70,50,40,30,20,10};
        int[] minorServiceCapacity = {50,40,20,10};
        int[] hourlyVol =       {50,40,90,10};

        int r = Recursive.optimalLossRecursive(hourlyVol, fullServiceCapacity, regularServiceCapacity, minorServiceCapacity);
        System.out.println(r);

//        int[] test = {};
//        System.out.println(test[0]);
//        int[] fullServiceCapacity = {100,90,80,70,60,50,40,30,20,10};
//        int[] regularServiceCapacity = {70,50,40,30,20,10};
//        int[] minorServiceCapacity = {50,40,20,10};
//
//        int[] hourlyVolume1 =       {50,40,90,10};
//        int[] hourlyVolume2 =       {50,40,90,10,5,100};
//        int[] hourlyVolume3 =       {50,40,90,10,5,100,40,20};
//        int[] hourlyVolume4 =       {50,40,90,10,5,100,40,20,50};
//        // TODO test if local optimum leads to global
//        int r1 = Recursive.optimalLossRecursive(hourlyVolume1, fullServiceCapacity, regularServiceCapacity, minorServiceCapacity);
//        int r2 = Recursive.optimalLossRecursive(hourlyVolume2, fullServiceCapacity, regularServiceCapacity, minorServiceCapacity);
//        int r3 = Recursive.optimalLossRecursive(hourlyVolume3, fullServiceCapacity, regularServiceCapacity, minorServiceCapacity);
//        int r4 = Recursive.optimalLossRecursive(hourlyVolume4, fullServiceCapacity, regularServiceCapacity, minorServiceCapacity);
//
//        System.out.println(String.format("r1: %s, r2: %s, r3: %s, r4: %s", r1, r2, r3, r4));
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
