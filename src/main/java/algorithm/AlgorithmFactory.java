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
     * @param notifier A notifier to pass to the BoundableAlgorithm
     * @param globalBest A reference to the schedule which will contain the best solution
     */
    BoundableAlgorithm create(int tier,
                              MultiAlgorithmNotifier notifier,
                              AtomicReference<Schedule> globalBest);
}
