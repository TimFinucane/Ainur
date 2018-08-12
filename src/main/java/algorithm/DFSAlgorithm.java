package algorithm;

import algorithm.heuristics.Arborist;
import algorithm.heuristics.LowerBound;
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

    /**
     * Constructor for DFSAlgorithm class.
     * @param arborist : A pruner to use in algorithm
     * @param lowerBound : A lower-bound to use in algorithm
     * @param notifier : An object used to communicate with tieredAlgorithms
     * @param globalBest : Reference to the current global best schedule
     */
    public DFSAlgorithm(Arborist arborist,
                        LowerBound lowerBound,
                        MultiAlgorithmNotifier notifier,
                        AtomicReference<Schedule> globalBest) {
        super(arborist, lowerBound, notifier, globalBest);
    }

    /**
     * Starts running the DFS.
     * Solution works by exploring avery possible schedule configuration and returning the best one it has found.
     * Also uses heuristics for faster runtime.
     *
     * Schedule is then stored and can be provided by getCurrentBest()
     * @see Algorithm#run(Graph)
     * @param graph : Graph object for DFS to be run on
     * @param schedule : A schedule that tasks can be added to
     * @param depth : The max depth to which each threaded algorithm will search to
     * @param nextNodes : A helpful list of nodes to search through next
     */
    @Override
    public void start(Graph graph, Schedule schedule, int depth, HashSet<Node> nextNodes) {
        _depth = depth;
        recurse(graph, new SimpleSchedule(_processors, graph.size()), new HashSet<>(graph.getEntryPoints()));
        _isComplete = true;
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
                    if(curSchedule.getEndTime() <= _globalBest.get().getEndTime()) {
                        _notifier.onSolutionFound(curSchedule);
                    }
                    continue;
                }

                // Check whether our heuristics advise continuing down this noble eightfold path
                if( prune(graph, curSchedule, toBePlaced)
                    || estimate(graph, curSchedule, new ArrayList<>(nextAvailableNodes)) >= _globalBest.get().getEndTime() )
                    continue;

                // Check if we have reached the max depth for searching - if so, the notify our notifier
                if(curSchedule.size() + 1 >= _depth){
                    SimpleSchedule newSchedule = new SimpleSchedule(curSchedule);
                    newSchedule.addTask(toBePlaced);
                    _notifier.explorePartialSolution(newSchedule, nextAvailableNodes);
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
}
