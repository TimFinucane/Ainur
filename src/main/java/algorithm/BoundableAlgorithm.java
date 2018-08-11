package algorithm;

import algorithm.heuristics.Arborist;
import algorithm.heuristics.LowerBound;
import common.graph.Edge;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.SimpleSchedule;
import common.schedule.Task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public abstract class BoundableAlgorithm extends Algorithm{

    private final MultiAlgorithmNotifier _notifier;
    protected AtomicReference<Schedule> _globalBest;
    private int _upperBound;
    protected int _depth;


    public BoundableAlgorithm(int processors, Arborist arborist, LowerBound lowerBound, MultiAlgorithmNotifier notifier) {
        super(processors, false, arborist, lowerBound);
        this._notifier = notifier;
    }

    /**
     * Starts running the DFS.
     * Solution works by exploring avery possible schedule configuration and returning the best one it has found.
     * Also uses heuristics for faster runtime. Can be multithreaded by use of depth varible. This specifies how
     * deep each tiered algorithm will search.
     *
     * Schedule is then stored and can be provided by getCurrentBest()
     * @see Algorithm#start(Graph)
     * @param graph : Graph that algorithm is to be run on
     * @param schedule :current schedule to add to
     * @param depth : max depth that each threaded algorithm will search to
     * @param nextNodes : a helpful list of nodes that the algorithm can search to next
     */
    public void start(Graph graph, SimpleSchedule schedule, int depth, HashSet<Node> nextNodes){
        _upperBound = initialUpperBound(graph);
        _depth = depth;
        _bestSchedule = recurse(graph, schedule, nextNodes);
        _isComplete = true;
    }

    @Override
    public Schedule getCurrentBest(){
        return _globalBest.get();
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
     *  @param graph The full graph
     * @param curSchedule The partial schedule with all nodes visited by 'parent' recursors in it
     * @param availableNodes A helpful list of nodes available to visit next
     */
    private SimpleSchedule recurse(Graph graph, SimpleSchedule curSchedule, HashSet<Node> availableNodes) {
        // We might discover a better upper bound part way through and want to use it
        SimpleSchedule curBest = null;

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

                    return newSchedule;
                }

                // Check whether our heuristics advise continuing down this noble eightfold path
                if( prune(graph, curSchedule, toBePlaced)
                        || estimate(graph, curSchedule, new ArrayList<>(nextAvailableNodes)) >= _upperBound )
                    continue;

                // Ok all that has failed so i guess we have to actually recurse with it
                curSchedule.addTask(toBePlaced);
                SimpleSchedule result = recurse(graph, curSchedule, nextAvailableNodes);
                curSchedule.removeTask(toBePlaced);


                if(result == null) // You failed when I needed you most (the result wasn't good enough)
                    continue;

                // But at least now we know we have a result that should be better than upper bound
                int resultTotalTime = result.getEndTime();

                // Just in case the schedule is still pretty bad
                if(resultTotalTime <= _upperBound) {
                    _upperBound = resultTotalTime;
                    curBest = result;
                }
            }
        }
        return curBest;
    }

}
