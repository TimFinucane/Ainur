package algorithm;

import algorithm.heuristics.Arborist;
import algorithm.heuristics.LowerBound;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;

import java.util.List;

public class TieredAlgorithm extends Algorithm implements MultiAlgorithmNotifier {

    protected TieredAlgorithm(int processors, AlgorithmFactory generator) {

        super(processors, null, null);
    }

    @Override
    public void start(Graph graph) {

    }

    /**
     * @see MultiAlgorithmNotifier#onComplete(BoundableAlgorithm)
     */
    @Override
    public void onComplete(BoundableAlgorithm completedAlgorithm) {

    }

    /**
     * @see MultiAlgorithmNotifier#onSolutionFound(Schedule)
     */
    @Override
    public void onSolutionFound(Schedule schedule) {

    }

    /**
     * @see MultiAlgorithmNotifier#notifyPartialSolution(Schedule)
     */
    @Override
    public void notifyPartialSolution(Schedule schedule) {

    }
}
