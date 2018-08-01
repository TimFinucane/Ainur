package algorithm.heuristics;

import common.graph.Graph;
import common.graph.Node;
import common.schedule.Processor;
import common.schedule.Schedule;
import common.schedule.Task;

import java.security.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *  Class defines the logic for finding the critical path of a tree. It finds the longest path to the bottom from
 *  the root node
 */
public class CriticalPath implements LowerBound {

    /**
     * Method provides an estimate of the lower bound based on parameters provided
     * @param graph : Graph
     * @param schedule : Schedule
     * @param nextNodesToVisit : List<Node>
     * @return estimate : int
     */
    //TODO - implementation
    public int estimate(Graph graph, Schedule schedule, List<Node> nextNodesToVisit) {

        List<Node> scheduledNodes = getScheduledNodes(schedule);

        return 0;
    }

    /**
     * Helper method to find all the nodes that have already been placed in the given schedule.
     * @param schedule schedule containing nodes
     * @return nodes in the schedulegit st
     */
    private List<Node> getScheduledNodes(Schedule schedule){
        List<Node> scheduledNodes = new ArrayList<>();

        // Generates a list storing all the nodes that have already been scheduled at some point
        for (Processor processor : schedule.getProcessors()) {
            for (Task task : processor.getTasks()) {
                scheduledNodes.add(task.getNode());
            }
        }
        return scheduledNodes;
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
