package common.schedule;

import common.graph.Node;
import javafx.util.Pair;

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

}
