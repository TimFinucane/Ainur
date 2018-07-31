package algorithm.heuristics;

import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;

import java.util.ArrayList;
import java.util.List;


/**
 * Interface is to be implemented by classes who provide logic to find a lower bound of a graph
 */
public interface LowerBound {

    /**
     * Efficiently and heuristically estimates a lower bound for the best possible time of a solution
     * created using the partial schedule provided.
     * @param graph The entire graph of the problem
     * @param schedule A partial/incomplete schedule from which the range of solutions is based
     * @param nodesToVisit An optional list of all nodes that can be added immediately to the schedule
     *                     (i.e. all dependencies accounted for in the partial schedule).
     * @return A lower bound of the optimal solution length
     */
    int estimate(Graph graph, Schedule schedule, List<Node> nodesToVisit);

    /**
     * Efficiently and heuristically estimates a lower bound for the best possible time of a solution
     * created using the partial schedule provided.
     * @param graph The entire graph of the problem
     * @param schedule A partial/incomplete schedule from which the range of solutions is based
     * @return A lower bound of the optimal solution length
     */
    default int estimate(Graph graph, Schedule schedule){
        return this.estimate(graph, schedule, new ArrayList<>());
    }

}
