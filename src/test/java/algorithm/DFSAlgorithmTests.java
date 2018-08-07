package algorithm;

import algorithm.heuristics.pruner.IsNotAPruner;
import algorithm.heuristics.lowerbound.NaiveBound;
import common.categories.HobbitonUnitTestsCategory;
import common.graph.Graph;
import common.schedule.Schedule;
import common.schedule.Task;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(HobbitonUnitTestsCategory.class)
public class DFSAlgorithmTests {
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

        Algorithm algorithm = new DFSAlgorithm(2, new IsNotAPruner(), new NaiveBound());

        algorithm.start(graph);
        Schedule result = algorithm.getCurrentBest();

        for(int i = 0; i < result.getNumProcessors(); ++i) {
            System.out.println("Processor " + String.valueOf(i));
            for(Task task : result.getTasks(i))
                System.out.println(
                    task.getNode().getLabel()
                    + ", " + String.valueOf(task.getStartTime()) + " to " + String.valueOf(task.getEndTime())
                );
        }

        Assert.assertEquals(9, result.getEndTime());
    }
}
