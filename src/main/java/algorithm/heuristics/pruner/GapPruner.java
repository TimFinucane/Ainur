package algorithm.heuristics.pruner;

import common.graph.Graph;
import common.schedule.Schedule;
import common.schedule.Task;

public class GapPruner implements Arborist {
    @Override
    public boolean prune(Graph graph, Schedule schedule, Task toBeAdded) {
        int prevEndTime = 0;

        for(Task task : schedule.getTasks(toBeAdded.getProcessor())) {
            if(task.getStartTime() - prevEndTime >= toBeAdded.getNode().getComputationCost())
                return true;
            prevEndTime = task.getEndTime();
        }
        return false;
    }
}
