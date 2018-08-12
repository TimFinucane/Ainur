package algorithm;

import common.graph.Node;
import common.schedule.Schedule;

import java.util.HashSet;

/**
 * An interface for a BoundableAlgorithm to communicate with when it has found something to communicate about :)
 */
public interface MultiAlgorithmNotifier {
    /**
     * Called when a boundable algorithm has reached its depth and wants the given partial schedule
     * to be explored.
     * @param schedule The partial schedule to explore
     */
    void explorePartialSolution(Schedule schedule, HashSet<Node> nextNodes);
}