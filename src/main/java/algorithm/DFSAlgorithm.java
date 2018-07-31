package algorithm;

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

    /**
     * Starts running the DFS.
     * N.B. This implementation is NOT multithreaded and will block upon running.
     * Solution works by exploring avery possible schedule configuration and returning the best one it has found.
     * Also uses heuristics for faster runtime.
     *
     * Schedule is then stored and can be provided by getCurrentBest()
     */
    @Override
    public void start(Graph graph) {}
}
