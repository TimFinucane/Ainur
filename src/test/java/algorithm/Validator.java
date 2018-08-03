package algorithm;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import common.graph.Edge;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Processor;
import common.schedule.Schedule;
import common.schedule.Task;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Validator {

    public static boolean isValid(Graph graph, Schedule schedule) {

        for (Processor processor : schedule.getProcessors()) {
            List<Task> tasks = processor.getTasks();

            for (Task task : tasks) {

                // Check all dependent nodes are after task finishes and after any necessary transmission time
                for (Edge edge : graph.getOutgoingEdges(task.getNode())) {

                    Node node = edge.getDestinationNode();

                    Pair<Processor, Task> dependentTask = schedule.findTask(node);

                    // Check if tasks are on the same processor
                    if (dependentTask.getKey() == processor) {
                        if (dependentTask.getValue().getStartTime() < task.getEndTime()) {
                            return false;
                        }
                    } else {
                        if (dependentTask.getValue().getStartTime() < task.getEndTime() + edge.getCost()) {
                            return false;
                        }
                    }

                }
            }

        }

    }

}
