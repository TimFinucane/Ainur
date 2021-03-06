package algorithm;

import common.Validator;
import common.graph.Graph;
import common.schedule.Schedule;
import integration.GraphSet;
import integration.IntegrationTest;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.text.DecimalFormat;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

/**
 * Calss is a test suite that tests GreedyAlgorithm. Runs it with pre-made graphs and test graph files. Each test
 * verifys that the schedule is a valid schedule, not that it is optimal. Does not
 */
@Tag("gandalf") // Gandalf tests may be slow, but they finish precisely when they mean to
public class GreedyAlgorithmTests {
    @Test
    public void testBasic3NodeGraph() {
        Graph g = new Graph.Builder()
                .node("a", 2)
                .node("b", 3)
                .node("c", 5)
                .node("d", 1)
                .node("e", 3)
                .edge("a", "b", 1)
                .edge("a", "c", 1)
                .edge("c", "d", 1)
                .edge("b", "e", 1)
                .edge("d", "e", 2)
                .build();
        Algorithm algorithm = new GreedyAlgorithm();
        algorithm.run(g, 2);

        assertTrue(Validator.isValid(g, algorithm.getCurrentBest())); // Check answer is valid
    }

    protected void testOptimal(String graphName, int processors, int optimalScheduleLength) {
        Graph graph = IntegrationTest.readGraph(graphName);

        // Execute algorithm w/ no heuristics
        Algorithm greedyAlgorithm = new GreedyAlgorithm();
        greedyAlgorithm.run(graph, processors);

        //Manually start algorithm on graph
        Schedule resultGreedy = greedyAlgorithm.getCurrentBest();

        assertTrue(Validator.isValid(graph, resultGreedy)); // Check answer is valid
        double amountOver = resultGreedy.getEndTime() / (double)optimalScheduleLength;
        System.out.println("Result " + new DecimalFormat("#.00").format(amountOver) + "x over optimal");
    }

    @TestFactory
    List<DynamicTest> generate() {
        return new IntegrationTest(GraphSet.ALL_REASONABLE(), this::testOptimal).getList();
    }
}
