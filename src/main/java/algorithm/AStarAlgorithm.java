package algorithm;

import algorithm.heuristics.lowerbound.LowerBound;
import algorithm.heuristics.pruner.Arborist;
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
    private Arborist _arborist;
    private LowerBound _lowerBound;

    // amount of memory allocated for the algorithm
    private static final double PERCENTAGE_MEMORY_TO_USE = 70;

    private int _numCulled = 0;
    private int _numExplored = 0;
    private Node _currentNode;

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
     * @param arborist : A pruner to use in algorithm
     * @param lowerBound : A lower-bound to use in algorithm
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
        SimpleSchedule simpleSchedule;

        if (schedule instanceof SimpleSchedule) {
            simpleSchedule = (SimpleSchedule)schedule;
        } else {
            simpleSchedule = new SimpleSchedule(schedule);
        }
        run(graph, simpleSchedule, nextNodes);
    }

    private void run(Graph graph, SimpleSchedule rootSchedule, HashSet<Node> nextNodes) {



        // TODO: REPLACE TREEMAP DATA STRUCTURE
        TreeMap<Integer, SimpleSchedule> schedulesToVisit = new TreeMap<>();

        //initial best estimate is just the first explored partial schedule.
        schedulesToVisit.put(_lowerBound.estimate(graph, rootSchedule, graph.getEntryPoints()), rootSchedule);

        while (!schedulesToVisit.isEmpty()) {
            Map.Entry<Integer, SimpleSchedule> integerScheduleEntry = schedulesToVisit.firstEntry();

            SimpleSchedule curSchedule = integerScheduleEntry.getValue();

            if (!continueRunning()) {
                // TODO figure out how to get the next nodes associated to current best schedule
                HashSet<Node> nextNodesToAdd = Helpers.calculateNextNodes(graph, curSchedule, );
                _communicator.explorePartialSolution(curSchedule, );
            }

            // if the schedule is complete, it is optimal.
            if (curSchedule.size() == graph.size()) {
                _communicator.update(curSchedule);
                return;
            }

            // generate all new possible schedules by adding nodes with all parents visited to all possible processors.
            for (Node node : nextNodes) {
                _currentNode = node;

                // find the earliest possible time the current node could be placed on each processor
                int[] earliestStarts = Helpers.calculateEarliestTimes(graph, curSchedule, node);

                // place the node on each possible processor to generate all possible schedules
                for (int proc = 0; proc < rootSchedule.getNumProcessors(); proc++){

                    // generate a new task that is placed on the earliest possible time for current processor
                    Task taskToPlace = new Task(proc, earliestStarts[proc], node);

                    // if pruner suggests culling this branch, do not explore this schedule
                    if (_arborist.prune(graph, curSchedule, taskToPlace)) {
                        _numCulled++;
                        continue;

                    } else { // explore this schedule

                        // generates a schedule with new task added.
                        SimpleSchedule newSchedule = curSchedule;
                        newSchedule.addTask(taskToPlace);

                        // find all the nodes that can now be visited after adding current node to schedule
                        HashSet<Node> nextNodesToAdd = Helpers.calculateNextNodes(graph, curSchedule, nextNodes, node);

                        // find the lower bound associated to the newly generated schedule.
                        int newLowerBound = _lowerBound.estimate(graph, newSchedule, new ArrayList<>(nextNodesToAdd));

                        // check to see if heuristics suggest exploring path based on new schedule lower bound estimate
                        if (newLowerBound >= _communicator.getCurrentBest().getEndTime()) {
                            _numCulled++;
                            continue;
                        } else { // explore new schedule by adding it to the search space
                            _numExplored++;
                            schedulesToVisit.put(newLowerBound, newSchedule);
                        }

                    }

                    // current schedule has now been explored so does not need to be revisited
                    schedulesToVisit.remove(curSchedule);
                }

            }

        }
    }

    /**
     * Helper method to determine if the algorithm is using over its threshold value of memory and if so if it should
     * continue to run.
     * @return Whether or not the algorithm has enough memory to keep running.
     */
    private boolean continueRunning(){
        Runtime runtime = Runtime.getRuntime();

        // determines the amount of memory that has been used out of the maximum amount that could be allocated to it
        long memoryUsed = runtime.maxMemory() - runtime.freeMemory();
        // calculates the percentage of memory that has been used
        long percentUsed = (memoryUsed/ runtime.maxMemory())*100;

        //if algorithm has used more than a set percentage it should pass its implementation to another thread.
        if (percentUsed > PERCENTAGE_MEMORY_TO_USE) {
            return false;
        } else {
            return true;
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
}
