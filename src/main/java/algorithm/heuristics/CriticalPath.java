package algorithm.heuristics;

import common.graph.Edge;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.Task;

import java.util.*;

/**
 *  Class defines the logic for finding the critical path of a tree. It finds the longest path to the bottom from
 *  the root node
 */
public class CriticalPath implements LowerBound {

    /**
     * Method provides an estimate of the lower bound based on the critical path of the subgraph consisting of all
     * the nodes that have not yet been added to a schedule.
     * @param graph : Entire graph
     * @param schedule : Partial schedule generated up until this point
     * @param nextNodesToVisit : All the nodes that can be added imminently to the partial schedule
     * @return estimate : int
     */
    public int estimate(Graph graph, Schedule schedule, List<Node> nextNodesToVisit) {

        // Map to store Nodes and the critical path to reach that node.
        Map<Node, Integer> nodePathWeights = new HashMap<>();
        // Nodes that have already been scheduled.
        List<Node> scheduledNodes = getScheduledNodes(schedule);

        // First nodes to be visited should be entry point nodes
        List<Node> unvisitedNodes = new ArrayList<>(nextNodesToVisit);

        // Populate the queue with all the nodes that have not yet been added to the schedule
        for (Node node : graph.getNodes()) {
            if (!scheduledNodes.contains(node) && !nextNodesToVisit.contains(node)) {
                unvisitedNodes.add(node);
            }
        }

        // Only iterate through when there are still nodes that have not been analysed.
        while (!unvisitedNodes.isEmpty()) {
            //inspect the element currently at the head of the queue but do not remove
            Node currentNode = unvisitedNodes.get(0);

            List<Integer> pathWeights = new ArrayList<>();
            boolean canVisit = true;

            //check to see that all parents of this node have been visited or are already scheduled
            for (Edge incomingEdge : graph.getIncomingEdges(currentNode)) {
                Node parent = incomingEdge.getOriginNode();

                // finds the weight of the critical path up until that parent.
                if (nodePathWeights.containsKey(parent)){
                    pathWeights.add(nodePathWeights.get(parent));
                }
                // if parents haven't already been visited and they're not in the schedule, node cannot be visited.
                else if (!scheduledNodes.contains(parent)) {
                    canVisit = false;
                    break;
                }
            }

            if (canVisit) { // add critical path value of node to the map
                if (pathWeights.isEmpty()) { // if node is a root in the subgraph just add own cost.
                    nodePathWeights.put(currentNode, currentNode.getComputationCost());
                } else { // put the max of the paths from a parent plus the node's own cost.
                    nodePathWeights.put(currentNode, Collections.max(pathWeights) + currentNode.getComputationCost());
                }

                unvisitedNodes.remove(0);

            } else { // if node's parents have not all been visited, node must be revisited later
                //node is shifted from head to back of the queue
                unvisitedNodes.remove(0);
                unvisitedNodes.add(currentNode);
            }
        }

        // return the maximum of all computed critical paths.
        return Collections.max(nodePathWeights.values());

    }

    /**
     * Helper method to find all the nodes that have already been visited in the given schedule.
     * @param schedule schedule containing nodes
     * @return nodes in the schedule
     */
    private List<Node> getScheduledNodes(Schedule schedule){
        List<Node> scheduledNodes = new ArrayList<>();

        // Generates a list storing all the nodes that have already been scheduled at some point
        for (int i = 0; i < schedule.getNumProcessors(); ++i) {
            for (Task task : schedule.getTasks(i)) {
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
        throw new UnsupportedOperationException();
    }
}
