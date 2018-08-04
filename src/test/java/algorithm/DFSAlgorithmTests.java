package algorithm;

import algorithm.heuristics.IsNotAPruner;
import algorithm.heuristics.NaiveBound;
import common.categories.HobbitonTests;
import common.graph.Graph;
import common.schedule.Processor;
import common.schedule.Schedule;
import common.schedule.Task;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(HobbitonTests.class)
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
}
