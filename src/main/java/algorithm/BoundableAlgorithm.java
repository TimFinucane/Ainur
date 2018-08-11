package algorithm;

import algorithm.heuristics.Arborist;
import algorithm.heuristics.LowerBound;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;

import java.util.List;

public abstract class BoundableAlgorithm extends Algorithm{
    private final MultiAlgorithmNotifier notifier;

    public BoundableAlgorithm(int processors, Arborist arborist, LowerBound lowerBound, MultiAlgorithmNotifier notifier) {
        super(processors, false, arborist, lowerBound);
        this.notifier = notifier;
    }

    public void start(Graph graph, Schedule schedule, int depth, List<Node> nextNodes){

    }

    public void updateSolution(Schedule schedule){

    }

}
