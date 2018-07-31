package algorithm;

import algorithm.Algorithm;
import algorithm.heuristics.Arborist;
import algorithm.heuristics.LowerBound;
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
    public DFSAlgorithm(int processors, boolean multithreaded, Arborist arborist, LowerBound lowerBound) {
        super(processors, multithreaded, arborist, lowerBound);
    }

    /**
     * Constructor for DFSAlgorithm class.
     * Defaults multithreading to false.
     * @param processors The number of processors
     */
    public DFSAlgorithm(int processors, Arborist arborist, LowerBound lowerBound) {
        super(processors, false, arborist, lowerBound);
    }

    // TODO Implement method
    @Override
    public void start(Graph graph) {}
}
