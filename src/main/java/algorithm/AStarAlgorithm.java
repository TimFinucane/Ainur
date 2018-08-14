package algorithm;

import algorithm.heuristics.lowerbound.LowerBound;
import algorithm.heuristics.pruner.Arborist;
import common.graph.Edge;
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
    protected Arborist _arborist;
    protected LowerBound _lowerBound;

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
     * @param arborist
     * @param lowerBound
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

                int[] earliestStarts = Helpers.calculateEarliestTimes(graph, schedule, node);

                for (int proc = 0; proc < schedule.getNumProcessors(); proc++){

                    // generate a new task that is placed on the earliest possible time on the given processor
                    Task taskToPlace = new Task(proc,earliestStarts[proc], node);
                    // find all the nodes that are now visitable after adding current node to schedule
                    HashSet<Node> nextNodesToAdd = Helpers.calculateNextNodes(graph, schedule, nextNodes, node);

                    // check to see if heuristics suggest exploring path
                    if (_arborist.prune(graph, schedule, taskToPlace)
                            || _lowerBound.estimate(graph, schedule, new ArrayList<>(nextNodesToAdd)) >= _communicator.getCurrentBest().getEndTime()){
                        _numCulled++;
                        continue;
                    } else { // partial schedule should be explored and thus, added to the search space
                        SimpleSchedule newSchedule = schedule;
                        schedule.addTask(taskToPlace);
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
