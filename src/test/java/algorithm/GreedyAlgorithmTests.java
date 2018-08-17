package algorithm;

import common.Validator;
import common.graph.Graph;
import common.schedule.Schedule;
import integration.IntegrationTest;
import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertTrue;

/**
 * Calss is a test suite that tests GreedyAlgorithm. Runs it with pre-made graphs and test graph files. Each test
 * verifys that the schedule is a valid schedule, not that it is optimal. Does not
 */
public class GreedyAlgorithmTests extends IntegrationTest {
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

    @Override
    protected void runAgainstOptimal(String graphName, int processors, int optimalScheduleLength) {
        Graph graph = readGraph(graphName);

        // Execute algorithm w/ no heuristics
        Algorithm greedyAlgorithm = new GreedyAlgorithm();
        greedyAlgorithm.run(graph, processors);

        //Manually start algorithm on graph
        Schedule resultGreedy = greedyAlgorithm.getCurrentBest();

        assertTrue(Validator.isValid(graph, resultGreedy)); // Check answer is valid
        double amountOver = resultGreedy.getEndTime() / (double)optimalScheduleLength;
        System.out.println("Result " + amountOver + "x over optimal");
    }
}
