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
     * @param communicator : A communicator used to communicate with tieredAlgorithms
     */
    public DFSAlgorithm(MultiAlgorithmCommunicator communicator, Arborist arborist, LowerBound lowerBound, int depth) {
        super(communicator);
        _arborist = arborist;
        _lowerBound = lowerBound;
        _depth = depth;
    }

    /**
     * Constructor for DFSAlgorithm running solo
     */
    public DFSAlgorithm(Arborist arborist, LowerBound lowerBound) {
        super();
        _arborist = arborist;
        _lowerBound = lowerBound;
        _depth = Integer.MAX_VALUE;
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
     * @param nextNodes : A helpful list of nodes to search through next
     */
    @Override
    public void run(Graph graph, Schedule schedule, HashSet<Node> nextNodes) {
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
        // If only one node left, take it and place it in optimal place. Base case.
        if(curSchedule.size() + 1 == graph.size()) {
            placeLastNode(graph, curSchedule, availableNodes.iterator().next());
            return;
        }

        // Go through every node of our children, recursively
        for(Node node : availableNodes) {
            // Calculate what nodes can be added next iteration
            HashSet<Node> nextAvailableNodes = calculateNextNodes(graph, curSchedule, availableNodes, node);
            // Get where to place the node for each processor
            int[] earliestStarts = calculateEarliestTimes(graph, curSchedule, node);

            // Now we run all possible ways of adding this node to the schedule.
            // We apply this to the schedule then remove it before using it again,
            // to prevent constant cloning of the schedule
            for(int processor = 0; processor < curSchedule.getNumProcessors(); ++processor) {
                Task toBePlaced = new Task(processor, earliestStarts[processor], node);

                // Check whether our heuristics advise continuing down this noble eightfold path
                if( _arborist.prune(graph, curSchedule, toBePlaced)
                    || _lowerBound.estimate(graph, curSchedule, new ArrayList<>(nextAvailableNodes)) >= _communicator.getCurrentBest().getEndTime() )
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
                    // Ok all that has failed so i guess we have to actually recurse with it.
                    // Push the task, run recurse, pop the task. Saves on copying.
                    curSchedule.addTask(toBePlaced);
                    recurse(graph, curSchedule, nextAvailableNodes);
                    curSchedule.removeTask(toBePlaced);
                }

            }
        }
    }

    /**
     * Places the last node in a schedule in the most optimal place, and attempts to update current best if it is better
     */
    private void placeLastNode(Graph graph, SimpleSchedule schedule, Node last) {
        // Choose where to add it:
        int[] earliestStarts = calculateEarliestTimes(graph, schedule, last);

        // Find best of them
        int minIndex = 0;
        for(int processor = 1; processor < earliestStarts.length; ++processor)
            if(earliestStarts[processor] < earliestStarts[minIndex])
                minIndex = processor;

        // Check if better than current, if so then update.
        int endTime = Math.min(schedule.getEndTime(), earliestStarts[minIndex] + last.getComputationCost());
        if(endTime < _communicator.getCurrentBest().getEndTime()) {
            SimpleSchedule newSchedule = new SimpleSchedule(schedule);
            newSchedule.addTask(new Task(minIndex, earliestStarts[minIndex], last));
            _communicator.update(newSchedule);
        }
    }

    /**
     * Calculates the set of available nodes to add to the schedule, derived from adding the current node
     * to the schedule and using the old set of available nodes.
     * TODO: Optimize?
     */
    private HashSet<Node> calculateNextNodes(Graph graph, SimpleSchedule schedule, HashSet<Node> oldNodes, Node added) {
        // Construct our new available nodes to pass on by copying available nodes and removing the one we're about
        // to add
        HashSet<Node> nextAvailableNodes = new HashSet<>(oldNodes);
        nextAvailableNodes.remove(added);

        // Now add all the children of the node we are visiting
        // Check that everything we add has all it's parents in the schedule.
        // There might be better code to do this or via method?
        for(Edge edge : graph.getOutgoingEdges(added)) {
            Node nodeToAdd = edge.getDestinationNode();

            boolean parentsInSchedule = true;
            for(Edge parentEdge : graph.getIncomingEdges(nodeToAdd)) {
                if (parentEdge.getOriginNode() != added && !schedule.contains(parentEdge.getOriginNode())) {
                    parentsInSchedule = false;
                    break;
                }
            }
            if(parentsInSchedule)
                nextAvailableNodes.add(nodeToAdd);
        }

        return nextAvailableNodes;
    }

    /**
     * Calculates the earliest times it can add the given node to each processor in the set
     * TODO: Optimize?
     */
    private int[] calculateEarliestTimes(Graph graph, SimpleSchedule schedule, Node node) {
        // Calculate earliest it can be placed
        int[] earliests = new int[schedule.getNumProcessors()];
        for(Edge edge : graph.getIncomingEdges(node)) {
            Node dependencyNode = edge.getOriginNode();
            Task item = schedule.findTask(dependencyNode);

            if(item == null)
                throw new RuntimeException("Chide Tim for not checking a node's parents are in the schedule");

            // If it's on the same processor, just has to be after task end. If not, then it also needs
            // to be past the communication cost
            for(int processor = 0; processor < schedule.getNumProcessors(); ++processor)
                earliests[processor] = Math.max(earliests[processor],
                    (item.getProcessor() == processor) ? item.getEndTime() :  item.getEndTime() + edge.getCost());
        }
        for(int processor = 0; processor < schedule.getNumProcessors(); ++processor)
            if( schedule.size(processor) > 0 )
                earliests[processor] = Math.max(earliests[processor], schedule.getLatest(processor).getEndTime());

        return earliests;
    }
}
