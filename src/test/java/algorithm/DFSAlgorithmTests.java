package algorithm;

import algorithm.heuristics.IsNotAPruner;
import algorithm.heuristics.NaiveBound;
import common.graph.Edge;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Processor;
import common.schedule.Schedule;
import common.schedule.Task;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class DFSAlgorithmTests {
    @Test
    public void simpleTest() {
        // Use two processors, simple graph
        List<Node> nodes = Arrays.asList( // Nodes
            new Node(3, "a"),
            new Node(4, "b"),
            new Node(2, "c"),
            new Node(1, "d"),
            new Node(3, "e")
        );
        List<Edge> edges = Arrays.asList(
            new Edge(nodes.get(0), nodes.get(2), 2),
            new Edge(nodes.get(1), nodes.get(3), 3),
            new Edge(nodes.get(2), nodes.get(4), 4),
            new Edge(nodes.get(1), nodes.get(4), 2)
        );

        Graph graph = new Graph(nodes, edges);

        Algorithm algorithm = new DFSAlgorithm(2, new IsNotAPruner(), new NaiveBound());

        algorithm.start(graph);
        Schedule result = algorithm.getCurrentBest();

        for(Processor processor : result.getProcessors()) {
            System.out.println("Processor");
            for(Task task : processor.getTasks())
                System.out.println(
                    task.getNode().getLabel()
                    + ", " + String.valueOf(task.getStartTime()) + " to " + String.valueOf(task.getEndTime())
                );
        }

        Assert.assertEquals(9, result.getTotalTime());
    }

    // Test on a more complex graph with 4 processors
    // Shouldnt make a graph any more complex on this as even this may strain jenkins atm.
    @Test
    public void lessSimpleTest() {
        // Use four processors, simple graph
        List<Node> nodes = Arrays.asList( // Nodes
            new Node(3, "a"),
            new Node(4, "b"),
            new Node(2, "c"),
            new Node(1, "d"),
            new Node(3, "e"),
            new Node(5, "f"),
            new Node(4, "g"),
            new Node(6, "h"),
            new Node(2, "i")
        );
        List<Edge> edges = Arrays.asList(
            new Edge(nodes.get(0), nodes.get(2), 2),
            new Edge(nodes.get(1), nodes.get(3), 3),
            new Edge(nodes.get(1), nodes.get(4), 4),
            new Edge(nodes.get(1), nodes.get(6), 4),
            new Edge(nodes.get(1), nodes.get(7), 4),
            new Edge(nodes.get(2), nodes.get(4), 2),
            new Edge(nodes.get(2), nodes.get(3), 3),
            new Edge(nodes.get(2), nodes.get(6), 3),
            new Edge(nodes.get(2), nodes.get(8), 1),
            new Edge(nodes.get(3), nodes.get(6), 5),
            new Edge(nodes.get(3), nodes.get(7), 2),
            new Edge(nodes.get(3), nodes.get(8), 6),
            new Edge(nodes.get(4), nodes.get(5), 4),
            new Edge(nodes.get(6), nodes.get(7), 3),
            new Edge(nodes.get(6), nodes.get(8), 1),
            new Edge(nodes.get(7), nodes.get(8), 5)
        );

        Graph graph = new Graph(nodes, edges);

        Algorithm algorithm = new DFSAlgorithm(20, new IsNotAPruner(), new NaiveBound());

        algorithm.start(graph);
        Schedule result = algorithm.getCurrentBest();

        for(Processor processor : result.getProcessors()) {
            System.out.println("Processor");
            for(Task task : processor.getTasks())
                System.out.println(
                    task.getNode().getLabel()
                        + ", " + String.valueOf(task.getStartTime()) + " to " + String.valueOf(task.getEndTime())
                );
        }

        Assert.assertEquals(20, result.getTotalTime());
    }
}
