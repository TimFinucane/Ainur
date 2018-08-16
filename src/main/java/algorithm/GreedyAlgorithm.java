package algorithm;

import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.SimpleSchedule;
import common.schedule.Task;
import scala.Int;
import sun.awt.image.IntegerInterleavedRaster;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Class runs a greedy algorithm on a graph to produce a valid schedule. Logic is that for each itteration, the
 * algorithm looks at all nodes that are possible to place (ie. all nodes whose parents are all in schedule) and
 * chooses the one which will result in the smallest schedule run time.
 */
public class GreedyAlgorithm implements Algorithm {

    private Schedule _schedule = null;

    @Override
    public void run(Graph graph, int processors) {
        SimpleSchedule schedule = new SimpleSchedule(processors);
        List<Node> nodesToVisit = graph.getEntryPoints();

        // while there are still nodes that have not been added to the schedule
        while (!(nodesToVisit.isEmpty())) {
            Task bestTask = null;
            int bestTime = Integer.MAX_VALUE;
            // Go through all current nodes avilable and select the one that would give the smallest run time
           for(Node n : nodesToVisit){
               int[] earliestTimes = Helpers.calculateEarliestTimes(graph, schedule, n);
               // Looks at all the possible time that task can be added and find the earliest position
               for(int i = 0; i < processors; i++){
                   Task t = new Task(i, earliestTimes[i], n);
                   if(t.getEndTime() < bestTime){
                       bestTask = t;
                       bestTime = t.getEndTime();
                   }
               }
           }
           // Add the best tas kto schedule
            schedule.addTask(bestTask);

            // the next nodes to visit are now those accessible from the new best schedule.
            nodesToVisit = new ArrayList<>(Helpers.calculateNextNodes(graph, schedule, new HashSet<>(nodesToVisit), bestTask.getNode()));
        }

        _schedule = schedule;
    }

    @Override
    public Schedule getCurrentBest() {
        return null;
    }

    @Override
    public BigInteger branchesCulled() {
        return null;
    }

    @Override
    public BigInteger branchesExplored() {
        return null;
    }

    @Override
    public Node currentNode() {
        return null;
    }

    @Override
    public int lowerBound() {
        return 0;
    }

}
