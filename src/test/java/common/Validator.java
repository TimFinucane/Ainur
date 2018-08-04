package common;

import common.categories.HobbitonUnitTestsCategory;
import common.graph.Edge;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Processor;
import common.schedule.Schedule;
import common.schedule.Task;
import javafx.util.Pair;
import org.junit.experimental.categories.Category;

import java.util.List;

@Category(HobbitonUnitTestsCategory.class)
public class Validator {

    public static boolean isValid(Graph graph, Schedule schedule) {

        return validOrder(graph, schedule) && validPlacement(schedule);

    }

    // Check for dependent nodes of tasks and ensure their tasks finish after their parent task is
    // finished plus any necessary transmission time.
    private static boolean validOrder(Graph graph, Schedule schedule) {

        for (Processor processor : schedule.getProcessors()) {
            List<Task> tasks = processor.getTasks();

            for (Task task : tasks) {

                // Loop through all outgoing edges of the graph
                for (Edge edge : graph.getOutgoingEdges(task.getNode())) {

                    Node node = edge.getDestinationNode();
                    Pair<Processor, Task> dependentTask = schedule.findTask(node);

                    // Check if tasks are on the same processor
                    if (dependentTask.getKey() == processor) {
                        // Check if dependentTask's start time comes before the task's end time
                        if (dependentTask.getValue().getStartTime() < task.getEndTime()) {
                            return false; // Invalid
                        }
                    } else {
                        /// Check if dependentTask's stert time comes before the task's end time plus transmission cost
                        if (dependentTask.getValue().getStartTime() < task.getEndTime() + edge.getCost()) {
                            return false; // Invalid
                        }
                    }
                }
            }
        }

        return true;
    }

    // Check that the processors do not feature tasks that overlap.
    private static boolean validPlacement(Schedule schedule) {

        //Check that Tasks do not overlap on a processor
        for (Processor processor : schedule.getProcessors()) {
            List<Task> tasks = processor.getTasks();

            for (Task task : tasks) {
                for (Task otherTask : tasks) {

                    // If either the start time of the other task lies in between the start and end time of the set task
                    // or the end time of the other task lies in between the start and finish of the set task.
                    if ((otherTask.getStartTime() < task.getEndTime() && otherTask.getStartTime() > task.getStartTime()) ||
                            (otherTask.getEndTime() < task.getEndTime() && otherTask.getEndTime() > task.getStartTime())) {

                        return false;
                    }

                }
            }
        }

        return true;
    }

}
