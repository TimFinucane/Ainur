package algorithm;

import algorithm.heuristics.Arborist;
import algorithm.heuristics.LowerBound;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.Task;

import java.util.List;

/**
 *  An abstract class which templates the algorithm to be implemented.
 */
public abstract class Algorithm {

    protected Schedule _bestSchedule = null; // Is settable by subclasses. TODO: thread safety
    protected boolean _isComplete = false;

    protected int _processors;
    protected boolean _multithreaded;

    // Private as not modifiable by subclasses, use through prune() and estimate()
    private Arborist _arborist;
    private LowerBound _lowerBound;

    /**
     * Constructor for Algorithm class.
     * @param processors The number of processors.
     * @param multithreaded Whether or not to use multithreading.
     * @param arborist The pruning method to be available to the subclass
     * @param lowerBound The lower bound estimator method to be available to the subclass
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
     * @param arborist The pruning method to be available to the subclass
     * @param lowerBound The lower bound estimator method to be available to the subclass
     */
    protected Algorithm(int processors, Arborist arborist, LowerBound lowerBound) {
        this(processors, false, arborist, lowerBound);
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
    public boolean isComplete() { return _isComplete; }

    /**
     * Lets the caller know the current best schedule the algorithm has.
     * If isComplete() is true (i.e. the algorithm has finished) then this will return the optimal solution
     * If the implementation has not yet come up with a best schedule, it will be null.
     * @return The current best schedule. May be null.
     */
    public Schedule getCurrentBest() { return _bestSchedule; }

    // PROTECTED METHODS
    /**
     * Is called by subclass to apply separate pruning algorithm
     * @see Arborist#prune
     */
    protected boolean prune(Graph graph, Schedule schedule, Task toBeAdded){
        return _arborist.prune(graph, schedule, toBeAdded);
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
