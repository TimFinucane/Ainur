package algorithm;

import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;

/**
 *  An abstract class which templates the algorithm to be implemented.
 */
public interface Algorithm {
    /**
     * Starts the scheduling algorithm. When complete, the optimal schedule will be available in
     * getCurrentBest(). Note that before finishing, getCurrentBest() may store schedules, but they will not
     * necessarily be the most optimal ones.
     * @param graph A graph object representing tasks needing to be scheduled.
     * @param processors The number of processors in the output schedule
     */
    void run(Graph graph, int processors);

    /**
     * Gets the current best schedule the algorithm is inspecting.
     *
     * @return The current best schedule.
     */
    Schedule getCurrentBest();

    /**
     * Gets the number of branches that were culled via pruning and lower bound.
     *
     * @return The number of culled branches that occurred
     */
    int branchesCulled();

    /**
     * Gets the number of branches that were explored by the algorithm.
     *
     * @return The number of branches explored
     */
    int branchesExplored();

    /**
     * Gets the current node being explored.
     *
     * @return The current node being explored.
     */
    Node currentNode();

    /**
     * Finds the minimum possible length of a solution. When complete, should be equal to the current best end time.
     */
    int lowerBound();
}
