package algorithm;

import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.SimpleSchedule;

import java.util.HashSet;

public abstract class BoundableAlgorithm implements Algorithm {
    protected final MultiAlgorithmCommunicator _communicator;
    /**
     * Constructor that uses a notifier and global best that are defined elsewhere, so that this and other algorithms
     * may work together on a single graph.
     */

    public BoundableAlgorithm(MultiAlgorithmCommunicator communicator) {
        this._communicator = communicator;
    }
    /**
     * Constructor that uses a dummy notifier so that the algorithm is runnable independently
     */
    public BoundableAlgorithm() {
        // Note: Assumes that you will never call explore when just running by yourself. Not perfect but good enough.
        this._communicator = new MultiAlgorithmCommunicator();
    }

    public abstract void run(Graph graph, Schedule schedule, HashSet<Node> nextNodes);
    /**
     * Runs the algorithm normally (infinite depth, and starting schedule is empty)
     * @param graph A graph object representing tasks needing to be scheduled.
     */
    @Override
    public void run(Graph graph, int processors) {
        run(graph, new SimpleSchedule(processors), new HashSet<>(graph.getEntryPoints()));
    }

    /**
     * @see Algorithm#getCurrentBest()
     */
    @Override
    public Schedule getCurrentBest() {
        return _communicator.getCurrentBest();
    }
}
