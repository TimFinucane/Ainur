package algorithm.heuristics;

import common.graph.Graph;
import common.schedule.Processor;
import common.schedule.Schedule;
import common.schedule.Task;
import javafx.util.Pair;

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
    public boolean prune(Graph graph, Schedule schedule, Pair<Processor, Task> toBeAdded) {

        // The time that the current latest finishing task in the schedule starts
        int latestTaskStart = 0;

        // This is a bit messy, add functionality for Processor to do this?
        for (Processor processor : schedule.getProcessors()) {
            for (Task task : processor.getTasks()) {
                if (latestTaskStart < task.getStartTime()) {
                    latestTaskStart = task.getStartTime();
                }
            }
        }

        // Get starting time of task to add
        Task taskToBeAdded = toBeAdded.getValue();
        int taskToBeAddedStartTime = taskToBeAdded.getStartTime();

        // If the current added task starts BEFORE the current latest finishing task starts in the schedule
        return latestTaskStart > taskToBeAddedStartTime;

    }
}
