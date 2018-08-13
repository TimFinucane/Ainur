package algorithm;

import common.graph.Graph;
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

    Schedule getCurrentBest();
}
