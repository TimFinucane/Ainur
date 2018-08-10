package algorithm;

import algorithm.heuristics.Arborist;
import algorithm.heuristics.LowerBound;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;

import java.util.List;

public class TieredAlgorithm extends Algorithm {

    public void TieredAlgorithm(AlgorithmFactory generator){

    }

    protected TieredAlgorithm(int processors, boolean multithreaded, Arborist arborist, LowerBound lowerBound) {
        super(processors, multithreaded, arborist, lowerBound);
    }

    @Override
    public void start(Graph graph, Schedule schedule, int depth, List<Node> nextNodes) {

    }

    private void onFound(Schedule partialSchedule){

    }

    private void onComplete(Thread thread, Schedule completeSchedule){

    }
}
