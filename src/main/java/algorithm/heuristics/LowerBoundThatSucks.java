package algorithm.heuristics;

import common.graph.Graph;
import common.graph.Node;
import common.schedule.Processor;
import common.schedule.Schedule;
import common.schedule.Task;

import java.util.List;

/**
 * This class can be used as a complete but terrible lower bound estimator.
 * This particular lower bound estimator simply returns the length of the partial schedule provided
 */
public class LowerBoundThatSucks implements LowerBound {

    /**
     * @see LowerBound#estimate(Graph, Schedule, List)
     */
    public int estimate(Graph graph, Schedule schedule, List<Node> nextNodes) {
        int max = 0;

        // Take maximum of all processors
        for(Processor processor : schedule.getProcessors()) {
            // Get end of last task in the processor, which we can assure is the last task in the processor.
            List<Task> tasks = processor.getTasks();
            Task lastTask = tasks.get(tasks.size() - 1);
            int endOfLastTask = lastTask.getStartTime() + lastTask.getNode().getComputationCost();

            max = Math.max(max, endOfLastTask);
        }

        return max;
    }

}
