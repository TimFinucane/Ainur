package algorithm;

import common.Graph;
import common.Schedule;

/**
 *  An abstract class which templates the algorithm to be implemented.
 */
public abstract class Algorithm {
    protected int _processors;
    protected boolean _multithreaded;

    /**
     * Constructor for Algorithm class.
     * @param processors The number of processors.
     * @param multithreaded Whether or not to use multithreading.
     */
    protected Algorithm(int processors, boolean multithreaded) {
        _processors = processors;
        _multithreaded = multithreaded;
    }

    /**
     * Starts the scheduling algorithm.
     * @param graph A graph object representing tasks needing to be scheduled.
     */
    public abstract void start(Graph graph);

    /**
     * Lets the caller know whether or not the algorithm is complete
     * @return True if the algorithm is complete, false otherwise.
     */
    // TODO Implement method
    // This method is up for debate. May not be needed.
    public boolean isComplete() { return false; }

    /**
     * Lets the caller know the current best schedule the algorithm has.
     * @return The current best schedule.
     */
    // TODO Implement method
    public Schedule getCurrentBest() { return null; }
}
