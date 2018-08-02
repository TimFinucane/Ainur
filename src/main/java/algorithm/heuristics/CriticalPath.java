package algorithm.heuristics;

import common.graph.Edge;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Processor;
import common.schedule.Schedule;
import common.schedule.Task;

import java.security.Provider;
import java.util.*;

/**
 *  Class defines the logic for finding the critical path of a tree. It finds the longest path to the bottom from
 *  the root node
 */
public class CriticalPath implements LowerBound {

    private Map<Node, Integer> nodePathWeights = new HashMap<>();

    private List<Node> scheduledNodes;

    /**
     * Method provides an estimate of the lower bound based on the critical path of the subgraph consisting of all
     * the nodes that have not yet been added to a schedule.
     * @param graph : Graph entire graph
     * @param schedule : Partial schedule generated up until this point
     * @param nextNodesToVisit : All the nodes that can be added imminently to the partial schedule
     * @return estimate : int
     */
    //TODO - implementation
    public int estimate(Graph graph, Schedule schedule, List<Node> nextNodesToVisit) {

        scheduledNodes = getScheduledNodes(schedule);

        Queue<Node> unvisitedNodes = new PriorityQueue<>(nextNodesToVisit);

        boolean canVisit = true;

        while (!unvisitedNodes.isEmpty()) {
            //inspect the element currently at the head of the queue
            Node currentNode = unvisitedNodes.element();

            List<Integer> pathWeights = new ArrayList<>();

            //check to see that all parents of this node have been visited or are already scheduled
            for (Edge incomingEdge : graph.getIncomingEdges(currentNode)) {
                Node parent = incomingEdge.getOriginNode();

                // if parents haven't already been visited and they're not in the schedule, node cannot be visited.
                if (!scheduledNodes.contains(parent) && !nodePathWeights.containsKey(parent)) {
                    canVisit = false;
                }
                // finds the weight of the critical path up until that parent.
                if (nodePathWeights.containsKey(parent)){
                    pathWeights.add(nodePathWeights.get(parent));
                }
            }

            if (canVisit) { // add critical path value of node to the map
                if (pathWeights.isEmpty()) { // if node is a root in the subgraph just add own cost.
                    nodePathWeights.put(currentNode, currentNode.getComputationCost());
                } else { // put the max of the paths from a parent plus the node's own cost.
                    nodePathWeights.put(currentNode, Collections.max(pathWeights) + currentNode.getComputationCost());
                }

                //now add all its children to unvisited if they have not yet been visited
                for (Edge outgoingEdge : graph.getOutgoingEdges(currentNode)) {
                    Node childNode = outgoingEdge.getDestinationNode();
                    if (!nodePathWeights.containsKey(childNode)) {
                        unvisitedNodes.add(childNode);
                    }
                }

                unvisitedNodes.remove();

            } else { // if node's parents have not all been visited, node must be revisited later
                unvisitedNodes.remove();
                //node is shifted from head to back of the queue
                unvisitedNodes.add(currentNode);
            }
            canVisit = true;
        }

        // return the maximum of all computed critical paths.
        return Collections.max(nodePathWeights.values());

    }

    /**
     * Helper method to find all the nodes that have already been visited in the given schedule.
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
