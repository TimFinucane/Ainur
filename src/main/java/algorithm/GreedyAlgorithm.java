package algorithm;

import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.SimpleSchedule;
import common.schedule.Task;
import scala.Int;
import sun.awt.image.IntegerInterleavedRaster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class GreedyAlgorithm implements Algorithm {

    private Schedule _schedule = null;

    @Override
    public void run(Graph graph, int processors) {
        SimpleSchedule schedule = new SimpleSchedule(processors);
        List<Node> nodesToVisit = graph.getEntryPoints();

        // while there are still nodes that have not been added to the schedule
        while (!(nodesToVisit.isEmpty())) {
            // Get current node we're gonna add to the schedule
            Node curNode = nodesToVisit.get(0);
            int[] earliestTimes = Helpers.calculateEarliestTimes(graph, schedule, curNode);
            // Looks at all the possible time that task can be added and find the earliest position
            Task bestTask = new Task(0, 0, new Node(Integer.MAX_VALUE, "a", 1));
            for(int i = 0; i < processors; i++){
                Task t = new Task(i, earliestTimes[i], curNode);
                if(t.getEndTime() < bestTask.getEndTime()){
                    bestTask = t;
                }
            }

            schedule.addTask(bestTask);

            // the next nodes to visit are now those accessible from the new best schedule.
            nodesToVisit = new ArrayList<>(Helpers.calculateNextNodes(graph, schedule, new HashSet<>(nodesToVisit), bestTask.getNode()));
        }

        _schedule = schedule;
    }

    @Override
    public Schedule getCurrentBest() {
        return _schedule;
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
