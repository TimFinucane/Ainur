package algorithm;

import algorithm.heuristics.Arborist;
import algorithm.heuristics.LowerBound;
import common.graph.Edge;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Processor;
import common.schedule.Schedule;
import common.schedule.Task;

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
        int upperBound = initialUpperBound(graph);

        Schedule schedule = recurse(graph, new Schedule(_processors), new HashSet<>(graph.getEntryPoints()), upperBound);

        _bestSchedule = schedule;
        _isComplete = true;
    }

    /**
     * A non-optimal solution to use as an initial upper bound.
     * Calculates the length of a solution with all nodes on the same processor
     */
    // TODO: When a proper non optimal solution has been implemented, use that instead
    private int initialUpperBound(Graph graph) {
        Set<Node> nextNodes = new HashSet<>(graph.getEntryPoints());

        int total = 0;

        // Visit every node as if we were traversing the graph normally (maintain a set of nodes to explore,
        // add to the set when new nodes are discovered, when the set is empty we have visited every node).
        while(!nextNodes.isEmpty()) {
            Node node = nextNodes.iterator().next();

            total += node.getComputationCost();
            nextNodes.remove(node);

            for(Edge edge : graph.getOutgoingEdges(node))
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
     * @param upperBound The maximum computation amt the schedule can be
     */
    private Schedule recurse(Graph graph, Schedule curSchedule, HashSet<Node> availableNodes, int upperBound) {
        // We might discover a better upper bound part way through and want to use it
        int curUpperBound = upperBound;
        Schedule curBest = null;

        // Go through every node of our children, recursively
        for(Node node : availableNodes) {
            // Construct our new available nodes to pass on
            HashSet<Node> nextAvailableNodes = new HashSet<>();
            nextAvailableNodes.addAll(availableNodes);
            nextAvailableNodes.remove(node);
            for(Edge edge : graph.getOutgoingEdges(node))
                nextAvailableNodes.add(edge.getDestinationNode());

            // Now we run all possible ways of adding this node to the schedule.
            // We apply this to the schedule then remove it before using it again,
            // to prevent constant cloning of the schedule
            for(Processor processor : curSchedule.getProcessors()) {
                // Calculate earliest it can be placed
                int earliest = 0;
                for(Edge edge : graph.getIncomingEdges(node)) {
                    Node dependencyNode = edge.getOriginNode();

                    // TODO: Add find task method to processor AND schedule and use it here
                    // PLEASE DO NOT PUT THIS CODE HERE JAVA WAS NOT MEANT FOR THIS
                    for(Processor searchProcessor : curSchedule.getProcessors()) {
                        Optional<Task> potentialTask = searchProcessor.getTasks().stream()
                            .filter((task) -> task.getNode() == dependencyNode)
                            .findAny();
                        if (potentialTask.isPresent() && searchProcessor != processor)
                            earliest = Math.max(
                                earliest,
                                potentialTask.get().getStartTime() + potentialTask.get().getNode().getComputationCost()
                            );
                    }
                }

                if( processor.getTasks().size() > 0 ) {
                    earliest = Math.max(
                        earliest,
                        // TODO: RESOLVE THIS PLEASE AAAA
                        processor.getTasks().get(processor.getTasks().size() - 1).getStartTime() +
                            processor.getTasks().get(processor.getTasks().size() - 1).getNode().getComputationCost()
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
                if( prune(graph, curSchedule, toBePlaced) )
                    continue;

                // Check whether its worth trying w.r.t. lower bound estimate
                if( estimate(graph, curSchedule, new ArrayList<>(nextAvailableNodes)) >= curUpperBound )
                    continue;

                // Ok all that has failed so i guess we have to actually recurse with it
                processor.addTask(toBePlaced); // Remember this adds it to the current schedule
                Schedule result = recurse(graph, curSchedule, nextAvailableNodes, curUpperBound);

                if(result == null) // You failed when I needed you most (the result wasn't good enough)
                    continue;

                // But at least now we know we have a result that should be better than upper bound
                // TODO: Schedule.getTotalCost()
                int resultTotalTime = 0;
                for(Processor resultProcessor : result.getProcessors()) {
                    Task endTask = resultProcessor.getTasks().get(resultProcessor.getTasks().size() - 1);
                    resultTotalTime = Math.max(
                        resultTotalTime,
                        endTask.getStartTime() + endTask.getNode().getComputationCost()
                    );
                }

                // Just in case the schedule is still pretty bad
                if(resultTotalTime < curUpperBound) {
                    curUpperBound = resultTotalTime;
                    curBest = result;
                }

                // We added a task to the schedule and we need to remove it to return the curSchedule to its
                // original state
                processor.removeTask(toBePlaced);
            }
        }
        return curBest;
    }
}
