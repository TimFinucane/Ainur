package algorithm.implementations;

import algorithm.Algorithm;
import common.graph.Graph;

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
     * Constructor for DFSAlgorithm class.
     * Defaults multithreading to false.
     * @param processors The number of processors
     */
    public DFSAlgorithm(int processors) {
        super(processors);
    }

    // TODO Implement method
    @Override
    public void start(Graph graph) {}
}
