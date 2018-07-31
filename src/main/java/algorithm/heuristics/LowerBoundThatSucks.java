package algorithm.heuristics;

import common.graph.Graph;
import common.graph.Node;
import common.schedule.Processor;
import common.schedule.Schedule;
import common.schedule.Task;

import java.util.List;

/**
 * This class can be used as a complete but terrible lower bound estimator.
 * This particular lower bound estimator simply returns the length of the partial schedule provided
 */
public class LowerBoundThatSucks implements LowerBound {

    /**
     * @see LowerBound#estimate(Graph, Schedule, List)
     */
    public int estimate(Graph graph, Schedule schedule, List<Node> nextNodes) {
        // TODO: Upgrade sucky lower bound to one that at least gets the length of the schedule?
        return 0;
    }

}
