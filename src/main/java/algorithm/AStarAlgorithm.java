package algorithm;

import algorithm.heuristics.lowerbound.LowerBound;
import algorithm.heuristics.pruner.Arborist;
import common.graph.Graph;

/**
 * Algorithm implementation that will utilise the A* technique to generate an optimal schedule.
 */
public class AStarAlgorithm extends Algorithm {

    /**
     * Constructor for AStarAlgorithm class.
     * Defaults multithreading to false.
     * @param processors The number of processors
     */
    public AStarAlgorithm(int processors, Arborist arborist, LowerBound lowerBound) {
        super(processors, false, arborist, lowerBound);
    }

    @Override
    public void start(Graph graph) {

    }
}
