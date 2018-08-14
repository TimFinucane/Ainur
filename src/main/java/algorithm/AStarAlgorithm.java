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
public class AStarAlgorithm extends BoundableAlgorithm {

    private int _depth;
    protected Arborist _arborist;
    protected LowerBound _lowerBound;

    /**
     * Constructor for DFSAlgorithm class.
     * @param arborist : A pruner to use in algorithm
     * @param lowerBound : A lower-bound to use in algorithm
     * @param communicator : A communicator used to communicate with tieredAlgorithms
     */
    public AStarAlgorithm(MultiAlgorithmCommunicator communicator, Arborist arborist, LowerBound lowerBound, int depth) {
        super(communicator);
        _arborist = arborist;
        _lowerBound = lowerBound;
        _depth = depth;
    }

    /**
     * Constructor for A* running in isolation.
     * @param arborist
     * @param lowerBound
     */
    public AStarAlgorithm(Arborist arborist, LowerBound lowerBound){
        super();
        _arborist = arborist;
        _lowerBound = lowerBound;
        //In this case should iterate through all layers of partial schedules.
        _depth = Integer.MAX_VALUE;
    }

    @Override
    public void run(Graph graph, Schedule schedule, HashSet<Node> nextNodes) {

        // TODO: REPLACE TREEMAP DATA STRUCTURE
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
                    // if all parents of the node are in the schedule, this node can be added.
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

//                    if (prune(graph, newSchedule)){
//
//                        int lowerBoundEstimate = estimate(graph, newSchedule, nextNodesToVisit);
//                        schedulesToVisit.put(lowerBoundEstimate, newSchedule);
//                    }
                }

            }

        }
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
