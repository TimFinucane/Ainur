package algorithm.heuristics.pruner;

import common.graph.Edge;
import common.graph.Graph;
import common.schedule.Schedule;
import common.schedule.Task;

public class GapPruner implements Arborist {
    @Override
    public boolean prune(Graph graph, Schedule schedule, Task toBeAdded) {
        // Calculate earliest it can be placed
        int earliest = 0;
        for(Edge edge : graph.getIncomingEdges(toBeAdded.getNode())) {
            Task item = schedule.findTask(edge.getOriginNode());
            // If it's on the same processor, just has to be after task end. If not, then it also needs
            // to be past the communication cost
            earliest = Math.max(
                earliest,
                (item.getProcessor() == toBeAdded.getProcessor()) ? item.getEndTime() :  item.getEndTime() + edge.getCost()
            );
        }

        int prevEndTime = 0;
        for(Task task : schedule.getTasks(toBeAdded.getProcessor())) {
            if(prevEndTime >= earliest && task.getStartTime() - prevEndTime >= toBeAdded.getNode().getComputationCost())
                return true;
            prevEndTime = task.getEndTime();
        }
        return false;
    }
}
