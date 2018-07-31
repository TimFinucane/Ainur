package algorithm;

import algorithm.heuristics.Arborist;
import algorithm.heuristics.LowerBound;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;

import java.util.List;

/**
 *  An abstract class which templates the algorithm to be implemented.
 */
public abstract class Algorithm {
    protected int _processors;
    protected boolean _multithreaded;
    protected Arborist _arborist;
    protected LowerBound _lowerBound;

    /**
     * Constructor for Algorithm class.
     * @param processors The number of processors.
     * @param multithreaded Whether or not to use multithreading.
     */
    protected Algorithm(int processors, boolean multithreaded, Arborist arborist, LowerBound lowerBound) {
        _processors = processors;
        _multithreaded = multithreaded;
        _arborist = arborist;
        _lowerBound = lowerBound;
    }

    /**
     * Constructor for Algorithm class.
     * Defaults multithreading to false.
     * @param processors The number of processors
     */
    protected Algorithm(int processors, Arborist arborist, LowerBound lowerBound) { this(processors, false, arborist, lowerBound); }

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

    protected boolean prune(Graph graph, Schedule schedule){
        return _arborist.prune(graph, schedule);
    }

    protected int estimate(Graph graph, Schedule schedule, List<Node> nodesToVisit){
        return _lowerBound.estimate(graph, schedule, nodesToVisit);
    }

    protected int estimate(Graph graph, Schedule schedule){
        return _lowerBound.estimate(graph, schedule);
    }
}
