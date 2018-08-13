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

public class TieredAlgorithm extends MultiAlgorithmCommunicator implements Algorithm {
    // This is a queue of all the schedules to be explored, as well as the next nodes to visit for each.
    private LinkedBlockingQueue<Pair<Schedule, HashSet<Node>>>   _schedulesToExplore;
    private AlgorithmFactory            _generator;
    private Thread[]                    _threads;

    private Graph                       _graph;

    /**
     * Does not get given a schedule to start with, it's initial guess is instead infinite.
     *
     * @see TieredAlgorithm#TieredAlgorithm(int, AlgorithmFactory, Schedule)
     */
    public TieredAlgorithm(int threads, AlgorithmFactory generator) {
        super();
        _generator = generator;
        _threads = new Thread[threads - 1];
        // Allow up to threads * 2 stored schedules before you cant add any more (and will block on trying to do so)
        _schedulesToExplore = new LinkedBlockingQueue<>((threads - 1) * 2);
    }
    /**
     * Create a tiered algorithm.
     * @param threads Number of threads to run algorithms in. Includes thread algorithm is started in
     * @param generator The factory used to generate the algorithms that will be used
     * @param initialGuess A schedule to start with as an initial guess
     */
    public TieredAlgorithm(int threads, AlgorithmFactory generator, Schedule initialGuess) {
        this(threads, generator);
        _globalBest.set(initialGuess);
    }

    @Override
    public void run(Graph graph, int processors) {
        _graph = graph;
        for (int i = 0 ; i<_threads.length ; i++){
            _threads[i] = new Thread(this::runThread);
            _threads[i].start();
        }

        BoundableAlgorithm algorithm = _generator.create(0, this);
        algorithm.run(_graph, new SimpleSchedule(processors), new HashSet<>(graph.getEntryPoints()));

        for(Thread t : _threads){
            t.interrupt();
        }
    }

    @Override
    public Schedule getCurrentBest() {
        return _globalBest.get();
    }

    /**
     * @see MultiAlgorithmCommunicator#explorePartialSolution(Schedule, HashSet)
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

    private void runAlgorithmOn(int tier, Schedule schedule, HashSet<Node> nextNodes) {
        BoundableAlgorithm algorithm = _generator.create(tier, this);

        algorithm.run(_graph, schedule, nextNodes);
    }
}
