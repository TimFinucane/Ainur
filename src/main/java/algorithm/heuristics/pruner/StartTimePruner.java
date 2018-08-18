package algorithm.heuristics.pruner;

import common.graph.Graph;
import common.schedule.Schedule;
import common.schedule.Task;

/**
 * Pruner that compares starting times of tasks in in question with latest finishing task in the current schedule.
 */
public class StartTimePruner implements Arborist {

    /**
     * This pruning method works by comparing the start times of the "in adding phase" task, and the current latest
     * finishing task in the schedule. If the current task is determined to have an start time before that of the
     * current latest task start time, the task being added in question can be disregarded.
     *
     * TODO: Add proof
     *
     * @param graph : Schedule
     * @param schedule : Schedule
     * @return boolean : boolean
     */
    public boolean prune(Graph graph, Schedule schedule, Task toBeAdded) {
        // If the current added task starts BEFORE the current latest finishing task starts in the schedule
        Task latest = schedule.getLatest();
        return latest != null && (toBeAdded.getStartTime() < latest.getStartTime()
            || (toBeAdded.getStartTime() == latest.getStartTime() // If the two start at the same time, choose the one with lowest id
            && toBeAdded.getNode().getId() < latest.getNode().getId()
            && latest.getNode().getComputationCost() == 0)); // If computation cost is 0, don't cull as one could depend on the other
    }
}
