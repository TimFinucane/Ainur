package algorithm.heuristics.lowerbound;

import algorithm.heuristics.lowerbound.LowerBound;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;

import java.util.HashSet;
import java.util.List;

/**
 * This class can be used as a complete lower bound estimator.
 * This particular lower bound estimator simply returns 0.
 */
public class NaiveBound implements LowerBound {

    /**
     * @see LowerBound#estimate(Graph, Schedule, HashSet)
     */
    public int estimate(Graph graph, Schedule schedule, HashSet<Node> nextNodes) {
        // TODO: Upgrade lower bound to one that at least gets the length of the schedule?
        return 0;
    }

}
