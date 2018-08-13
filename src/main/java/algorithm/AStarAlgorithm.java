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

            // only add schedules to the list to visit if they pass the heuristic tests
            // add each possible node to each possible processor
            for (int proc = 0; proc < _processors; proc++){
                for (Node node : nodesToAdd) {
                    // find the earliest possible time the node can be added to a processor taking into account
                    // edge communication costs.
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

                }

            }

        }
    }


}
