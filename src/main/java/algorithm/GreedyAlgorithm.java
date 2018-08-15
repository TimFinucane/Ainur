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

        Node rootNode = nodesToVisit.get(0);
        int initCost = nodesToVisit.get(0).getComputationCost();

        // place the node with the smallest computation cost first in the schedule
        for (Node node : nodesToVisit) {
            if (node.getComputationCost() < initCost){
                initCost = node.getComputationCost();
                rootNode = node;
            }
        }
        // add the node with smallest cost into start of schedule.
        schedule.addTask(new Task(0, 0, rootNode));

        // next nodes to visit are all those accessible once root node has been added
        nodesToVisit = new ArrayList<>(Helpers.calculateNextNodes(graph, schedule, new HashSet<>(nodesToVisit), rootNode));

        // while there are still nodes that have not been added to the schedule
        while (!(nodesToVisit.isEmpty())) {

            int bestEndTime = Integer.MAX_VALUE;
            SimpleSchedule curBestSchedule = new SimpleSchedule(processors);
            Node nodeAdded = null;

            //try adding all nodes to all possible schedules and pick the shortest one.
            for (Node node : nodesToVisit) {
                int[] earliestTimes = Helpers.calculateEarliestTimes(graph, schedule, node);

                //add all nodes to all different processors
                for (int proc = 0; proc < processors; proc++) {

                    //generate a schedule with current node placed at best time on current processor.
                    SimpleSchedule newSchedule = schedule;
                    newSchedule.addTask(new Task(proc, earliestTimes[proc], node));

                    // if this schedule has the current best end time, store it.
                    if (newSchedule.getEndTime() < bestEndTime) {
                        bestEndTime = newSchedule.getEndTime();
                        curBestSchedule = newSchedule;
                        nodeAdded = node;
                    }
                }
            }

            schedule = curBestSchedule;

            // the next nodes to visit are now those accessible from the new best schedule.
            nodesToVisit = new ArrayList<>(Helpers.calculateNextNodes(graph, schedule, new HashSet<>(nodesToVisit), nodeAdded));
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
