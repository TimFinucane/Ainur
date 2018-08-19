package algorithm;

import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.SimpleSchedule;
import javafx.util.Pair;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Class represents a tier on threads for when using multithreaded algorithms. Each tier looks through
 * a set number of nodes before moving on to the next partial schedule in the list
 */

public class TieredAlgorithm extends MultiAlgorithmCommunicator implements Algorithm {
    // This is a queue of all the schedules to be explored, as well as the next nodes to visit for each.
    private LinkedBlockingQueue<Pair<Schedule, HashSet<Node>>> _schedulesToExplore;

    private Lock                        _waitForItemsLock = new ReentrantLock();
    private Lock                        _shutDownLock = new ReentrantLock();
    private AtomicInteger               _running = new AtomicInteger(0);
    private Thread[]                    _threads;

    private List<BoundableAlgorithm>    _algorithmsRunning;
    private AlgorithmFactory            _generator;
    private Graph                       _graph;

    private AtomicReference<BigInteger> _totalCulled = new AtomicReference<>(BigInteger.ZERO);
    private AtomicReference<BigInteger> _totalExplored = new AtomicReference<>(BigInteger.ZERO);

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
        _schedulesToExplore = new LinkedBlockingQueue<>(threads * 2);
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

        try {
            for (int i = 1; i < _threads.length; i++)
                _threads[i].join();
        } catch(InterruptedException ignored) {
            // If this happens, there was a problem. Most likely close was somehow called twice, which seems to happen in junit?
            // We should still have a correct answer anyway, so never mind it.
        }
    }

    /**
     * Gets the current best schedule stored in the reference global best
     */
    @Override
    public Schedule getCurrentBest() {
        return super.getCurrentBest();
    }

    /**
     * @see Algorithm#branchesCulled()
     */
    @Override
    public BigInteger branchesCulled() {
        // Add the sum of nodes culled by algorithms that have finished running
        BigInteger sum = _totalCulled.get();

        // Add culled from the currently running algorithms
        for (BoundableAlgorithm algorithm : _algorithmsRunning) {
            sum = sum.add(algorithm.branchesCulled());
        }
        return sum;
    }

    /**
     * @see Algorithm#branchesExplored()
     */
    @Override
    public BigInteger branchesExplored() {
        // Add the sum of nodes explored by algorithms that have finished running
        BigInteger sum = _totalExplored.get();

        // Add explored from the currently running algorithms
        for (BoundableAlgorithm algorithm : _algorithmsRunning) {
            sum = sum.add(algorithm.branchesExplored());
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

    public int numThreads() {
        return _threads.length;
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
     * @see MultiAlgorithmCommunicator#explorePartialSolution(Graph, Schedule, HashSet)
     */
    @Override
    public void explorePartialSolution(Graph graph, Schedule schedule, HashSet<Node> nextNodes) {
        // We will try to add the above to the schedule. If theres not enough room (too many schedules to explore),
        // as it is obvious exploration is getting out of hand we will instead run it here, in this thread, RIGHT NOW!!!
        // TODO: Tiers are only ever 0 or 1. Change?
        if(!_schedulesToExplore.offer(new Pair<>(schedule, nextNodes)))
            runAlgorithmOn(calculateTier(schedule), graph, schedule, nextNodes);
    }

    /**
     * Runs a single thread, on which algorithms will be created to deal with
     * partial solutions as they come.
     */
    private void runThread() {
        // Effectively a while true loop (loop forever)
        try {
            while (!Thread.interrupted()) {
                tryClose();

                Pair<Schedule, HashSet<Node>> pair;
                _waitForItemsLock.lockInterruptibly();
                try {
                    // Ensure the operation of taking a schedule and saying we are running is atomic, as we want to
                    // say we are running the instant we have a schedule to run.
                    pair = _schedulesToExplore.take();
                    _running.incrementAndGet();
                } finally {
                    _waitForItemsLock.unlock();
                }

                runAlgorithmOn(calculateTier(pair.getKey()), _graph, pair.getKey(), pair.getValue());
                _running.decrementAndGet();
            }
        } catch(InterruptedException ignored) {
            // If we have been interrupted, we are meant to finish running
        }
    }

    /**
     * Runs an algorithm on a specified tier with a schedule to add to and a helpful list of nodes
     * to look at next
     * @param tier : The tier of the algorithm
     * @param graph : The graph
     * @param schedule : Schedule to add to
     * @param nextNodes : Helpful list of next nodes to look through
     */
    private void runAlgorithmOn(int tier, Graph graph, Schedule schedule, HashSet<Node> nextNodes) {
        BoundableAlgorithm algorithm = _generator.create(tier, this);
        // Add algorithm to running algorithm list
        _algorithmsRunning.add(algorithm);

        algorithm.run(graph, schedule, nextNodes);

        // Increment counters
        _totalExplored.accumulateAndGet(algorithm.branchesExplored(), BigInteger::add);
        _totalCulled.accumulateAndGet(algorithm.branchesCulled(), BigInteger::add);

        // When the algorithm has finished running must remove from list so its values are not used to calculate
        // other values
        _algorithmsRunning.remove(algorithm);
    }

    /**
     * Gets the tier the given schedule should run on. (Hint: Its the size of the schedule)
     */
    private int calculateTier(Schedule schedule) {
        return schedule.size();
    }

    /**
     * Checks whether it is time to close threads. If it is, then it will close threads.
     */
    private void tryClose() throws InterruptedException {
        _shutDownLock.lockInterruptibly(); // Ensure no two threads both try to close.
        try {
            // If running is 0, nothing is in the middle of running an algorithm, and we can exit.
            if(_schedulesToExplore.isEmpty() && _running.get() == 0) {
                _running.decrementAndGet(); // To -1, to signify finished.
                for (Thread thread : _threads)
                    thread.interrupt(); // Yes, it is safe to call this on yourself, it'll get picked up later
            }
        } finally {
            _shutDownLock.unlock();
        }
    }
}
