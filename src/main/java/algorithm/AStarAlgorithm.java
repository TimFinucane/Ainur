package algorithm;

import algorithm.heuristics.lowerbound.LowerBound;
import algorithm.heuristics.pruner.Arborist;
import common.graph.Edge;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.SimpleSchedule;
import common.schedule.Task;
import sun.java2d.pipe.SpanShapeRenderer;

import java.util.*;

/**
 * Algorithm implementation that will utilise the A* technique to generate an optimal schedule.
 */
public class AStarAlgorithm extends BoundableAlgorithm {

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

    private void run(Graph graph, SimpleSchedule schedule, HashSet<Node> nextNodes) {

        // TODO: REPLACE TREEMAP DATA STRUCTURE
        TreeMap<Integer, SimpleSchedule> schedulesToVisit = new TreeMap<>();

        //initial best estimate is just the first explored partial schedule.
        schedulesToVisit.put(_lowerBound.estimate(graph, schedule, graph.getEntryPoints()), schedule);

        while (!schedulesToVisit.isEmpty()) {
            Map.Entry<Integer, SimpleSchedule> integerScheduleEntry = schedulesToVisit.firstEntry();

            SimpleSchedule curSchedule = integerScheduleEntry.getValue();

            // if the schedule is complete, it is optimal.
            if (curSchedule.size() == graph.size()) {
                _communicator.update(curSchedule);
                return;
            }

            // generate all new possible schedules by adding nodes with all parents visited to all possible processors.
            for (Node node : nextNodes) {

                // find the earliest possible time the current node could be placed on each processor
                int[] earliestStarts = Helpers.calculateEarliestTimes(graph, schedule, node);

                // place the node on each possible processor to generate all possible schedules
                for (int proc = 0; proc < schedule.getNumProcessors(); proc++){

                    // generate a new task that is placed on the earliest possible time for current processor
                    Task taskToPlace = new Task(proc, earliestStarts[proc], node);

                    // if pruner suggests culling this branch, do not explore this schedule
                    if (_arborist.prune(graph, schedule, taskToPlace)) {
                        _numCulled++;
                        continue;

                    } else { // explore this schedule

                        // generates a schedule with new task added.
                        SimpleSchedule newSchedule = schedule;
                        schedule.addTask(taskToPlace);

                        // find all the nodes that can now be visited after adding current node to schedule
                        HashSet<Node> nextNodesToAdd = Helpers.calculateNextNodes(graph, schedule, nextNodes, node);

                        // find the lower bound associated to the newly generated schedule.
                        int newLowerBound = _lowerBound.estimate(graph, newSchedule, new ArrayList<>(nextNodesToAdd));

                        // check to see if heuristics suggest exploring path based on new schedule lower bound estimate
                        if (newLowerBound >= _communicator.getCurrentBest().getEndTime()) {
                            _numCulled++;
                            continue;
                        } else { // explore new schedule by adding it to the search space
                            schedulesToVisit.put(newLowerBound, newSchedule);
                        }

                    }

                    // current schedule has now been explored so does not need to be revisited
                    schedulesToVisit.remove(schedule);
                }

            }

        }
    }


    @Override
    public int branchesCulled() {
        return 0;
    }

    @Override
    public int branchesExplored() {
        return 0;
    }

    @Override
    public Node currentNode() {
        return null;
    }
}
