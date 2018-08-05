package algorithm.heuristics;

import common.graph.Graph;
import common.schedule.Schedule;
import common.schedule.Task;

/**
 * Prunes based on removing partial schedules which dont adhere to a processor ordering
 */
public class ProcessorOrderPruner implements Arborist {
    @Override
    public boolean prune(Graph graph, Schedule schedule, Task toBeAdded) {
        if(schedule.size(toBeAdded.getProcessor()) > 0)
            return false;
        // Cull branch if there is any processor before the one on which the task being placed which has no tasks or
        // its first task is ordered after this task (by node id).
        for(int i = 0; i < toBeAdded.getProcessor(); ++i) {
            if(schedule.size(i) == 0) // Task which starts first should come next
                return true;
            // Order by startTime then (if there is no chance of one depending on the other) node id
            Task firstTask = schedule.getTasks(i).get(0);
            if(toBeAdded.getStartTime() < firstTask.getStartTime()
                || (toBeAdded.getStartTime() == firstTask.getStartTime()
                    && toBeAdded.getNode().getId() < firstTask.getNode().getId()
                    && firstTask.getNode().getComputationCost() > 0))
                return true;
        }
        return false;
    }
}
