package algorithm;

import common.Graph;

/**
 * A DFS implementation of the Algorithm class.
 */
public class DFSAlgorithm extends Algorithm {

    /**
     * Constructor for DFSAlgorithm class.
     * @param processors The number of processors.
     * @param multithreaded Whether or not to use multithreading.
     */
    public DFSAlgorithm(int processors, boolean multithreaded) {
        super(processors, multithreaded);
    }

    /**
     * Starts the scheduling algorithm. Uses DFS to calculate an optimal schedule.
     * @param graph A graph object representing tasks needing to be scheduled.
     */
    // TODO Implement method
    @Override
    public void start(Graph graph) {}
}
