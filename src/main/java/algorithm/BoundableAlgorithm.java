package algorithm;

import algorithm.heuristics.lowerbound.LowerBound;
import algorithm.heuristics.pruner.Arborist;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.SimpleSchedule;
import common.schedule.Task;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;

public abstract class BoundableAlgorithm extends Algorithm {
    protected final MultiAlgorithmCommunicator _communicator;

    /**
     * Constructor that uses a notifier and global best that are defined elsewhere, so that this and other algorithms
     * may work together on a single graph.
     * @see Algorithm#Algorithm(Arborist, LowerBound) for arborists and pruners usage
     */
    public BoundableAlgorithm(Arborist arborist,
                              LowerBound lowerBound,
                              MultiAlgorithmCommunicator communicator,
                              AtomicReference<Schedule> globalBest) {
        super(arborist, lowerBound);
        this._communicator = communicator;
    }

    /**
     * Constructor that uses a dummy notifier so that the algorithm is runnable independently
     * @see Algorithm#Algorithm(Arborist, LowerBound)
     */
    public BoundableAlgorithm(Arborist arborist, LowerBound lowerBound) {
        super(arborist, lowerBound);
        // Note: Assumes that you will never call explore when just running by yourself. Not perfect but good enough.
        this._communicator = new MultiAlgorithmCommunicator();
    }

    public abstract void run(Graph graph, Schedule schedule, int depth, HashSet<Node> nextNodes);

    /**
     * Runs the algorithm normally (infinite depth, and starting schedule is empty)
     * @param graph A graph object representing tasks needing to be scheduled.
     */
    @Override
    public void run(Graph graph, int processors) {
        run(graph, new SimpleSchedule(processors), Integer.MAX_VALUE, new HashSet<>(graph.getEntryPoints()));
    }

    /**
     * @see Algorithm#getCurrentBest()
     */
    @Override
    public Schedule getCurrentBest() {
        return _communicator.getCurrentBest();
    }
}
