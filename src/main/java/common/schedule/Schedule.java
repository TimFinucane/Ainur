package common.schedule;

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
}
