package algorithm;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import common.graph.Edge;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Processor;
import common.schedule.Schedule;
import common.schedule.Task;

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

            // Get all the nodes that are dependant on given task on a certain processor.
            List<Node> destinationNodes = new VirtualFlow.ArrayLinkedList<>();
            for (Edge edge : edges) {
                destinationNodes.add(edge.getDestinationNode());
            }

        }

    }

}
