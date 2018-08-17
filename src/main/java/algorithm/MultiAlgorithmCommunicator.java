package algorithm;

import common.graph.Graph;
import common.graph.Node;
import common.schedule.*;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An interface for a BoundableAlgorithm to communicate with when it has found something to communicate about :)
 */
public class MultiAlgorithmCommunicator {
    protected final AtomicReference<Schedule>   _globalBest;
    private boolean initialisedGreedy = false;

    public MultiAlgorithmCommunicator(Schedule initialGuess) {
        _globalBest = new AtomicReference<>(initialGuess);
    }

    public MultiAlgorithmCommunicator() {
        // Create a schedule with max end time to simulate an infinitely large schedule.
        this(new SimpleSchedule(0){
            public int getEndTime() { return Integer.MAX_VALUE; }
        });
    }

    public Schedule getCurrentBest() {
        return _globalBest.get();
    }

    public synchronized void update(Schedule better) {
        _globalBest.updateAndGet((Schedule old) -> better.getEndTime() < old.getEndTime() ? better : old);
    }
    /**
     * Called when a boundable algorithm has reached its depth and wants the given partial schedule to be explored.
     * The default implementation throws as it is for solutions which do not require explorePartialSolution
     * @param schedule The partial schedule to explore
     */
    void explorePartialSolution(Schedule schedule, HashSet<Node> nextNodes) {
        throw new UnsupportedOperationException("Can't explore a partial solution with an undefined MultiAlgorithmCommunicator");
    }

    /**
     * Initialises a value for a greedy estimate of the current best schedule. Should only run once.
     * @param graph
     * @param processors
     */
    public void setGreedyInitialBest(Graph graph, int processors){
        if (!initialisedGreedy) {
            GreedyAlgorithm greedyAlgorithm = new GreedyAlgorithm();

            greedyAlgorithm.run(graph, processors);

            update(greedyAlgorithm.getCurrentBest());

            initialisedGreedy = true;
        }
    }
}
