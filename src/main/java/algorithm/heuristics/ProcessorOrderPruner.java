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
        // Cull branch if there is any processor before the one on which the task being placed which has no tasks or
        // its first task is ordered after this task (by node id).
        for(int i = 0; i < toBeAdded.getProcessor(); ++i) {
            if(schedule.size(i) == 0
                || schedule.getTasks(i).get(0).getNode().getId() > toBeAdded.getNode().getId())
                return true;
        }
        return false;
    }
}
