package algorithm.heuristics.lowerbound;

import algorithm.AlgorithmUtils;
import common.graph.Edge;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;

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
        // Ensures that schedule end time is returned if it is less than the critical path
        nodePathWeights.put(null, schedule.getEndTime());

        List<Node> nodesToVisit = new ArrayList<>();

        // All parents are in the schedule, so we can get start time through normal means
        for(Node currentNode : nextNodesToVisit) {
            int earliests[] = AlgorithmUtils.calculateEarliestTimes(graph, schedule, currentNode);

            int startTime = earliests[0];
            for(int proc = 1; proc < earliests.length; ++proc)
                startTime = Math.min(startTime, earliests[proc]);

            nodePathWeights.put(currentNode, startTime + currentNode.getComputationCost());

            // Add children of the current node, now that we have visited it.
            for(Edge edge : graph.getOutgoingEdges(currentNode))
                nodesToVisit.add(edge.getDestinationNode());
        }

        // Only iterate through when there are still nodes that have not been analysed.
        outerNodeLoop:
        while (!nodesToVisit.isEmpty()) {
            //inspect the element currently at the head of the queue but do not remove
            Node currentNode = nodesToVisit.get(0);
            nodesToVisit.remove(0);

            // Already been searched, mate.
            if(nodePathWeights.containsKey(currentNode))
                continue;

            // The path weight will be the maximum length path to get here, plus this nodes cost.
            // Start by getting max path weight
            int potentialPathWeight = 0;

            // Finds the (estimated) earliest this node can exist in our schedule, based on where parent nodes exist.
            for (Edge incomingEdge : graph.getIncomingEdges(currentNode)) {
                Node parent = incomingEdge.getOriginNode();

                // If the parent is in nodepathweights, use that calculation, if its in schedule we can just get its end time.
                if (nodePathWeights.containsKey(parent))
                    potentialPathWeight = Math.max(potentialPathWeight, nodePathWeights.get(parent));
                else if (schedule.contains(parent))
                    potentialPathWeight = Math.max(potentialPathWeight, schedule.findTask(parent).getEndTime());
                else // If neither, we can't visit this node yet
                    continue outerNodeLoop; // Skip all below code, move to next node.
            }

            // The real path weight will be the maximum potential path weight + the weight of the node. This is the
            // earliest the current node could end at when placed in the schedule
            potentialPathWeight += currentNode.getComputationCost();
            nodePathWeights.put(currentNode, potentialPathWeight);

            // Add children of the current node, now that we have visited it.
            for(Edge edge : graph.getOutgoingEdges(currentNode))
                nodesToVisit.add(edge.getDestinationNode());
        }

        // return the maximum of all computed critical paths.
        return Collections.max(nodePathWeights.values());
    }
}
