package algorithm;

import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.Task;
import javafx.util.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class TieredAlgorithm extends Algorithm implements MultiAlgorithmNotifier {
    // This is a queue of all the schedules to be explored, as well as the next nodes to visit for each.
    private LinkedBlockingQueue<Pair<Schedule, HashSet<Node>>>   _schedulesToExplore;
    private AlgorithmFactory            _generator;
    private int                         _threads;
    private AtomicReference<Schedule>   _globalBest;

    private Graph                       _graph;

    public TieredAlgorithm(int processors, int threads, AlgorithmFactory generator, Schedule startingSchedule) {
        super(processors, null, null);

        _generator = generator;
        _threads = threads;
        // Allow up to threads * 2 stored schedules before you cant add any more (and will block on trying to do so)
        _schedulesToExplore = new LinkedBlockingQueue<>(threads * 2);

        _globalBest = new AtomicReference<>(startingSchedule);
    }
    public TieredAlgorithm(int processors, int threads, AlgorithmFactory generator) {
        // Hacky way of creating an infinitely large fake schedule
        this(processors, threads, generator, new Schedule(0) {
            @Override
            public int getEndTime() { return 1000000; } // TODO: Too small?
            @Override
            public void addTask(Task task) {}
            @Override
            public void removeTask(Task task) {}
            @Override
            public Task findTask(Node node) { return null; }
            @Override
            public Task getLatest(int processor) { return null; }
            @Override
            public List<Task> getTasks(int processor) { return null; }
            @Override
            public int size(int processor) { return 0; }
        });
    }

    @Override
    public void start(Graph graph) {

    }

    /**
     * @see MultiAlgorithmNotifier#onSolutionFound(Schedule)
     */
    @Override
    public void onSolutionFound(Schedule schedule) {

    }

    /**
     * @see MultiAlgorithmNotifier#notifyPartialSolution(Schedule, HashSet)
     */
    @Override
    public void notifyPartialSolution(Schedule schedule, HashSet<Node> nextNodes) {

    }

    /**
     * Runs a single thread, on which algorithms will be created to deal with
     * partial solutions as they come.
     */
    private void runThread() {
        // Thread only stops when the algorithm is claimed to be complete
        try {
            while (true) {
                // Try and get a schedule
                Pair<Schedule, HashSet<Node>> pair = _schedulesToExplore.take();

                BoundableAlgorithm algorithm =
                    _generator.create(1, pair.getKey().getNumProcessors(), this, _globalBest);

                algorithm.start(_graph, pair.getKey(), 9999, pair.getValue());
            }
        } catch(InterruptedException e) {
            return;
        }
    }
}
