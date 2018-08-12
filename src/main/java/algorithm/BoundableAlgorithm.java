package algorithm;

import algorithm.heuristics.Arborist;
import algorithm.heuristics.LowerBound;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.SimpleSchedule;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;

public abstract class BoundableAlgorithm extends Algorithm {
    protected final MultiAlgorithmNotifier      _notifier;
    protected final AtomicReference<Schedule>   _globalBest;

    public BoundableAlgorithm(Arborist arborist,
                              LowerBound lowerBound,
                              MultiAlgorithmNotifier notifier,
                              AtomicReference<Schedule> globalBest) {
        super(arborist, lowerBound);
        this._notifier = notifier;
        this._globalBest = globalBest;
    }

    public abstract void start(Graph graph, Schedule schedule, int depth, HashSet<Node> nextNodes);

    /**
     * Runs the algorithm normally (infinite depth, and starting schedule is empty)
     * @param graph A graph object representing tasks needing to be scheduled.
     */
    @Override
    public void run(Graph graph, int processors) {
        start(graph, new SimpleSchedule(processors), Integer.MAX_VALUE, new HashSet<>(graph.getEntryPoints()));
    }

    /**
     * @see Algorithm#getCurrentBest()
     */
    @Override
    public Schedule getCurrentBest() {
        return _globalBest.get();
    }
}
