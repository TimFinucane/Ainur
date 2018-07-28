package common.schedule;

import common.graph.Node;

/**
 * Stores the start time of when a Node from a Graph has been scheduled.
 */
public class Task {

    private final int _startTime;
    private final Node _node;

    /**
     * Default constructor for a Task object
     * @param startTime start time for scheduled execution of task
     * @param node Node associated to task.
     */
    public Task(int startTime, Node node) {
        _startTime = startTime;
        _node = node;
    }

    /**
     * Returns the start time of the execution of the Task
     * @return start time of execution
     */
    public int get_startTime() {
        return _startTime;
    }

    /**
     * Returns the Node associated to the task, which will reference associated nodes and a computation cost
     * @return Node associated to task.
     */
    public Node getNode() {
        return _node;
    }
}
