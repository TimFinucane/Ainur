package algorithm.heuristics.pruner;

import algorithm.Helpers;
import common.graph.Edge;
import common.graph.Graph;
import common.schedule.Schedule;
import common.schedule.Task;

/**
 * Will prune a node if it can be placed on another processor earlier enough that all nodes place-able on the former
 * are place-able in the same positions on the latter.
 */
public class BetterStartPruner implements Arborist {
    public boolean prune(Graph graph, Schedule schedule, Task toBeAdded) {
        int earliests[] = Helpers.calculateEarliestTimes(graph, schedule, toBeAdded.getNode());

        int minimum = earliests[0];
        for(int i = 1; i < earliests.length; ++i)
            minimum = Math.min(minimum, earliests[i]);

        int maximumEdgeCost = 0;
        for(Edge edge : graph.getOutgoingEdges(toBeAdded.getNode()))
            maximumEdgeCost = Math.max(maximumEdgeCost, edge.getCost());

        return toBeAdded.getEndTime() > (minimum + toBeAdded.getNode().getComputationCost() + maximumEdgeCost);
    }
}
