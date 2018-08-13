package algorithm;

import algorithm.heuristics.lowerbound.LowerBound;
import algorithm.heuristics.pruner.Arborist;
import common.graph.Graph;
import common.schedule.Schedule;
import common.schedule.SimpleSchedule;

import java.util.*;

/**
 * Algorithm implementation that will utilise the A* technique to generate an optimal schedule.
 */
public class AStarAlgorithm extends Algorithm {

    /**
     * Constructor for AStarAlgorithm class.
     * Defaults multithreading to false.
     * @param processors The number of processors
     */
    public AStarAlgorithm(int processors, Arborist arborist, LowerBound lowerBound) {
        super(processors, false, arborist, lowerBound);
    }

    @Override
    public void start(Graph graph) {

        SimpleSchedule emptySchedule = new SimpleSchedule(+_processors);

        List<SimpleSchedule> schedulesToVisit = new ArrayList<>();
        schedulesToVisit.add(emptySchedule);

        HashMap<SimpleSchedule, Integer> scheduleWeights = new HashMap<>();

        int initialLowerBound = estimate(graph, emptySchedule, graph.getEntryPoints());

        scheduleWeights.put(emptySchedule, initialLowerBound);

        while (!schedulesToVisit.isEmpty()) {

            Integer min = null;
            SimpleSchedule curSchedule = null;

            //get the schedule with the smallest weight estimate in the map.
            for (SimpleSchedule schedule : schedulesToVisit) {
                int curMin = scheduleWeights.get(schedule);
                if (min == null || curMin < min) {
                    min = curMin;
                    curSchedule = schedule;
                }
            }

            // if the schedule is complete, it is optimal.
            if (curSchedule.size() == graph.size()) {
                _bestSchedule = curSchedule;
                return;
            }




        }
         //f(n) is lower bound estimate rather than g(n) + h(n)
    }


}
