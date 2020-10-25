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
        if (totalHours == 0) { return 0; }

        ArrayList<ArrayList<ArrayList<Integer>>> lossMatrix = generateLossMatrix(hourlyVolume,
                fullServiceCapacity, regularServiceCapacity, minorServiceCapacity);

        // we want to return t=0 with a recent full service, or start of service
        int minCapAt0 = lossMatrix.get(0).get(0).get(0);
        int regCapAt0 = lossMatrix.get(0).get(1).get(0);
        int fulCapAt0 = lossMatrix.get(0).get(2).get(0);
        int afterFulAt0 = lossMatrix.get(0).get(2).get(4);
        return Math.min(minCapAt0, Math.min(regCapAt0, Math.min(fulCapAt0, afterFulAt0)));
    }

    private static ArrayList<ArrayList<ArrayList<Integer>>> generateLossMatrix(int[] hourlyVolume,
                   int[] fullServiceCapacity, int[] regularServiceCapacity, int[] minorServiceCapacity) {

        int totalHours = hourlyVolume.length;
        // NOTE: extend each service array to include waiting time for servicing.
        // Thus we don't need to consider a separate entry for out of service state
        ArrayList<Integer> fulCapWithWait = new ArrayList<>();
        for (int i = 0; i < 4; i++) { fulCapWithWait.add(0); }
        for (int i = 0; i < Math.min(fullServiceCapacity.length, hourlyVolume.length); i++) {
            fulCapWithWait.add(fullServiceCapacity[i]);
        }
        if (fullServiceCapacity.length == 0) { fulCapWithWait.add(0); } // if fullService is empty we need valid index

        ArrayList<Integer> regCapWithWait = new ArrayList<>();
        for (int i = 0; i < 2; i++) { regCapWithWait.add(0); }
        for (int i = 0; i < Math.min(regularServiceCapacity.length, hourlyVolume.length); i++) {
            regCapWithWait.add(regularServiceCapacity[i]);
        }

        ArrayList<Integer> minCapWithWait = new ArrayList<>();
        for (int i = 0; i < 1; i++) { minCapWithWait.add(0); }
        for (int i = 0; i < Math.min(minorServiceCapacity.length, hourlyVolume.length); i++) {
            minCapWithWait.add(minorServiceCapacity[i]);
        }

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
                int cap = serviceMap.get(j).size();
                for (int k = 0; k < cap; k++) {
                    if (i==totalHours) {
                        lossMatrix.get(i).get(j).add(0);
                    } else {
                        lossMatrix.get(i).get(j).add(-1); // placeholder
                    }
                }
            }
        }

        // calculate values
        for (int i=totalHours-1; i>=0; i--) { // start at the end
            for (int j = 0; j < 3; j++) {
                int cap = lossMatrix.get(i).get(j).size();
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
        return lossMatrix;
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
        int totalHours = hourlyVolume.length;
        Service[] results = new Service[totalHours];
        if (totalHours == 0) { return results; }

        // NOTE: extend each service array to include waiting time for servicing.
        // Thus we don't need to consider a separate entry for out of service state
        ArrayList<Integer> fulCapWithWait = new ArrayList<>();
        for (int i = 0; i < 4; i++) { fulCapWithWait.add(0); }
        for (int i = 0; i < Math.min(fullServiceCapacity.length, hourlyVolume.length); i++) {
            fulCapWithWait.add(fullServiceCapacity[i]);
        }
        if (fullServiceCapacity.length == 0) { fulCapWithWait.add(0); } // if fullService is empty we need valid index

        ArrayList<Integer> regCapWithWait = new ArrayList<>();
        for (int i = 0; i < 2; i++) { regCapWithWait.add(0); }
        for (int i = 0; i < Math.min(regularServiceCapacity.length, hourlyVolume.length); i++) {
            regCapWithWait.add(regularServiceCapacity[i]);
        }

        ArrayList<Integer> minCapWithWait = new ArrayList<>();
        for (int i = 0; i < 1; i++) { minCapWithWait.add(0); }
        for (int i = 0; i < Math.min(minorServiceCapacity.length, hourlyVolume.length); i++) {
            minCapWithWait.add(minorServiceCapacity[i]);
        }

        HashMap<Integer, ArrayList<Integer>> serviceMap = new HashMap<>();
        serviceMap.put(0, minCapWithWait); // minor service
        serviceMap.put(1, regCapWithWait); // regular service
        serviceMap.put(2, fulCapWithWait); // full service

        ArrayList<ArrayList<ArrayList<A2Element>>> lossMatrix = new ArrayList<>();
        // our base case is currentHour==k then return 0
        for (int i=0; i < totalHours + 1; i++) {
            lossMatrix.add(new ArrayList<>());
            for (int j=0; j < 3; j++) {
                lossMatrix.get(i).add(new ArrayList<>());
                int cap = serviceMap.get(j).size();
                for (int k = 0; k < cap; k++) {
                    if (i==totalHours) {
                        lossMatrix.get(i).get(j).add(new A2Element(0, -1, -1));
                    } else {
                        lossMatrix.get(i).get(j).add(null); // placeholder
                    }
                }
            }
        }

        // calculate values
        for (int i=totalHours-1; i>=0; i--) { // start at the end
            for (int j = 0; j < 3; j++) {
                int cap = lossMatrix.get(i).get(j).size();
                for (int k = 0; k<cap; k++) {
                    int thisLoss = hourlyVolume[i] - serviceMap.get(j).get(k);
                    if (thisLoss < 0) { thisLoss = 0; } // min floor for loss val
                    A2Element serv1Loss = lossMatrix.get(i+1).get(0).get(0); // min
                    A2Element serv2Loss = lossMatrix.get(i+1).get(1).get(0); // reg
                    A2Element serv3Loss = lossMatrix.get(i+1).get(2).get(0); // ful
                    if (k==cap-1) {
                        // if k is last value of index, it depends on the next hour service
                        // of which there are 3 possible
                        A2Element[] check = {serv1Loss, serv2Loss, serv3Loss};

                        int idx = -1;
                        int m = Integer.MAX_VALUE;
                        for (int l = 0; l < 3; l++) { if (check[l].val < m) { m = check[l].val; idx = l; } }

                        int finalLoss = m + thisLoss;
                        lossMatrix.get(i).get(j).set(k, new A2Element(finalLoss, idx, 0));
                    } else {
                        // else it depends on next hour service start, or just the next second of the same service
                        A2Element lastLoss = lossMatrix.get(i+1).get(j).get(k+1);
                        A2Element[] check = {serv1Loss, serv2Loss, serv3Loss, lastLoss};

                        int idx = -1;
                        int m = Integer.MAX_VALUE;
                        int nextK = 0;
                        for (int l = 0; l < 4; l++) {
                            if (check[l].val < m) {
                                m = check[l].val;
                                idx = l;
                            }
                        }
                        if (idx==3) {
                            idx = j;
                            nextK = k+1;
                        }

                        int finalLoss = thisLoss + m;
                        lossMatrix.get(i).get(j).set(k, new A2Element(finalLoss, idx, nextK));
                    }
                }
            }
        }

//        System.out.println(lossMatrix);
        // we want to return t=0 with a recent full service, or start of service
        int j = 2;
        int k = 4;
        A2Element minCapAt0 = lossMatrix.get(0).get(0).get(0);
        A2Element regCapAt0 = lossMatrix.get(0).get(1).get(0);
        A2Element fulCapAt0 = lossMatrix.get(0).get(2).get(0);
        A2Element afterFulAt0 = lossMatrix.get(0).get(j).get(k);
        A2Element[] check = {minCapAt0, regCapAt0, fulCapAt0, afterFulAt0};
//        System.out.println(String.format("Init Check : %s, %s, %s, %s", check[0], check[1], check[2], check[3]));
        int idx = -1;
        int m = Integer.MAX_VALUE;
        for (int l = 0; l < 4; l++) {
            if (check[l].val < m) {
                m = check[l].val;
                idx = l;
            }
        }
//        System.out.println(String.format("Idx: %s", idx));
        if (idx == 0) {
            results[0] = Service.MINOR_SERVICE;
        } else if (idx == 1) {
            results[0] = Service.REGULAR_SERVICE;
        } else if (idx == 2) {
            results[0] = Service.FULL_SERVICE;
        } else {
            results[0] = null;
        }
        j = check[idx].jCoord;
        k = check[idx].kCoord;
        for (int i = 1; i < totalHours; i++) {
//            System.out.println(String.format("j: %s, k: %s", j, k));
            if (j==0 && k < 1) {
                results[i] = Service.MINOR_SERVICE;
            } else if (j==1 && k < 2) {
                results[i] = Service.REGULAR_SERVICE;
            } else if (j==2 && k < 4) {
                results[i] = Service.FULL_SERVICE;
            } else {
                results[i] = null;
            }
            A2Element next = lossMatrix.get(i).get(j).get(k);
            j = next.jCoord;
            k = next.kCoord;
        }
        return results; // REMOVE THIS LINE AND WRITE THIS METHOD
    }

    private static class A2Element {
        public int val;
        public int jCoord; // j coord this value was attained from
        public int kCoord; // k coor this value was attained from

        A2Element (int val, int j, int k) {
            this.val = val;
            this.jCoord = j;
            this.kCoord = k;
        }

        @Override
        public String toString() {
            return String.format("val: %s, nextJ: %s, nextK: %s", this.val, this.jCoord, this.kCoord);
        }
    }

}
