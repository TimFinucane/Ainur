package algorithm;

import algorithm.heuristics.lowerbound.NaiveBound;
import algorithm.heuristics.pruner.IsNotAPruner;
import common.Validator;
import common.graph.Graph;
import integration.IntegrationTest;

import static org.junit.jupiter.api.Assertions.*;

public class UnoptimizedDFSTest extends IntegrationTest {
    UnoptimizedDFSTest() {
        super(2); // Only run 7 and 8 nodes
    }

    protected void runAgainstOptimal(String graphName, int processors, int optimalScheduleLength) {
        Graph graph = readGraph(graphName);

        // Execute algorithm w/out all heuristics
        Algorithm algorithm = new DFSAlgorithm(new IsNotAPruner(), new NaiveBound());
        algorithm.run(graph, processors);

        assertEquals(optimalScheduleLength, algorithm.getCurrentBest().getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, algorithm.getCurrentBest())); // Check result is valid
    }
}
