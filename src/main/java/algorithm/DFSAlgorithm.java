package algorithm;

import algorithm.heuristics.pruner.Arborist;
import algorithm.heuristics.lowerbound.LowerBound;
import common.graph.*;
import common.schedule.*;
import java.util.*;
import static algorithm.Helpers.*;

/**
 * A DFS implementation of the Algorithm class.
 */
public class DFSAlgorithm extends BoundableAlgorithm {
    private int _depth;
    private Arborist _arborist;
    private LowerBound _lowerBound;

    private int _numCulled = 0;
    private int _numExplored = 0;
    private Node _currentNode;

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
            _currentNode = node;
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
                if( _arborist.prune(graph, curSchedule, toBePlaced) ) {
                    _numCulled++;
                    continue;
                }

                curSchedule.addTask(toBePlaced);
                // Check whether to continue (delve deeper)
                if( _lowerBound.estimate(graph, curSchedule, new ArrayList<>(nextAvailableNodes)) >= _communicator.getCurrentBest().getEndTime()) {
                    _numCulled++;
                }
                // We are meant to continue with this schedule
                else {
                    _numExplored++;

                    // Either pass the schedule to our communicator
                    if (curSchedule.size() + 1 >= _depth)
                        _communicator.explorePartialSolution(new SimpleSchedule(curSchedule), nextAvailableNodes);
                    else
                        recurse(graph, curSchedule, nextAvailableNodes);
                }
                curSchedule.removeTask(toBePlaced);
            }
        }
    }

    /**
     * @see Algorithm#branchesCulled()
     */
    @Override
    public int branchesCulled() {
        return _numCulled;
    }

    /**
     * @see Algorithm#branchesExplored()
     */
    @Override
    public int branchesExplored() {
        return _numExplored;
    }

    /**
     * @see Algorithm#currentNode() 
     */
    @Override
    public Node currentNode() {
        return _currentNode;
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
}
