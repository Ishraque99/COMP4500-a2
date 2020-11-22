# COMP4500-a2

Solution to assignment 2 for COMP4500.

This project implements an optimal algorithmic solution to the assignment 2 problem spec.
First, a recursive solution is made, which is then used to develop and validate a dynamic programming solution.

This repo serves as a demonstration to developing and optimising algorithms using *property based testing*.
[quickcheck](https://github.com/pholser/junit-quickcheck) is used to generate test cases for validating both solutions.

The driver script in `Main.java` validates and runs the algorithms with increasing problem sizes and writes the running time to a specified output file. `vis.py` is used to ingest the output file and visualise the asymptotic growth of the algorithms using `matplotlib`.

## Project structure

```bash
/ ->|
    -> src/assigment2/        # Solution directory
    |
    ->src/assigment2/test     # Manual tests
    |
    ->src/Main.java           # Automated/Generated tests
    |
    -> vis.py                 # Python script for visualising asymptotic growth
```

## Recursive pseudocode

```java
private static int optimalLossRecursive(..., int currentHour, Service lastService, int hoursSinceService) {

        if (currentHour == hourlyVolume.length) { return 0; } // we've exhausted our hours

        int loss = lossFn()

        int nextHr = currentHour +1;
        int nextNoService = loss + optimalLossRecursive(..., nextHr, lastService, hoursSinceService + 1);
        int nextMinService = serviceLoss + optimalLossRecursive(..., nextHr, Service.MINOR_SERVICE, 0);
        int nextRegService = serviceLoss + optimalLossRecursive(..., nextHr, Service.REGULAR_SERVICE, -1);
        int nextFullService = serviceLoss + optimalLossRecursive(..., nextHr, Service.FULL_SERVICE, -3);

        int[] results = {nextNoService, nextMinService, nextRegService, nextFullService};
        int m = Integer.MAX_VALUE;
        for (int i: results) { if (i < m) { m = i; } }
        return m;
    }
```

## Dynamic pseudocode

```java
    public static int optimalLossDynamic(...) {
        if (totalHours == 0) { return 0; }
        ArrayList<...> lossMatrix = generateLossMatrix(...);
        // we want to return t=0 with a recent full service, or start of service
        ... // calculate all possible starting values
        return Math.min(...);
    }

    private static ArrayList<...> generateLossMatrix(...) {
        ArrayList<...> lossMatrix = new ArrayList<>();
        // our base case is currentHour==k then return 0
        for (int i=0; i < totalHours + 1; i++) {
            for (int j=0; j < 3; j++) {
                for (int k = 0; k < capacity; k++) {
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
                    ...
                    int finalLoss = thisLoss + Math.min(...);
                    lossMatrix.get(i).get(j).set(k, finalLoss);
                }
            }
        }
        return lossMatrix;
    }
```
