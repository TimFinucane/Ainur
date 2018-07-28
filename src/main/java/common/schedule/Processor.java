package common.schedule;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a list of Tasks in a sequence.
 */
public class Processor {

    private List<Task> _tasks;

    /**
     * Default constructor for a Processor.
     */
    public Processor(){
        _tasks = new ArrayList<>();
    }

    /**
     * Returns all the tasks currently assigned to this processor.
     * @return assigned tasks
     */
    public List<Task> getTasks() {
        return _tasks;
    }

    /**
     * Adds a task to the processor
     * @param task task to schedule
     */
    public void addTask(Task task){
        _tasks.add(task);
    }

    /**
     * Removes a task from the current schedule.
     * @param task task to remove.
     */
    public void removeTask(Task task) {
        _tasks.remove(task);
    }

    /**
     * Removes a task from the current schedule by specifying its index location
     * @param taskIndex index position of task to remove.
     */
    public void removeTask(int taskIndex) {
        _tasks.remove(taskIndex);
    }

    /**
     * Returns true if specified task is already in the Processor schedule.
     * @param task task to check for
     * @return true if task is scheduled on this Processor.
     */
    public boolean containsTask(Task task) {
        return _tasks.contains(task);
    }


}
