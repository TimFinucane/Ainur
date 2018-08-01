package algorithm.heuristics;

import common.graph.Graph;
import common.schedule.Schedule;
import common.schedule.Task;

/**
 * This is Busters Pruner class
 */
public class BustersPruner implements Arborist {

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

        // The time that the current latest finishing task in the schedule starts
        int latestTaskStart = schedule.getTotalTime();

        // TODO: sort out what this is
        int taskStartTime = 0;

        // If the current added task starts BEFORE the current latest finishing task starts in the schedule
        if (latestTaskStart > taskStartTime) {
            return true; // Prune
        } else {
            return false; // Don't prune
        }
    }
}
