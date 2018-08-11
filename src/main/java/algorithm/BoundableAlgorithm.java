package algorithm;

import algorithm.heuristics.Arborist;
import algorithm.heuristics.LowerBound;
import common.graph.Edge;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.SimpleSchedule;
import common.schedule.Task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public abstract class BoundableAlgorithm extends Algorithm {
    protected final MultiAlgorithmNotifier      _notifier;
    protected final AtomicReference<Schedule>   _globalBest;

    public BoundableAlgorithm(Arborist arborist,
                              LowerBound lowerBound,
                              MultiAlgorithmNotifier notifier,
                              AtomicReference<Schedule> globalBest) {
        super(globalBest.get().getNumProcessors(), arborist, lowerBound);
        this._notifier = notifier;
        this._globalBest = globalBest;
    }

    public abstract void start(Graph graph, Schedule schedule, int depth, HashSet<Node> nextNodes);

    /**
     * Runs the algorithm normally (infinite depth, and starting schedule is empty)
     * @param graph A graph object representing tasks needing to be scheduled.
     */
    @Override
    public void start(Graph graph) {
        start(graph, new SimpleSchedule(_processors), Integer.MAX_VALUE, new HashSet<>(graph.getEntryPoints()));
    }

}
