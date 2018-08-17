package algorithm;

import algorithm.heuristics.pruner.Arborist;
import algorithm.heuristics.lowerbound.LowerBound;
import common.graph.*;
import common.schedule.*;
import javafx.util.Pair;

import java.math.BigInteger;
import java.util.*;

/**
 * A DFS implementation of the Algorithm class.
 */
public class DFSAlgorithm extends BoundableAlgorithm {
    private int _depth;
    private Arborist _arborist;
    private LowerBound _lowerBound;

    volatile private BigInteger _numCulled = BigInteger.ZERO;
    volatile private BigInteger _numExplored = BigInteger.ZERO;
    volatile private Node _currentNode;
    volatile private int _curLowerBound;

    private Graph _graph;

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
        _graph = graph;
        recurse(schedule instanceof SimpleSchedule ? (SimpleSchedule)schedule : new SimpleSchedule(schedule),
            nextNodes, Integer.MAX_VALUE);
        _curLowerBound = getCurrentBest().getEndTime();

        _currentNode = null;
    }

    /**
     * The recursive part of the algorithm
     *
     * @param curSchedule The partial schedule with all nodes visited by 'parent' recursors in it
     * @param availableNodes A helpful list of nodes available to visit next
     * @param parentLowerBound The minimum lower bound of the parent recursor. Used to calculate current lower bound.
     */
    @SuppressWarnings("NonAtomicOperationOnVolatileField") // All the volatile non-atomics here are only ever modified in this class, in this thread.
    private void recurse(SimpleSchedule curSchedule, HashSet<Node> availableNodes, int parentLowerBound) {
        // If only one node left, take it and place it in optimal place. Base case.
        if(curSchedule.size() + 1 == _graph.size()) {
            placeLastNode(_graph, curSchedule, availableNodes.iterator().next());
            return;
        }

        // List of tasks ordered by lower bound
        PriorityQueue<Pair<Integer, Task>> orderedTasks = new PriorityQueue<>(Comparator.comparing(Pair::getKey));
        HashMap<Node, HashSet<Node>> nextAvailableNodes = new HashMap<>();

        // Go through every node of our children and add it to the priority queue
        for(Node node : availableNodes) {
            _currentNode = node;
            // Calculate what nodes can be added next iteration

            HashSet<Node> nodesNextAvailableNodes = AlgorithmUtils.calculateNextNodes(_graph, curSchedule, availableNodes, node);
            nextAvailableNodes.put(node, nodesNextAvailableNodes);

            // Get where to place the node for each processor
            int[] earliestStarts = AlgorithmUtils.calculateEarliestTimes(_graph, curSchedule, node);

            // Now we run all possible ways of adding this node to the schedule.c
            // We apply this to the schedule then remove it before using it again,
            // to prevent constant cloning of the schedule
            for (int processor = 0; processor < curSchedule.getNumProcessors(); ++processor) {
                Task toBePlaced = new Task(processor, earliestStarts[processor], node);

                // Check whether our heuristics advise continuing down this noble eightfold path
                if (_arborist.prune(_graph, curSchedule, toBePlaced)) {
                    _numCulled = _numCulled.add(BigInteger.ONE); // Same as _numCulled++;
                    continue;
                }
                curSchedule.addTask(toBePlaced);
                int nodesLowerBound = _lowerBound.estimate(_graph, curSchedule, nextAvailableNodes.get(node));
                curSchedule.removeTask(toBePlaced);

                // Now add it to our sorted node info
                orderedTasks.add(new Pair<>(nodesLowerBound, toBePlaced));
            }
        }

        // Now we go through each node in order and recurse on it
        while(!orderedTasks.isEmpty()) {
            Pair<Integer, Task> taskPair = orderedTasks.poll();
            Task toBeAdded = taskPair.getValue();
            // Check if lower bound is good enough
            if(taskPair.getKey() >= _communicator.getCurrentBest().getEndTime()) {
                //Same as _numCulled += orderedTasks.size() + 1;
                _numCulled = _numCulled.add(new BigInteger(Integer.toString(orderedTasks.size()))).add(BigInteger.ONE);
                break; // We can break because every subsequent task has a greater lower bound
            }

            // We are meant to continue with this schedule
            _numExplored = _numExplored.add(BigInteger.ONE); // Same as _numExplored++
            curSchedule.addTask(toBeAdded);
            HashSet<Node> nodesNextAvailableNodes = nextAvailableNodes.get(toBeAdded.getNode());

            // Update our current lower bound to the minimum of the two minimum possible values (parent min, and our ordered task min)
            _curLowerBound = orderedTasks.isEmpty() ? parentLowerBound : Math.min(parentLowerBound, orderedTasks.peek().getKey());

            // Either pass the schedule to our communicator
            if (curSchedule.size() + 1 >= _depth)
                _communicator.explorePartialSolution(_graph, new SimpleSchedule(curSchedule), nodesNextAvailableNodes);
            else
                recurse(curSchedule, nodesNextAvailableNodes, _curLowerBound);
            curSchedule.removeTask(toBeAdded);
        }
    }

    /**
     * @see Algorithm#branchesCulled()
     */
    @Override
    public BigInteger branchesCulled() {
        return _numCulled;
    }

    /**
     * @see Algorithm#branchesExplored()
     */
    @Override
    public BigInteger branchesExplored() {
        return _numExplored;
    }

    /**
     * @see Algorithm#currentNode() 
     */
    @Override
    public Node currentNode() {
        return _currentNode;
    }

    @Override
    public int lowerBound() {
        return _curLowerBound;
    }

    /**
     * Places the last node in a schedule in the most optimal place, and attempts to update current best if it is better
     */
    private void placeLastNode(Graph graph, SimpleSchedule schedule, Node last) {
        // Choose where to add it:
        int[] earliestStarts = AlgorithmUtils.calculateEarliestTimes(graph, schedule, last);

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
