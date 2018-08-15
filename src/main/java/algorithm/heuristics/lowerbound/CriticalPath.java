package algorithm.heuristics.lowerbound;

import algorithm.Helpers;
import algorithm.heuristics.lowerbound.LowerBound;
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
    public int estimate(Graph graph, Schedule schedule, HashSet<Node> nextNodesToVisit) {

        // Map to store Nodes and the critical path to reach that node.
        Map<Node, Integer> nodePathWeights = new HashMap<>();

        List<Node> nodesToVisit = new ArrayList<>(nextNodesToVisit);

        // Only iterate through when there are still nodes that have not been analysed.
        outerNodeLoop:
        while (!nodesToVisit.isEmpty()) {
            //inspect the element currently at the head of the queue but do not remove
            Node currentNode = nodesToVisit.get(0);
            nodesToVisit.remove(0);

            List<Integer> potentialPathWeights = new ArrayList<>();

            // Finds the (estimated) earliest this node can exist in our schedule, based on where parent nodes exist.
            for (Edge incomingEdge : graph.getIncomingEdges(currentNode)) {
                Node parent = incomingEdge.getOriginNode();

                // If the parent is in nodepathweights, use that calculation, if its in schedule we can just get its end time.
                if (nodePathWeights.containsKey(parent)){
                    potentialPathWeights.add(nodePathWeights.get(parent));
                }
                else if (schedule.contains(parent)) {
                    potentialPathWeights.add(schedule.findTask(parent).getEndTime());
                }
                // If it's in neither, this node can't be visited yet. We will put it back in the list and move to
                // the next node
                else {
                    nodesToVisit.add(currentNode);
                    continue outerNodeLoop; // Skip all below code, move to next node.
                }
            }

            // The real path weight will be the maximum potential path weight + the weight of the node. This is the
            // earliest the current node could end at when placed in the schedule
            int pathWeight = currentNode.getComputationCost();
            if (!potentialPathWeights.isEmpty())
                pathWeight += Collections.max(potentialPathWeights);

            nodePathWeights.put(currentNode, pathWeight);

            // Add children of the current node, now that we have visited it.
            for(Edge edge : graph.getOutgoingEdges(currentNode))
                nodesToVisit.add(edge.getDestinationNode());
        }

        // return the maximum of all computed critical paths.
        return Collections.max(nodePathWeights.values());
    }
}
