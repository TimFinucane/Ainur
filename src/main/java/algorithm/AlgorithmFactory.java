package algorithm;

import common.schedule.Schedule;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Used by a TieredAlgorithm to create algorithms to run on certain parts of the application
 */
public interface AlgorithmFactory {
    /**
     * Creates a boundable algorithm of your choice
     * @param tier The tier on which the algorithm will be operating
     * @param processors The number of processors the solution must be on
     * @param notifier A notifier to pass to the BoundableAlgorithm
     * @param globalBest A reference to the schedule which will contain the best solution
     */
    BoundableAlgorithm create(int tier,
                              int processors, // May not need this if we have globalBest?
                              MultiAlgorithmNotifier notifier,
                              AtomicReference<Schedule> globalBest);
}
