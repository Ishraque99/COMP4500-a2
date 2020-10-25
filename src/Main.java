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
import java.util.concurrent.ThreadLocalRandom;

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
        int[] fullServiceCapacity = {};
        int[] regularServiceCapacity = {70,50,40,30,20,10};
        int[] minorServiceCapacity = {50,40,20,10};
        int[] hourlyVol =       {50,40,90,10};

        int r = Recursive.optimalLossRecursive(hourlyVol, fullServiceCapacity, regularServiceCapacity, minorServiceCapacity);
        System.out.println(r);
    }

    public static void perfTests(int maxVol, int arrSize) {
        ArrayList<Integer> hourlyVol = new ArrayList<>();
        ArrayList<Integer> fullService = new ArrayList<>();
        ArrayList<Integer> regService = new ArrayList<>();
        ArrayList<Integer> minService = new ArrayList<>();
        for (int i=0; i<arrSize; i++) {
            hourlyVol.add(ThreadLocalRandom.current().nextInt(0, maxVol+1));
            fullService.add(ThreadLocalRandom.current().nextInt(0, maxVol+1));
            regService.add(ThreadLocalRandom.current().nextInt(0, maxVol+1));
            minService.add(ThreadLocalRandom.current().nextInt(0, maxVol+1));
        }



    }
}
