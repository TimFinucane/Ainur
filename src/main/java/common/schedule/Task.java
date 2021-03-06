package common.schedule;

import common.graph.Node;

/**
 * Stores the start time of when a Node from a Graph has been scheduled.
 */
public class Task {
    private final int   _processor;
    private final int   _startTime;
    private final Node  _node;

    /**
     * Default constructor for a Task object
     * @param processor the processor on which the task is placed
     * @param startTime start time for scheduled execution of task
     * @param node Node associated to task.
     */
    public Task(int processor, int startTime, Node node) {
        _processor = processor;
        _startTime = startTime;
        _node = node;
    }

    /**
     * The processor which the task is on in the schedule
     */
    public int getProcessor() {
        return _processor;
    }
    /**
     * Returns the start time of the execution of the Task
     * @return start time of execution
     */
    public int getStartTime() {
        return _startTime;
    }

    /**
     * Returns the Node associated to the task, which will reference associated nodes and a computation cost
     * @return Node associated to task.
     */
    public Node getNode() {
        return _node;
    }

    /**
     * Returns the scheduled end time of an assigned task.
     * @return time Task will have finished on its processor.
     */
    public int getEndTime(){
        return _startTime + _node.getComputationCost();
    }
}
