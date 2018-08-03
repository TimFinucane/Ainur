package algorithm.heuristics;

import common.graph.Graph;
import common.schedule.Processor;
import common.schedule.Schedule;
import common.schedule.Task;
import javafx.util.Pair;

/**
 * Stub class that implements Arborist - to be used in place of fully implemented pruning algorithms
 */
public class IsNotAPruner implements Arborist {

    /**
     * @param graph : Schedule
     * @param schedule : Schedule
     * @return boolean : boolean
     */
    public boolean prune(Graph graph, Schedule schedule, Pair<Processor, Task> processorTaskPair) {
        return false;
    }
}
