package assignment2;

public class Recursive {

    /**
     * Returns the least cost that can be incurred by your company over the
     * k = hourlyVolume.length hours (i.e hour 0 to hour k-1) that you are
     * in charge of the pump. Given that a full service concluded the hour
     * before you were placed in charge of the system (i.e finished one hour
     * before hour 0), given parameters hourlyVolume, fullServiceCapacity,
     * regularServiceCapacity and minorServiceCapacity
     *
     * (See handout for details)
     *
     * This method must be implemented using a recursive programming solution to
     * this problem. It is expected to have a worst-case running time that is
     * exponential in k
     *
     * @require the arrays hourlyVolume, fullServiceCapacity, regularServiceCapacity
     * and minorServiceCapacity are not null, and do not contain null values. Each
     * of the values in all arrays are non-negative (greater than or equal to 0).
     * fullServiceCapacity.length > 0, regularServiceCapacity.length > 0,
     * minorServiceCapacity.length > 0
     *
     * @ensure Returns the least cost that can be incurred by your company over the
     * k = hourlyVolume.length hours (i.e hour 0 to hour k-1) that you are
     * in charge of the pump. Given that a full service concluded the hour
     * before you were placed in charge of the system (i.e finished one hour
     * before hour 0), given parameters hourlyVolume, fullServiceCapacity,
     * regularServiceCapacity and minorServiceCapacity
     */
    public static int optimalLossRecursive(int[] hourlyVolume,
            int[] fullServiceCapacity, int [] regularServiceCapacity, int[] minorServiceCapacity) {
        // IMPLEMENT THIS METHOD BY IMPLEMENTING THE PRIVATE METHOD IN THIS
        // CLASS THAT HAS THE SAME NAME
        return optimalLossRecursive(hourlyVolume, fullServiceCapacity, regularServiceCapacity, minorServiceCapacity,
                0, Service.FULL_SERVICE, 0);
    }

    /**
     * Given parameters hourlyVolume, fullServiceCapacity, regularServiceCapacity and
     * minorServiceCapacity, return the least cost that can be incurred from hour
     * "currentHour" to hour "k-1" (inclusive) of the hours you are in charge of the pump
     * (where k = hourlyVolume.length), given that the last maintenance activity before hour
     * "currentHour" is given by parameter "lastService", and that it occurred
     * "hoursSinceService" hours before hour "currentHour".
     *
     * (See handout for details)
     *
     * This method must be implemented using a recursive programming solution to
     * this problem. It is expected to have a worst-case running time that is
     * exponential in k
     *
     * @require the arrays hourlyVolume, fullServiceCapacity, regularServiceCapacity
     * and minorServiceCapacity are not null, and do not contain null values. Each
     * of the values in all arrays are non-negative (greater than or equal to 0).
     * fullServiceCapacity.length > 0, regularServiceCapacity > 0, minorServiceCapacity > 0
     *
     * @ensure Returns the least cost that can be incurred by your company over the
     * k = hourlyVolume.length hours (i.e hour 0 to hour k-1) that you are
     * in charge of the pump. Given that a full service concluded the hour
     * before you were placed in charge of the system (i.e finished one hour
     * before hour 0), given parameters hourlyVolume, fullServiceCapacity,
     * regularServiceCapacity and minorServiceCapacity
     */
    private static int optimalLossRecursive(int[] hourlyVolume,
            int[] fullServiceCapacity, int [] regularServiceCapacity,
            int[] minorServiceCapacity, int currentHour, Service lastService, int hoursSinceService) {

        int k = hourlyVolume.length;
        if (currentHour == k) { return 0; } // we've exhausted our hours
        // NOTE: we can service at hour 0. so we need to make a decision first, before calling later times.

        // loss given no service
        int loss = lossFn(hourlyVolume, fullServiceCapacity, regularServiceCapacity, minorServiceCapacity,
                currentHour, lastService, hoursSinceService);
        int serviceLoss = hourlyVolume[currentHour]; // loss if we start a service

        int nextHr = currentHour +1;
        int nextNoService = loss + optimalLossRecursive(hourlyVolume, fullServiceCapacity, regularServiceCapacity,
                minorServiceCapacity, nextHr, lastService, hoursSinceService + 1);
        int nextMinService = serviceLoss + optimalLossRecursive(hourlyVolume, fullServiceCapacity, regularServiceCapacity,
                minorServiceCapacity, nextHr, Service.MINOR_SERVICE, 0);
        int nextRegService = serviceLoss + optimalLossRecursive(hourlyVolume, fullServiceCapacity, regularServiceCapacity,
                minorServiceCapacity, nextHr, Service.REGULAR_SERVICE, -1);
        int nextFullService = serviceLoss + optimalLossRecursive(hourlyVolume, fullServiceCapacity, regularServiceCapacity,
                minorServiceCapacity, nextHr, Service.FULL_SERVICE, -3);

        int[] results = {nextNoService, nextMinService, nextRegService, nextFullService};
        int m = Integer.MAX_VALUE;
        for (int i: results) {
            if (i < m) {
                m = i;
            }
        }
        return m;

//        return -1; //REMOVE THIS LINE AND WRITE THIS METHOD
    }

    private static int lossFn(int[] hourlyVolume, int[] fullServiceCapacity, int [] regularServiceCapacity,
                               int[] minorServiceCapacity, int currentHour, Service lastService,
                               int hoursSinceService) {
        // pick the right capacity
        int[] capacity;
        if (lastService.equals(Service.FULL_SERVICE)) {
            capacity = fullServiceCapacity;
        } else if (lastService.equals(Service.REGULAR_SERVICE)) {
            capacity = regularServiceCapacity;
        } else {
            capacity = minorServiceCapacity;
        }

        // consider if hoursSinceService is valid index
        int l = capacity.length;
        // calc loss so far
        int loss;
        if (hoursSinceService < 0) {
            // out of order due to service
            loss = hourlyVolume[currentHour];
        } else if (hoursSinceService >= l) {
            // out of order due to lack of service
            loss = hourlyVolume[currentHour];
        } else {
            loss = hourlyVolume[currentHour] - capacity[hoursSinceService];
        }
        if (loss < 0) {
            // we've made no loss
            loss = 0;
        }

        return loss;
    }
}
