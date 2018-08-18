package algorithm;

import common.graph.*;
import common.schedule.*;

import java.util.HashSet;

public class AlgorithmUtils {

    /**
     * Calculates the nodes that are valid to add to the current schedule.
     */
    public static HashSet<Node> calculateNextNodes(Graph graph, Schedule schedule) {

        HashSet<Node> unscheduledNodes = new HashSet<>();

        // finds all the nodes that have not yet been added to the schedule.
        for (Node node : graph.getNodes()) {
            if (schedule.findTask(node) == null) {
                unscheduledNodes.add(node);
            }
        }
        HashSet<Node> nodesToAdd = new HashSet<>(unscheduledNodes);

        // node can only be added next to the schedule if all its parents are in the schedule
        for (Node node : unscheduledNodes) {
            for (Edge edge : graph.getIncomingEdges(node)){
                Node parentNode = edge.getOriginNode();
                //if a parent is not in the schedule then this node cannot be added.
                if (!schedule.contains(parentNode)){
                    nodesToAdd.remove(node);
                    continue;
                }
            }
        }
        return nodesToAdd;
    }

    /**
     * Calculates the set of available nodes to add to the schedule, derived from adding the current node
     * to the schedule and using the old set of available nodes.
     * TODO: Optimize?
     */
    public static HashSet<Node> calculateNextNodes(Graph graph, Schedule schedule, HashSet<Node> oldNodes, Node added) {
        // Construct our new available nodes to pass on by copying available nodes and removing the one we're about
        // to add
        HashSet<Node> nextAvailableNodes = new HashSet<>(oldNodes);
        nextAvailableNodes.remove(added);

        // Now add all the children of the node we are visiting
        // Check that everything we add has all it's parents in the schedule.
        // There might be better code to do this or via method?
        for(Edge edge : graph.getOutgoingEdges(added)) {
            Node nodeToAdd = edge.getDestinationNode();

            boolean parentsInSchedule = true;
            for(Edge parentEdge : graph.getIncomingEdges(nodeToAdd)) {
                if (parentEdge.getOriginNode() != added && !schedule.contains(parentEdge.getOriginNode())) {
                    parentsInSchedule = false;
                    break;
                }
            }
            if(parentsInSchedule)
                nextAvailableNodes.add(nodeToAdd);
        }

        return nextAvailableNodes;
    }

    /**
     * Calculates the earliest times it can add the given node to each processor in the set
     */
    public static int[] calculateEarliestTimes(Graph graph, Schedule schedule, Node node) {
        // Calculate earliest it can be placed
        int[] earliests = new int[schedule.getNumProcessors()];
        for(Edge edge : graph.getIncomingEdges(node)) {
            Node dependencyNode = edge.getOriginNode();
            Task item = schedule.findTask(dependencyNode);

            // If it's on the same processor, just has to be after task end. If not, then it also needs
            // to be past the communication cost
            for(int processor = 0; processor < schedule.getNumProcessors(); ++processor) {
                if (item.getProcessor() == processor)
                    earliests[processor] = Math.max(earliests[processor], item.getEndTime());
                else
                    earliests[processor] = Math.max(earliests[processor], item.getEndTime() + edge.getCost());
            }
        }
        for(int processor = 0; processor < schedule.getNumProcessors(); ++processor)
            if(schedule.size(processor) > 0)
                earliests[processor] = Math.max(earliests[processor], schedule.getLatest(processor).getEndTime());

        return earliests;
    }
}
