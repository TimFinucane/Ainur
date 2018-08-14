package algorithm;

import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.SimpleSchedule;
import common.schedule.Task;

import java.util.List;

public class GreedyAlgorithm implements Algorithm {

    @Override
    public void run(Graph graph, int processors) {
        SimpleSchedule schedule = new SimpleSchedule(processors);
        List<Node> nodesToVisit = graph.getEntryPoints();

        while (!(nodesToVisit.isEmpty())){
            Node currentNode = nodesToVisit.get(0);
            nodesToVisit.remove(currentNode);


            // Check that all parents are there


            // For each node, try it on each processor
            for (int i = 0; i<processors ; i++){

            }
        }
    }

    @Override
    public Schedule getCurrentBest() {
        return null;
    }

    @Override
    public int branchesCulled() {
        return 0;
    }

    @Override
    public int branchesExplored() {
        return 0;
    }

    @Override
    public Node currentNode() {
        return null;
    }
}
