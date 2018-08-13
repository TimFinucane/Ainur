package algorithm;

import algorithm.heuristics.lowerbound.LowerBound;
import algorithm.heuristics.pruner.Arborist;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.SimpleSchedule;
import common.schedule.Task;
import javafx.util.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Class represents a tier on threads for when using multithreaded algorithms. Each tier looks through
 * a set number of nodes before moving on to the next partial schedule in the list
 */
public class TieredAlgorithm implements MultiAlgorithmNotifier, Algorithm {
    // This is a queue of all the schedules to be explored, as well as the next nodes to visit for each.
    private LinkedBlockingQueue<Pair<Schedule, HashSet<Node>>>   _schedulesToExplore;
    private AlgorithmFactory            _generator;
    private Thread[]                    _threads;
    private AtomicReference<Schedule>   _globalBest;

    private Graph                       _graph;

    public TieredAlgorithm(int processors, int threads, AlgorithmFactory generator, Schedule startingSchedule) {

        _generator = generator;
        _threads = new Thread[threads - 1];
        // Allow up to threads * 2 stored schedules before you cant add any more (and will block on trying to do so)
        _schedulesToExplore = new LinkedBlockingQueue<>((threads - 1) * 2);

        _globalBest = new AtomicReference<>(startingSchedule);
    }
    public TieredAlgorithm(int processors, int threads, AlgorithmFactory generator) {
        // Hacky way of creating an infinitely large fake schedule
        this(processors, threads, generator, new Schedule(0) {
            @Override
            public int getEndTime() { return Integer.MAX_VALUE; }
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

    /**
     * Starts running the tiered algorithm by setting up and starting the threads, then running a boundable
     * algorithm and pushing the partial schedules to a globally accessible list that each thread can
     * pick partial schedules off
     * @param graph A graph object representing tasks needing to be scheduled.
     * @param processors The number of processors in the output schedule
     */
    @Override
    public void run(Graph graph, int processors) {
        _graph = graph;
        for (int i = 0 ; i<_threads.length ; i++){
            _threads[i] = new Thread(this::runThread);
            _threads[i].start();
        }

        BoundableAlgorithm algorithm = _generator.create(0, this, _globalBest);
        algorithm.run(_graph, new SimpleSchedule(processors), 5, new HashSet<>(graph.getEntryPoints()));

        for(Thread t : _threads){
            t.interrupt();
        }
    }

    /**
     * Gets the current best schedule stored in the reference global best
     */
    @Override
    public Schedule getCurrentBest() {
        return _globalBest.get();
    }

    /**
     * @see MultiAlgorithmNotifier#explorePartialSolution(Schedule, HashSet)
     */
    @Override
    public void explorePartialSolution(Schedule schedule, HashSet<Node> nextNodes) {
        // We will try to add the above to the schedule. If theres not enough room (too many schedules to explore),
        // as it is obvious exploration is getting out of hand we will instead run it here, in this thread, RIGHT NOW!!!
        if(!_schedulesToExplore.offer(new Pair<>(schedule, nextNodes)))
            runAlgorithmOn(1, schedule, nextNodes);
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
                runAlgorithmOn(1, pair.getKey(), pair.getValue());
            }
        } catch(InterruptedException e) {
            return;
        }
    }

    /**
     * Runs an algorithm on a specified tier with a schedule to add to and a helpful list of nodes
     * to look at next
     * @param tier :
     * @param schedule : Schedule to add to
     * @param nextNodes : Helpful list of next nodes to look through
     */
    private void runAlgorithmOn(int tier, Schedule schedule, HashSet<Node> nextNodes) {
        BoundableAlgorithm algorithm =
            _generator.create(tier, this, _globalBest);

        algorithm.run(_graph, schedule, 9999, nextNodes);
    }
}
