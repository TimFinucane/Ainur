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
    // When we have multithreading, these will be shared
    private SimpleSchedule _bestSchedule;
    private int _upperBound;

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
     * @see Algorithm#start(Graph)
     * @param graph : Graph object for DFS to be run on
     * @param schedule : A schedule that tasks can be added to
     * @param depth : The max depth to which each threaded algorithm will search to
     * @param nextNodes : A helpful list of nodes to search through next
     */
    @Override
    public void start(Graph graph, Schedule schedule, int depth, HashSet<Node> nextNodes) {
        _upperBound = initialUpperBound(graph);
        recurse(graph, new SimpleSchedule(_processors, graph.size()), new HashSet<>(graph.getEntryPoints()));
        _isComplete = true;
    }

    /**
     * A non-optimal solution to use as an initial upper bound.
     * Calculates the length of a solution with all nodes on the same processor
     */
    private int initialUpperBound(Graph graph) {
        Set<Node> visited = new HashSet<>();
        Set<Node> nextNodes = new HashSet<>(graph.getEntryPoints());

        int total = 0;

        // Visit every node as if we were traversing the graph normally (maintain a set of nodes to explore,
        // add to the set when new nodes are discovered, when the set is empty we have visited every node).
        while(!nextNodes.isEmpty()) {
            Node node = nextNodes.iterator().next();

            total += node.getComputationCost();
            nextNodes.remove(node);
            visited.add(node);

            for(Edge edge : graph.getOutgoingEdges(node))
                if(!visited.contains(edge.getDestinationNode()))
                    nextNodes.add(edge.getDestinationNode());
        }

        return total;
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
                    int resultTotalTime = curSchedule.getEndTime();
                    if(resultTotalTime <= _upperBound) {
                        _upperBound = resultTotalTime;
                        _bestSchedule = curSchedule;
                    }
                    return;
                }

                // Check whether our heuristics advise continuing down this noble eightfold path
                if( prune(graph, curSchedule, toBePlaced)
                    || estimate(graph, curSchedule, new ArrayList<>(nextAvailableNodes)) >= _upperBound )
                    continue;

                // Ok all that has failed so i guess we have to actually recurse with it
                curSchedule.addTask(toBePlaced);
                curSchedule.removeTask(toBePlaced);
                recurse(graph, curSchedule, nextAvailableNodes);

            }
        }
    }

    public SimpleSchedule getCurrentSchedule(){
        return _bestSchedule;
    }
}
