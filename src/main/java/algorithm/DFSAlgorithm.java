package algorithm;

import algorithm.heuristics.pruner.Arborist;
import algorithm.heuristics.lowerbound.LowerBound;
import common.graph.Edge;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.SimpleSchedule;
import common.schedule.Task;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A DFS implementation of the Algorithm class.
 */
public class DFSAlgorithm extends BoundableAlgorithm {
    private int _depth;
    protected Arborist _arborist;
    protected LowerBound _lowerBound;

    /**
     * Constructor for DFSAlgorithm class.
     * @param arborist : A pruner to use in algorithm
     * @param lowerBound : A lower-bound to use in algorithm
     * @param notifier : An object used to communicate with tieredAlgorithms
     * @param globalBest : Reference to the current global best schedule
     */
    public DFSAlgorithm(Arborist arborist,
                        LowerBound lowerBound,
                        MultiAlgorithmCommunicator notifier,
                        AtomicReference<Schedule> globalBest) {
        super(notifier, globalBest);
        _arborist = arborist;
        _lowerBound = lowerBound;
    }

    /**
     * @see BoundableAlgorithm#BoundableAlgorithm()
     */
    public DFSAlgorithm(Arborist arborist, LowerBound lowerBound) {
        super();
        _arborist = arborist;
        _lowerBound = lowerBound;
    }

    /**
     * Starts running the DFS.
     * Solution works by exploring avery possible schedule configuration and returning the best one it has found.
     * Also uses heuristics for faster runtime.
     *
     * Schedule is then stored and can be provided by getCurrentBest()
     * @see Algorithm#run(Graph, int)
     * @param graph : Graph object for DFS to be run on
     * @param schedule : A schedule that tasks can be added to
     * @param depth : The max depth to which each threaded algorithm will search to
     * @param nextNodes : A helpful list of nodes to search through next
     */
    @Override
    public void run(Graph graph, Schedule schedule, int depth, HashSet<Node> nextNodes) {
        _depth = depth;
        recurse(graph,
            schedule instanceof SimpleSchedule ? (SimpleSchedule)schedule : new SimpleSchedule(schedule),
            nextNodes);
    }

    /**
     * The recursive part of the algorithm
     *
     * @param graph The full graph
     * @param curSchedule The partial schedule with all nodes visited by 'parent' recursors in it
     * @param availableNodes A helpful list of nodes available to visit next
     */
    private void recurse(Graph graph, SimpleSchedule curSchedule, HashSet<Node> availableNodes) {

        // Go through every node of our children, recursively
        for(Node node : availableNodes) {
            // Construct our new available nodes to pass on by copying available nodes and removing the one we're about
            // to add
            HashSet<Node> nextAvailableNodes = new HashSet<>(availableNodes);
            nextAvailableNodes.remove(node);

            // Now add all the children of the node we are visiting
            // Check that everything we add has all it's parents in the schedule.
            // There might be better code to do this or via method?
            for(Edge edge : graph.getOutgoingEdges(node)) {
                Node nodeToAdd = edge.getDestinationNode();

                boolean parentsInSchedule = true;
                for(Edge parentEdge : graph.getIncomingEdges(nodeToAdd)) {
                    if (parentEdge.getOriginNode() != node && curSchedule.findTask(parentEdge.getOriginNode()) == null) {
                        parentsInSchedule = false;
                        break;
                    }
                }
                if(parentsInSchedule)
                    nextAvailableNodes.add(nodeToAdd);
            }

            // Now we run all possible ways of adding this node to the schedule.
            // We apply this to the schedule then remove it before using it again,
            // to prevent constant cloning of the schedule
            for(int processor = 0; processor < curSchedule.getNumProcessors(); ++processor) {
                // Calculate earliest it can be placed
                int earliest = 0;
                for(Edge edge : graph.getIncomingEdges(node)) {
                    Node dependencyNode = edge.getOriginNode();
                    Task item = curSchedule.findTask(dependencyNode);

                    if(item == null)
                        throw new RuntimeException("Chide Tim for not checking a node's parents are in the schedule");

                    // If it's on the same processor, just has to be after task end. If not, then it also needs
                    // to be past the communication cost
                    if(item.getProcessor() == processor)
                        earliest = Math.max(earliest, item.getEndTime());
                    else
                        earliest = Math.max(earliest, item.getEndTime() + edge.getCost());
                }

                if( curSchedule.size(processor) > 0 ) {
                    earliest = Math.max(
                        earliest,
                        curSchedule.getLatest(processor).getEndTime()
                    );
                }

                Task toBePlaced = new Task(processor, earliest, node);

                // Check the base case (that adding the task will give us a complete schedule that we then return)
                if(curSchedule.size() + 1 == graph.size()) {
                    SimpleSchedule newSchedule = new SimpleSchedule(curSchedule);
                    newSchedule.addTask(toBePlaced);
                    if(newSchedule.getEndTime() < _communicator.getCurrentBest().getEndTime()) {
                        _communicator.update(newSchedule);
                    }
                    continue;
                }

                // Check whether our heuristics advise continuing down this noble eightfold path
                if( prune(graph, curSchedule, toBePlaced)
                    || estimate(graph, curSchedule, new ArrayList<>(nextAvailableNodes)) >= _communicator.getCurrentBest().getEndTime() )
                    continue;

                // Check if we have reached the max depth for searching - if so, the notify our notifier
                if(curSchedule.size() + 1 >= _depth){
                    // Copy the schedule and nodes so that they aren't modified when passed on.
                    SimpleSchedule newSchedule = new SimpleSchedule(curSchedule);
                    newSchedule.addTask(toBePlaced);
                    _communicator.explorePartialSolution(newSchedule, new HashSet<>(nextAvailableNodes));
                }
                // Else continue searching through the graph for another schedule solution
                else {
                    // Ok all that has failed so i guess we have to actually recurse with it
                    curSchedule.addTask(toBePlaced);
                    recurse(graph, curSchedule, nextAvailableNodes);
                    curSchedule.removeTask(toBePlaced);
                }

            }
        }
    }

    public boolean prune(Graph graph, Schedule schedule, Task processorTaskPair) {
        return _arborist.prune(graph, schedule, processorTaskPair);
    }

    public int estimate(Graph graph, Schedule schedule, List<Node> nodesToVisit) {
        return _lowerBound.estimate(graph, schedule, nodesToVisit);
    }

    public int estimate(Graph graph, Schedule schedule) {
        return _lowerBound.estimate(graph, schedule);
    }
}
