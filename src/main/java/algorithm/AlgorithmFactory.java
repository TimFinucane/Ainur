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
     * @param communicator A communicator to pass to the BoundableAlgorithm
     */
    BoundableAlgorithm create(int tier, MultiAlgorithmCommunicator communicator);
}
