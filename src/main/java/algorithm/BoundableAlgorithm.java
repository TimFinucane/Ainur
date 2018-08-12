package algorithm;

import algorithm.heuristics.Arborist;
import algorithm.heuristics.LowerBound;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.SimpleSchedule;
import common.schedule.Task;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;

public abstract class BoundableAlgorithm extends Algorithm {
    protected final MultiAlgorithmNotifier      _notifier;
    protected final AtomicReference<Schedule>   _globalBest;

    /**
     * Constructor that uses a notifier and global best that are defined elsewhere, so that this and other algorithms
     * may work together on a single graph.
     * @see Algorithm#Algorithm(Arborist, LowerBound) for arborists and pruners usage
     */
    public BoundableAlgorithm(Arborist arborist,
                              LowerBound lowerBound,
                              MultiAlgorithmNotifier notifier,
                              AtomicReference<Schedule> globalBest) {
        super(arborist, lowerBound);
        this._notifier = notifier;
        this._globalBest = globalBest;
    }

    /**
     * Constructor that uses a dummy notifier so that the algorithm is runnable independently
     * @see Algorithm#Algorithm(Arborist, LowerBound)
     */
    public BoundableAlgorithm(Arborist arborist, LowerBound lowerBound) {
        super(arborist, lowerBound);
        this._notifier = new MultiAlgorithmNotifier() {
            public void onSolutionFound(Schedule schedule) {
                _globalBest.set(schedule);
            }
            public void explorePartialSolution(Schedule schedule, HashSet<Node> nextNodes) {
                throw new UnsupportedOperationException("Trying to explore solution separately with no owning algorithm");
            }
        };
        this._globalBest = new AtomicReference<>(null);
    }

    public abstract void run(Graph graph, Schedule schedule, int depth, HashSet<Node> nextNodes);

    /**
     * Runs the algorithm normally (infinite depth, and starting schedule is empty)
     * @param graph A graph object representing tasks needing to be scheduled.
     */
    @Override
    public void run(Graph graph, int processors) {
        // Set the schedule if it has not already been set.
        if(_globalBest.get() == null) {
            Schedule schedule = new SimpleSchedule(processors);
            // TODO: Find simpler way of doing this?
            schedule.addTask(new Task(0, Integer.MAX_VALUE, new Node(0, "", 0)));
            _globalBest.set(schedule);
        }
        run(graph, new SimpleSchedule(processors), Integer.MAX_VALUE, new HashSet<>(graph.getEntryPoints()));
    }

    /**
     * @see Algorithm#getCurrentBest()
     */
    @Override
    public Schedule getCurrentBest() {
        return _globalBest.get();
    }
}
