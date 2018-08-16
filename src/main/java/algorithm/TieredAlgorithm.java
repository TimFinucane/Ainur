package algorithm;

import common.graph.*;
import common.schedule.*;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Class represents a tier on threads for when using multithreaded algorithms. Each tier looks through
 * a set number of nodes before moving on to the next partial schedule in the list
 */

public class TieredAlgorithm extends MultiAlgorithmCommunicator implements Algorithm {
    // This is a queue of all the schedules to be explored, as well as the next nodes to visit for each.
    private LinkedBlockingQueue<Pair<Schedule, HashSet<Node>>>   _schedulesToExplore;
    private List<BoundableAlgorithm>    _algorithmsRunning;
    private AlgorithmFactory            _generator;
    private Thread[]                    _threads;

    private Graph                       _graph;

    private int                         _totalCulled;
    private int                         _totalExplored;

    /**
     * Does not get given a schedule to start with, it's initial guess is instead infinite.
     *
     * @see TieredAlgorithm#TieredAlgorithm(int, AlgorithmFactory, Schedule)
     */
    public TieredAlgorithm(int threads, AlgorithmFactory generator) {
        super();
        _generator = generator;
        _threads = new Thread[threads];
        _algorithmsRunning = new CopyOnWriteArrayList<>();
        // Allow up to threads * 2 stored schedules before you cant add any more (and will block on trying to do so)
        _schedulesToExplore = new LinkedBlockingQueue<>((threads) * 2);
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

        // Add the empty schedule as first
        _schedulesToExplore.add(new Pair<>(new SimpleSchedule(processors), new HashSet<>(graph.getEntryPoints())));

        _threads[0] = Thread.currentThread();
        for (int i = 1 ; i<_threads.length ; i++) {
            _threads[i] = new Thread(this::runThread);
            _threads[i].start();
        }

        // Now run on this thread as if it is one of our own:
        runThread();
    }

    /**
     * Gets the current best schedule stored in the reference global best
     */
    @Override
    public Schedule getCurrentBest() {
        return _globalBest.get();
    }

    /**
     * @see Algorithm#branchesCulled()
     */
    @Override
    public int branchesCulled() {
        // Add the sum of nodes culled by algorithms that have finished running
        int sum = _totalCulled;

        // Add culled from the currently running algorithms
        for (BoundableAlgorithm algorithm : _algorithmsRunning) {
            sum += algorithm.branchesCulled();
        }
        return sum;
    }

    /**
     * @see Algorithm#branchesExplored()
     */
    @Override
    public int branchesExplored() {
        // Add the sum of nodes explored by algorithms that have finished running
        int sum = _totalExplored;

        // Add explored from the currently running algorithms
        for (BoundableAlgorithm algorithm : _algorithmsRunning) {
            sum += algorithm.branchesExplored();
        }
        return sum;
    }

    /**
     * @see Algorithm#currentNode()
     */
    @Override
    public Node currentNode() {
        // Since several algorithms can be running concurrently just select a node from a random running algorithm
        if ( _algorithmsRunning.size() == 0)
            return null;
        return _algorithmsRunning.get(new Random().nextInt(_algorithmsRunning.size())).currentNode();
    }

    /**
     * @see Algorithm#lowerBound()
     */
    @Override
    public int lowerBound() {
        int minBound = getCurrentBest().getEndTime();
        for(Algorithm algorithm : _algorithmsRunning)
            minBound = Math.min(minBound, algorithm.lowerBound());
        return minBound;
    }

    /**
     * Gets the list of current nodes being examined by each currently running algorithm
     *
     * @return A list of nodes being examined by each currently running algorithm.
     *      The list will be empty if no algorithms are currently running.
     */
    public List<Node> currentNodes() {
        List<Node> nodeList = new ArrayList<>();
        for (BoundableAlgorithm algorithm : _algorithmsRunning) {
            nodeList.add(algorithm.currentNode());
        }

        return nodeList;
    }

    /**
     * @see MultiAlgorithmCommunicator#explorePartialSolution(Schedule, HashSet)
     */
    @Override
    public void explorePartialSolution(Schedule schedule, HashSet<Node> nextNodes) {
        // We will try to add the above to the schedule. If theres not enough room (too many schedules to explore),
        // as it is obvious exploration is getting out of hand we will instead run it here, in this thread, RIGHT NOW!!!
        // TODO: Tiers are only ever 0 or 1. Change?
        if(!_schedulesToExplore.offer(new Pair<>(schedule, nextNodes)))
            runAlgorithmOn(calculateTier(schedule), schedule, nextNodes);
    }

    /**
     * Runs a single thread, on which algorithms will be created to deal with
     * partial solutions as they come.
     */
    private void runThread() {
        // Thread only stops when the algorithm is claimed to be complete
        try {
            while(!Thread.interrupted()) {
                // If there are no more schedules either running or waiting to be run, close.
                if(_schedulesToExplore.isEmpty() && _algorithmsRunning.isEmpty())
                    closeAll(); // Closes all threads, including this one, safely via interrupts.

                // Try and get a schedule. Blocks until this occurs
                Pair<Schedule, HashSet<Node>> pair = _schedulesToExplore.take();
                runAlgorithmOn(calculateTier(pair.getKey()), pair.getKey(), pair.getValue());
            }
        } catch(InterruptedException ignored) {
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
        BoundableAlgorithm algorithm = _generator.create(tier, this);

        // Add alogorithm to running algorithm list
        _algorithmsRunning.add(algorithm);
        algorithm.run(_graph, schedule, nextNodes);

        // Increment counters
        _totalExplored += algorithm.branchesExplored();
        _totalCulled += algorithm.branchesCulled();

        // When the algorithm has finished running must remove from list so its values are not used to calculate
        // other values
        _algorithmsRunning.remove(algorithm);
    }

    /**
     * Gets the tier the given schedule should run on.
     * TODO: Currently only ever gives 0 if empty or 1 otherwise. More tiers?
     */
    private int calculateTier(Schedule schedule) {
        return schedule.size() == 0 ? 0 : 1;
    }

    /**
     * Closes all threads. Should only be called once threads have stopped running algorithms, otherwise some threads
     * may continue running algorithms after this object has apparently finished.
     */
    private void closeAll() {
        for(Thread thread : _threads)
            thread.interrupt();
    }
}
