package algorithm.heuristics;

import common.graph.Graph;
import common.schedule.Schedule;
import common.schedule.Task;

/**
 * This is Busters Pruner class
 */
public class BustersPruner implements Arborist {

    /**
     *
     * @param graph : Schedule
     * @param schedule : Schedule
     * @return boolean : boolean
     */
    public boolean prune(Graph graph, Schedule schedule, Task toBeAdded) {

        // The time that the current latest finishing task in the schedule finishes
        int latestTaskFinish = schedule.getTotalTime();

        // TODO: sort out what this is
        int finishingTaskTime = 0;

        // If the current added task finishes BEFORE the current latest finish in the schedule
        if (latestTaskFinish > finishingTaskTime) {
            return true; // Prune
        } else {
            return false; // Don't prune
        }
    }
}
