package common.schedule;

import common.graph.Node;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contains a list of Processors which in turn contain ordered Tasks.
 */
public class Schedule {

    private List<Processor> _processors;

    /**
     * Default constructor for a Schedule
     * @param processors number of processors for tasks to be scheduled on within this schedule.
     */
    public Schedule(int processors) {
        _processors = new ArrayList<>();
        for (int i = 0; i < processors; i++){
            _processors.add(new Processor());
        }
    }

    /**
     * Returns all the processors used in this Schedule.
     * @return processors in this schedule.
     */
    public List<Processor> getProcessors() {
        return _processors;
    }

    /**
     * Finds the processor and task associated to a particular node.
     * @param node node to find on the schedule.
     * @return Processor running the task associated to input node, associated task
     */
    public Pair<Processor, Task> findTask(Node node) {
        for (Processor processor: _processors) {
            Task task = processor.findTask(node);
            if (task != null) {
                return new Pair<>(processor, task);
            }
        }
        return null;
    }

    /**
     * Finds the latest task end time across all the processors in the schedule.
     * @return latest task finishing time.
     */
    public int getTotalTime(){
        List<Integer> endTimes = new ArrayList<>();
        for (Processor processor : _processors) {
            // Find the most recently scheduled task and it's finishing time
            Task task = processor.getLatestTask();
            endTimes.add(task != null ? task.getEndTime() : 0);
        }
        // Returns the maximum of all the processor end times.
        return Collections.max(endTimes);
    }

    /**
     * Gets number of tasks currently placed in the schedule
     */
    public int size() {
        int total = 0;
        for(Processor processor : _processors)
            total += processor.getTasks().size();
        return total;
    }
}
