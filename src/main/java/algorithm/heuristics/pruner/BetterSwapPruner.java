package algorithm.heuristics.pruner;

import algorithm.AlgorithmUtils;
import common.graph.Edge;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.Task;

import java.util.HashSet;

/**
 * The better swap pruner sees whether swapping the given task and the predecessor on the processor
 * results in a better schedule. It is really useful for join graphs (resulted in an approx 10x speedup).
 */
public class BetterSwapPruner implements Arborist {

    @Override
    public boolean prune(Graph graph, Schedule schedule, Task toBeAdded) {
        if(schedule.size(toBeAdded.getProcessor()) == 0)
            return false;

        // Find the immediately preceding task on the schedule
        Task precedingTask = schedule.getLatest(toBeAdded.getProcessor());

        // Ensure that the preceding task is not our parent
        for(Edge edge : graph.getIncomingEdges(toBeAdded.getNode()))
            if(edge.getOriginNode() == precedingTask.getNode())
                return false;

        // Check they have the same children
        HashSet<Node> toBeAddedChildren = new HashSet<>();
        for(Edge edge : graph.getOutgoingEdges(toBeAdded.getNode())) {
            toBeAddedChildren.add(edge.getDestinationNode());
        }
        for(Edge edge : graph.getOutgoingEdges(precedingTask.getNode())) {
            if(toBeAddedChildren.contains(edge.getDestinationNode()))
                toBeAddedChildren.remove(edge.getDestinationNode());
            else
                return false;
        }

        // Find earlies start for us, and then place them after us
        schedule.removeTask(precedingTask);
        int start = AlgorithmUtils.calculateEarliestTime(graph, schedule, toBeAdded.getNode(), toBeAdded.getProcessor());
        schedule.addTask(precedingTask);

        // Get tasks for the alternative
        Task alternativeAdded = new Task(toBeAdded.getProcessor(), start, toBeAdded.getNode());
        Task alternativePreceding = new Task(toBeAdded.getProcessor(), alternativeAdded.getEndTime(), precedingTask.getNode());

        if(alternativePreceding.getEndTime() > toBeAdded.getEndTime())
            return false; // TODO: Any weaker requirement than this?

        // Confirm that under every child, the alternative is better or equal
        boolean atBestEqual = true; // Is the alternative exactly equal to the current proposition?
        for(Edge edge : graph.getOutgoingEdges(toBeAdded.getNode())) {
            int addedComCost = edge.getCost();
            int precedingComCost = communicationCost(graph, precedingTask.getNode(), edge.getDestinationNode());

            int thisBest = Math.max(toBeAdded.getEndTime() + addedComCost, precedingTask.getEndTime() + precedingComCost);
            int theirBest = Math.max(alternativeAdded.getEndTime() + addedComCost, alternativePreceding.getEndTime() + precedingComCost);

            if(thisBest > theirBest)
                atBestEqual = false;
            if(thisBest < theirBest) // We are better, phew! We dont have to worry with this nonsense
                return false;
        }

        // If they are better, prune! If they are at best equal, choose the one to use based on Id ordering.
        return (!atBestEqual) || toBeAdded.getNode().getId() < precedingTask.getNode().getId();

    }

    private int communicationCost(Graph graph, Node from, Node to) {
        for(Edge edge : graph.getOutgoingEdges(from)) {
            if(edge.getDestinationNode() == to)
                return edge.getCost();
        }
        return Integer.MAX_VALUE;
    }
}
