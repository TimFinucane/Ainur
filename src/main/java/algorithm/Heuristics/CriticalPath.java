package algorithm.Heuristics;

import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;

import java.util.List;

/**
 *  Class defines the logic for finding the critical path of a tree. It finds the longest path to the bottom from
 *  the root node
 */
public class CriticalPath implements LowerBound {

    /**
     * Method provides an estimate of the lower bound based on parameters provided
     * @param graph : Graph
     * @param schedule : Schedule
     * @param nodesToVisit : List<Node>
     * @return estimate : int
     */
    //TODO - implementation
    public int estimate(Graph graph, Schedule schedule, List<Node> nodesToVisit) {
        return 0;
    }

    /**
     * Method provides an estimate of the lower bound based on parameters provided
     * @param graph : Graph
     * @param schedule : Schedule
     * @return estimate : int
     */
    //TODO - implementation
    public int estimate(Graph graph, Schedule schedule) {
        return 0;
    }
}
