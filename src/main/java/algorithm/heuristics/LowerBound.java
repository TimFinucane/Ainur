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

    /***
     * Method provides an estimate of the lower bound based on parameters provided
     * @param graph
     * @param schedule
     * @param nodesToVisit
     * @return estimate : int
     */
    int estimate(Graph graph, Schedule schedule, List<Node> nodesToVisit);

    /**
     * Method provides an estimate of the lower bound based on parameters provided
     * @param graph
     * @param schedule
     * @return estimate : int
     */
    default int estimate(Graph graph, Schedule schedule){
        return this.estimate(graph, schedule, new ArrayList<Node>());
    }

}
