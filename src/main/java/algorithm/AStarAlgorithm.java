package algorithm;

import algorithm.heuristics.lowerbound.LowerBound;
import algorithm.heuristics.pruner.Arborist;
import common.graph.Edge;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.SimpleSchedule;
import common.schedule.Task;

import java.util.*;

/**
 * Algorithm implementation that will utilise the A* technique to generate an optimal schedule.
 */
public class AStarAlgorithm extends Algorithm {

    /**
     * Constructor for AStarAlgorithm class.
     * Defaults multithreading to false.
     * @param processors The number of processors
     */
    public AStarAlgorithm(int processors, Arborist arborist, LowerBound lowerBound) {
        super(processors, false, arborist, lowerBound);
    }

    @Override
    public void start(Graph graph) {

        SimpleSchedule emptySchedule = new SimpleSchedule(_processors);

        // TODO: Ensure TreeMaps allow duplicate key values...
        TreeMap<Integer, SimpleSchedule> schedulesToVisit = new TreeMap<>();
        schedulesToVisit.put(estimate(graph, emptySchedule, graph.getEntryPoints()), emptySchedule);

        while (!schedulesToVisit.isEmpty()) {
            Map.Entry<Integer, SimpleSchedule> integerScheduleEntry = schedulesToVisit.firstEntry();

            Integer min = integerScheduleEntry.getKey();
            SimpleSchedule curSchedule = integerScheduleEntry.getValue();


            // if the schedule is complete, it is optimal.
            if (curSchedule.size() == graph.size()) {
                _bestSchedule = curSchedule;
                return;
            }


            // finds all the nodes that are able to be added to the schedule, must have all their parents
            // already in the schedule.
            List<Node> nodesToAdd = new ArrayList<>();

            for (Node node : graph.getNodes()) {
                if (!curSchedule.contains(node)) {
                    boolean canAdd = true;
                    // a node can only be added to a schedule if all its parents have also been added to that schedule.
                    for (Edge edge : graph.getIncomingEdges(node)) {
                        Node parent = edge.getOriginNode();
                        if (!curSchedule.contains(parent)){
                            canAdd = false;
                        }
                    }
                    if (canAdd) {
                        nodesToAdd.add(node);
                    }
                }
            }




        }
         //f(n) is lower bound estimate rather than g(n) + h(n)
    }


}
