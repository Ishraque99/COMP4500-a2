package assignment2;

import java.util.*;

public class Dynamic {

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
     * This method must be implemented using an efficient bottom-up dynamic programming
     * solution to the problem (not memoised)
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
    public static int optimalLossDynamic(int[] hourlyVolume,
            int[] fullServiceCapacity, int [] regularServiceCapacity, int[] minorServiceCapacity) {

        int totalHours = hourlyVolume.length;

        // NOTE: extend each service array to include waiting time for servicing.
        // Thus we don't need to consider a separate entry for out of service state
        ArrayList<Integer> fulCapWithWait = new ArrayList<>();
        for (int i = 0; i < 4; i++) { fulCapWithWait.add(0); }
        for (int x: fullServiceCapacity) { fulCapWithWait.add(x); }

        ArrayList<Integer> regCapWithWait = new ArrayList<>();
        for (int i = 0; i < 2; i++) { regCapWithWait.add(0); }
        for (int x: regularServiceCapacity) { regCapWithWait.add(x); }

        ArrayList<Integer> minCapWithWait = new ArrayList<>();
        for (int i = 0; i < 1; i++) { minCapWithWait.add(0); }
        for (int x: minorServiceCapacity) { minCapWithWait.add(x); }

        HashMap<Integer, ArrayList<Integer>> serviceMap = new HashMap<>();
        serviceMap.put(0, minCapWithWait); // minor service
        serviceMap.put(1, regCapWithWait); // regular service
        serviceMap.put(2, fulCapWithWait); // full service

        ArrayList<ArrayList<ArrayList<Integer>>> lossMatrix = new ArrayList<>();
        // our base case is currentHour==k then return 0
        for (int i=0; i < totalHours + 1; i++) {
            lossMatrix.add(new ArrayList<>());
            for (int j=0; j < 3; j++) {
                lossMatrix.get(i).add(new ArrayList<>());
                int cap;
                if (j==0) {
                    cap = minCapWithWait.size();
                } else if (j==1) {
                    cap = regCapWithWait.size();
                } else { // j==2
                    cap = fulCapWithWait.size();
                }
                for (int k = 0; k < cap; k++) {
                    if (i==totalHours) {
                        lossMatrix.get(i).get(j).add(0);
                    } else {
                        lossMatrix.get(i).get(j).add(Integer.MAX_VALUE); // placeholder
                    }
                }
            }
        }

        // calculate values
        for (int i=totalHours-1; i>=0; i--) { // start at the end
            for (int j = 0; j < 3; j++) {
                int cap;
                if (j==0) {
                    cap = minCapWithWait.size();
                } else if (j==1) {
                    cap = regCapWithWait.size();
                } else { // j==2
                    cap = fulCapWithWait.size();
                }
                for (int k = 0; k<cap; k++) {
                    int thisLoss = hourlyVolume[i] - serviceMap.get(j).get(k);
                    if (thisLoss < 0) { thisLoss = 0; } // min floor for loss val
                    if (k==cap-1) {
                        // if k is last value of index, it depends on the next hour service
                        // of which there are 3 possible
                        int serv1Loss = lossMatrix.get(i+1).get(0).get(0);
                        int serv2Loss = lossMatrix.get(i+1).get(1).get(0);
                        int serv3Loss = lossMatrix.get(i+1).get(2).get(0);

                        int finalLoss = Math.min(serv1Loss, Math.min(serv2Loss, serv3Loss)) + thisLoss;
                        lossMatrix.get(i).get(j).set(k, finalLoss);
                    } else {
                        // else it depends on next hour service start, or just the next second of the same service
                        int serv1Loss = lossMatrix.get(i+1).get(0).get(0);
                        int serv2Loss = lossMatrix.get(i+1).get(1).get(0);
                        int serv3Loss = lossMatrix.get(i+1).get(2).get(0);

                        int lastLoss = lossMatrix.get(i+1).get(j).get(k+1);
                        int finalLoss = thisLoss + Math.min(serv1Loss, Math.min(serv2Loss, Math.min(serv3Loss, lastLoss)));
                        lossMatrix.get(i).get(j).set(k, finalLoss);
                    }
                }
            }
        }

        // we want to return t=0 with a recent full service
        return lossMatrix.get(0).get(2).get(4); // REMOVE THIS LINE AND WRITE THIS METHOD
    }


    /**
     * Returns a schedule of the services that should take place on each of the k
     * = hourlyVolume.length hours that you are in charge of the pump, that guarantees
     * that the least possible cost will be incurred by your company over these k
     * hours (given parameters hourlyVolume, fullServiceCapacity, regularServiceCapacity
     * and minorServiceCapacity)
     *
     * The schedule should be an array of services of length k, where for each array index
     * i, for 0 <= i < k, the value of the array at index i should be the service that is in
     * progress at that hour (Service.FULL_SERVICE, Service.REGULAR_SERVICE, Service.MINOR_SERVICE)
     * if there is a service or null if there is no service taking place at that time.
     *
     * For example, with a k value of 8, the return value
     * [null, null, REGULAR_SERVICE, REGULAR_SERVICE, null, null, MINOR_SERVICE, null]
     * represents a schedule where a regular service is conducted that takes place through the
     * third and fourth hours (hours 2 and 3) and a minor service is conducted in the seventh hour
     * (hour 6) and no services are conducted during the other hours.
     *
     * You should assume that a full service was completed the hour before you took control
     * of the pump (i.e 1 hour before hour 0)
     *
     * (See handout for details.)
     *
     * This method must be implemented using an efficient bottom-up dynamic programming solution
     * to the problem (not memoised)
     *
     * @require the arrays hourlyVolume, fullServiceCapacity, regularServiceCapacity
     * and minorServiceCapacity are not null, and do not contain null values. Each
     * of the values in all arrays are non-negative (greater than or equal to 0).
     * fullServiceCapacity.length > 0, regularServiceCapacity.length > 0,
     * minorServiceCapacity.length > 0
     *
     * @ensure Returns a schedule of the services that should take place on each of the k
     * = hourlyVolume.length hours that you are in charge of the pump, that guarantees
     * that the least possible cost will be incurred by your company over these k
     * hours (given parameters hourlyVolume, fullServiceCapacity, regularServiceCapacity
     * and minorServiceCapacity)
     */
    public static Service[] optimalServicesDynamic(int[] hourlyVolume,
            int[] fullServiceCapacity, int [] regularServiceCapacity, int[] minorServiceCapacity) {
        return null; // REMOVE THIS LINE AND WRITE THIS METHOD
    }

}
