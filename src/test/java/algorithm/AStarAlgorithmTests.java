package algorithm;

import algorithm.heuristics.lowerbound.NaiveBound;
import algorithm.heuristics.pruner.IsNotAPruner;
import common.graph.Graph;
import common.schedule.Schedule;
import common.schedule.Task;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Tag;

@Tag("gandalf") // Gandalf tests may be slow, but they finish precisely when they mean to
public class AStarAlgorithmTests {

    @Test
    public void simpleTest() {
        // Use two processors, simple graph
        Graph graph = new Graph.Builder()
                .node("a", 3)
                .node("b", 4)
                .node("c", 2)
                .node("d", 1)
                .node("e", 3)
                .edge("a", "c", 2)
                .edge("b", "d", 3)
                .edge("c", "e", 4)
                .edge("b", "e", 2)
                .build();


        Algorithm algorithm = new AStarAlgorithm(new IsNotAPruner(), new NaiveBound());

        algorithm.run(graph, 2);
        Schedule result = algorithm.getCurrentBest();

        for (int i = 0; i < result.getNumProcessors(); ++i) {
            System.out.println("Processor " + String.valueOf(i));
            for (Task task : result.getTasks(i))
                System.out.println(
                        task.getNode().getLabel()
                                + ", " + String.valueOf(task.getStartTime()) + " to " + String.valueOf(task.getEndTime())
                );
        }

        Assert.assertEquals(9, result.getEndTime());
    }
}