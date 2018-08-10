package algorithm;

import algorithm.heuristics.Arborist;
import algorithm.heuristics.LowerBound;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;

import java.util.List;

public abstract class BoundableAlgorithm extends Algorithm{

    protected BoundableAlgorithm(int processors, boolean multithreaded, Arborist arborist, LowerBound lowerBound) {
        super(processors, multithreaded, arborist, lowerBound);
    }

    public void BoundableAlgorithm(boolean onFound, boolean onComplete){

    }

    public void start(Graph graph, Schedule schedule, int depth, List<Node> nextNodes){

    }

    public void updateSolution(Schedule schedule){

    }

}
