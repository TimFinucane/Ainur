package algorithm;

import algorithm.heuristics.Arborist;
import algorithm.heuristics.LowerBound;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.Task;
import sun.nio.ch.ThreadPool;

import java.util.List;
import java.util.concurrent.Executors;

/**
 *  An abstract class which templates the algorithm to be implemented.
 */
public abstract class Algorithm {

    protected boolean _isComplete = false;

    protected int _processors;

    // Private as not modifiable by subclasses, use through prune() and estimate()
    private Arborist _arborist;
    private LowerBound _lowerBound;

    /**
     * Constructor for Algorithm class.
     * @param processors The number of processors.
     * @param arborist The pruning method to be available to the subclass
     * @param lowerBound The lower bound estimator method to be available to the subclass
     */
    protected Algorithm(int processors, Arborist arborist, LowerBound lowerBound) {
        _processors = processors;
        _arborist = arborist;
        _lowerBound = lowerBound;
    }

    /**
     * Starts the scheduling algorithm. When complete, the optimal schedule will be available in
     * getCurrentBest(). Note that before finishing, getCurrentBest() may store schedules, but they will not
     * necessarily be the most optimal ones.
     * @param graph A graph object representing tasks needing to be scheduled.
     */
    public abstract void start(Graph graph);

    /**
     * Lets the caller know whether or not the algorithm is complete
     * @return True if the algorithm is complete, false otherwise.
     */
    // This method is up for debate. May not be needed.
    public boolean isComplete() {
        return _isComplete;
    }

    // PROTECTED METHODS
    /**
     * Is called by subclass to apply separate pruning algorithm
     * @see Arborist#prune
     */
    protected boolean prune(Graph graph, Schedule schedule, Task processorTaskPair){
        return _arborist.prune(graph, schedule, processorTaskPair);
    }

    /**
     * Is called by subclass to apply separate lower bound estimation.
     * One method provides the optional hint nodesToVisit so that the estimator may know the immediately reachable
     * nodes that follow from the schedule.
     *
     * @see LowerBound#estimate
     */
    protected int estimate(Graph graph, Schedule schedule, List<Node> nodesToVisit){
        return _lowerBound.estimate(graph, schedule, nodesToVisit);
    }
    protected int estimate(Graph graph, Schedule schedule){
        return _lowerBound.estimate(graph, schedule);
    }
}
