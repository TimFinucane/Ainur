package algorithm;

import algorithm.Algorithm;
import algorithm.DFSAlgorithm;
import algorithm.heuristics.lowerbound.CriticalPath;
import algorithm.heuristics.lowerbound.NaiveBound;
import algorithm.heuristics.pruner.Arborist;
import algorithm.heuristics.pruner.IsNotAPruner;
import algorithm.heuristics.pruner.ProcessorOrderPruner;
import algorithm.heuristics.pruner.StartTimePruner;
import common.Validator;
import common.graph.Graph;
import common.schedule.Schedule;
import integration.GraphSet;
import integration.IntegrationTest;
import javafx.util.Pair;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Runs thorough test suite for DFS on giant data set.
 */
@Tag("gandalf") // Gandalf tests may be slow, but they finish precisely when they mean to
public class DFSIntegrationTests {
    void testOptimal(String graphName, int processors, int optimalScheduleLength) {
        Graph graph = IntegrationTest.readGraph(graphName);

        // Execute algorithm w/ all heuristics
        Algorithm algorithmWithAllHeuristics = new DFSAlgorithm(
            Arborist.combine(new StartTimePruner(), new ProcessorOrderPruner()),
            new CriticalPath()
        );

        algorithmWithAllHeuristics.run(graph, processors);
        Schedule resultManual = algorithmWithAllHeuristics.getCurrentBest();

        assertEquals(optimalScheduleLength, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check result is valid
    }

    void testUnoptimal(String graphName, int processors, int optimalScheduleLength) {
        Graph graph = IntegrationTest.readGraph(graphName);

        // Execute algorithm w/ all heuristics
        Algorithm algorithmWithAllHeuristics = new DFSAlgorithm(
            new IsNotAPruner(),
            new NaiveBound()
        );

        algorithmWithAllHeuristics.run(graph, processors);
        Schedule resultManual = algorithmWithAllHeuristics.getCurrentBest();

        assertEquals(optimalScheduleLength, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check result is valid
    }

    @TestFactory
    List<DynamicContainer> generateTests() {
        return IntegrationTest.join(
            new Pair<>("optimal", new IntegrationTest(GraphSet.ALL_REASONABLE(), this::testOptimal)),
            new Pair<>("unoptimal", new IntegrationTest(GraphSet.OLIVER(), this::testOptimal))
        );
    }
}
