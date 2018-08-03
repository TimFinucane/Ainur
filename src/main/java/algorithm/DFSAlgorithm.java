package algorithm;

import algorithm.heuristics.Arborist;
import algorithm.heuristics.LowerBound;
import common.graph.Edge;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Processor;
import common.schedule.Schedule;
import common.schedule.Task;
import javafx.util.Pair;

import java.util.*;

/**
 * A DFS implementation of the Algorithm class.
 */
public class DFSAlgorithm extends Algorithm {
    // TODO: Multithreaded. When done add as argument to the algorithm

    /**
     * Constructor for DFSAlgorithm class.
     * Defaults multithreading to false.
     * @param processors The number of processors
     */
    public DFSAlgorithm(int processors, Arborist arborist, LowerBound lowerBound) {
        super(processors, false, arborist, lowerBound);
    }

    /**
     * Starts running the DFS.
     * N.B. This implementation is NOT multithreaded and will block upon running.
     * Solution works by exploring avery possible schedule configuration and returning the best one it has found.
     * Also uses heuristics for faster runtime.
     *
     * Schedule is then stored and can be provided by getCurrentBest()
     * @see Algorithm#start(Graph)
     */
    @Override
    public void start(Graph graph) {
        _upperBound = initialUpperBound(graph);

        _bestSchedule = recurse(graph, new Schedule(_processors), new HashSet<>(graph.getEntryPoints()));
        _isComplete = true;
    }

    /**
     * A non-optimal solution to use as an initial upper bound.
     * Calculates the length of a solution with all nodes on the same processor
     */
    // TODO: When a proper non optimal solution has been implemented, use that instead
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
    private Schedule recurse(Graph graph, Schedule curSchedule, HashSet<Node> availableNodes) {
        // We might discover a better upper bound part way through and want to use it
        Schedule curBest = null;

        // Go through every node of our children, recursively
        for(Node node : availableNodes) {
            // Construct our new available nodes to pass on by copying available nodes and removing the one we're about
            // to add
            // TODO: Consider same memory storage optimisation as with schedule for this?
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
            for(Processor processor : curSchedule.getProcessors()) {
                // Calculate earliest it can be placed
                int earliest = 0;
                for(Edge edge : graph.getIncomingEdges(node)) {
                    Node dependencyNode = edge.getOriginNode();
                    Pair<Processor, Task> item = curSchedule.findTask(dependencyNode);

                    if(item == null)
                        throw new RuntimeException("Chide Tim for not checking a node's parents are in the schedule");

                    // If it's on the same processor, just has to be after task end. If not, then it also needs
                    // to be past the communication cost
                    if(item.getKey() == processor)
                        earliest = Math.max(earliest, item.getValue().getEndTime());
                    else
                        earliest = Math.max(earliest, item.getValue().getEndTime() + edge.getCost());
                }

                if( processor.getTasks().size() > 0 ) {
                    earliest = Math.max(
                        earliest,
                        processor.getLatestTask().getEndTime()
                    );
                }

                Task toBePlaced = new Task(earliest, node);

                // Check the base case (that adding the task will give us a complete schedule that we then return)
                if(curSchedule.size() + 1 == graph.size()) {
                    // Clone the schedule manually, or we will be modifying our parents schedule and that is bad
                    Schedule newSchedule = new Schedule(_processors);
                    for (int i = 0; i < _processors; ++i)
                    {
                        newSchedule.getProcessors().get(i).getTasks().addAll(curSchedule.getProcessors().get(i).getTasks());
                    }

                    // Add the task to newSchedule instead of curSchedule
                    newSchedule.getProcessors().get(curSchedule.getProcessors().indexOf(processor)).addTask(toBePlaced);
                    return newSchedule;
                }

                // Check whether placing it there is a good idea
                if( prune(graph, curSchedule, new Pair<>(processor, toBePlaced)) )
                    continue;

                // Check whether its worth trying w.r.t. lower bound estimate
                if( estimate(graph, curSchedule, new ArrayList<>(nextAvailableNodes)) >= _upperBound )
                    continue;

                // Ok all that has failed so i guess we have to actually recurse with it
                processor.addTask(toBePlaced); // Remember this adds it to the current schedule
                Schedule result = recurse(graph, curSchedule, nextAvailableNodes);
                // We added a task to the schedule and we need to remove it to return the curSchedule to its
                // original state
                processor.removeTask(toBePlaced);

                if(result == null) // You failed when I needed you most (the result wasn't good enough)
                    continue;

                // But at least now we know we have a result that should be better than upper bound
                int resultTotalTime = result.getTotalTime();

                // Just in case the schedule is still pretty bad
                if(resultTotalTime <= _upperBound) {
                    _upperBound = resultTotalTime;
                    curBest = result;
                }
            }
        }
        return curBest;
    }

    private int _upperBound; // When we have multithreading, this will be shared
}
