package common.schedule;

import java.util.List;

/**
 * Contains a list of Processors which in turn contain ordered Tasks.
 */
public class Schedule {

    private List<Processor> _processors;

    /**
     * Default constructor for a Schedule
     * @param processors processors for tasks to be scheduled on within this schedule.
     */
    public Schedule(List<Processor> processors) {
        this._processors = processors;
    }

    /**
     * Returns all the processors used in this Schedule.
     * @return processors in this schedule.
     */
    public List<Processor> get_processors() {
        return _processors;
    }
}
