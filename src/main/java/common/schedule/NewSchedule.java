package common.schedule;

import common.graph.Node;

import java.util.List;

/**
 * Represents a placed list of nodes on processors, with defined start and end times.
 */
abstract class NewSchedule {

    protected int _numProcessors;

    protected NewSchedule(int numProcessors) {
        _numProcessors = numProcessors;
    }

    /**
     * Adds the given task to the schedule.
     */
    abstract public void        addTask(Task task);

    /**
     * Finds the task in the schedule associated with the given node.
     * @return A task if one with the given node exists in the Schedule, otherwise null.
     */
    abstract public Task        findTask(Node node);

    /**
     * Returns whether the given node is in the schedule as a task
     */
    public boolean              contains(Node node) {
        return findTask(node) != null;
    }

    /**
     * Gets the latest task in the given processor
     */
    abstract public Task        getLatest(int processor);
    /**
     * Gets the latest task in the entire schedule (where latest is defined as the task whose start is latest)
     */
    public Task                 getLatest() {
        int latestStart = 0;
        Task latest = null;

        for(int i = 0; i < _numProcessors; ++i) {
            Task latestOnProcessor = getLatest(i);
            if(latestOnProcessor != null && latestOnProcessor.getStartTime() > latestStart) {
                latest = latestOnProcessor;
                latestStart = latestOnProcessor.getStartTime();
            }
        }

        return latest;
    }

    /**
     * Gets the time at which all processing on the given processor is finished
     */
    public int                  getEndTime(int processor) {
        Task latestTask = getLatest(processor);
        return latestTask != null ? latestTask.getEndTime() : 0;
    }
    /**
     * Gets the time at which all processing is finished
     */
    public int                  getEndTime() {
        // Note here that getLatest() returns the task whose start is latest and so should NOT be used for the purpose
        //  of getting the end time.
        // Because life is complicated :)
        int endTime = 0;

        for(int i = 0; i < _numProcessors; ++i)
            endTime = Math.max(endTime, getEndTime(i));

        return endTime;
    }

    /**
     * Gets all the tasks, in order, on the given processor.
     * N.B., this seems to be the least used method, please keep it that way just in case there are further
     * optimisations RE not maintaining a full list of tasks in order.
     */
    abstract public List<Task>  getTasks(int processor);

    /**
     * Gets the number of placed tasks in the schedule
     */
    abstract public int         size(int processor);
    public int                  size() {
        int total = 0;
        for(int i = 0; i < _numProcessors; ++i)
            total += size(i);

        return total;
    }

    /**
     * This may or may not get the number of processors, may fail if the computer is not working.
     */
    public int                  getNumProcessors() {
        return _numProcessors;
    }

}
