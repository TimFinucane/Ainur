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

        List<Task> tasks = new ArrayList<>();

        for (Processor processor : schedule.getProcessors()) {
            for (Task task : processor.getTasks()) {
                tasks.add(task);
            }
        }

        for (Task task : tasks) {
            // The outgoing edges of the node in question
            List<Edge> edges = graph.getOutgoingEdges(task.getNode());

            // Check all dependent nodes are after task finishes and after any necessary transmission time
            for (Edge edge : edges) {
                Node node = edge.getDestinationNode();

                Pair<Processor, Task> dependentTask = schedule.findTask(node);
                
            }
        }

    }

}
