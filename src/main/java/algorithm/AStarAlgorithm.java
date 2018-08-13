package algorithm;

import algorithm.heuristics.lowerbound.LowerBound;
import algorithm.heuristics.pruner.Arborist;
import common.graph.Edge;
import common.graph.Graph;
import common.graph.Node;
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

        // TODO: Ensure TreeMaps allow duplicate key values... (Doesn't look like it)
        TreeMap<Integer, SimpleSchedule> schedulesToVisit = new TreeMap<>();
        schedulesToVisit.put(estimate(graph, emptySchedule, graph.getEntryPoints()), emptySchedule);

        while (!schedulesToVisit.isEmpty()) {
            Map.Entry<Integer, SimpleSchedule> integerScheduleEntry = schedulesToVisit.firstEntry();

            SimpleSchedule curSchedule = integerScheduleEntry.getValue();

            // if the schedule is complete, it is optimal.
            if (curSchedule.size() == graph.size()) {
                _bestSchedule = curSchedule;
                return;
            }

            // nodes that still need to be added to the current schedule
            List<Node> nodesToAdd = new ArrayList<>();

            // finds all the nodes that can be added to the schedule
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

            // generate all new possible schedules by adding nodes with all parents visited to all possible processors.
            for (Node node : nodesToAdd) {
                for (int proc = 0; proc < _processors; proc++){

                    // find the earliest possible time the node can be added to current processor
                    int earliestPossStart = curSchedule.getEndTime(proc);

                    for (Edge edge : graph.getIncomingEdges(node)) {
                        Node parent = edge.getOriginNode();
                        Task parentTask = curSchedule.findTask(parent);

                        // if parent is not on the same processor as the child, find its communication cost.
                        if (parentTask.getProcessor() != proc) {
                            int communicationCost = parentTask.getEndTime() + edge.getCost();
                            if (earliestPossStart < communicationCost) {
                                earliestPossStart = communicationCost;
                            }
                        }
                    }

                    Task task = new Task(proc, earliestPossStart, node);

                    SimpleSchedule newSchedule = curSchedule;
                    newSchedule.addTask(task);

                    // TODO: add all new schedules to be visited only if they pass the heuristics.

                    List<Node> nextNodesToVisit = nodesToAdd;
                    nextNodesToVisit.remove(node);

                    if (prune(graph, newSchedule)){

                        int lowerBoundEstimate = estimate(graph, newSchedule, nextNodesToVisit);
                        schedulesToVisit.put(lowerBoundEstimate, newSchedule);
                    }
                }

            }

        }
    }


}
