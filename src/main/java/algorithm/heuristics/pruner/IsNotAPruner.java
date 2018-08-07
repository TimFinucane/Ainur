package algorithm.heuristics.pruner;

import algorithm.heuristics.pruner.Arborist;
import common.graph.Graph;
import common.schedule.Schedule;
import common.schedule.Task;

/**
 * Stub class that implements Arborist - to be used in place of fully implemented pruning algorithms
 */
public class IsNotAPruner implements Arborist {

    /**
     * @param graph : Schedule
     * @param schedule : Schedule
     * @return boolean : boolean
     */
    public boolean prune(Graph graph, Schedule schedule, Task toBeAdded) {
        return false;
    }
}
