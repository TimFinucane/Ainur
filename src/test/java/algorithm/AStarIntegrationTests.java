package algorithm;

import algorithm.AStarAlgorithm;
import algorithm.Algorithm;
import algorithm.heuristics.lowerbound.CriticalPath;
import algorithm.heuristics.lowerbound.LowerBound;
import algorithm.heuristics.pruner.Arborist;
import algorithm.heuristics.pruner.ProcessorOrderPruner;
import algorithm.heuristics.pruner.StartTimePruner;
import common.Validator;
import common.graph.Graph;
import common.schedule.Schedule;
import integration.GraphSet;
import integration.IntegrationTest;
import org.junit.Assert;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;

import java.util.List;

/**
 * Runs thorough test suite for A* on giant data set.
 *
 * Also runs through all data provided to us on Canvas.
 */
@Tag("gandalf") // Gandalf tests may be slow, but they finish precisely when they mean to
class AStarIntegrationTests {
    /**
     * The following test runs on the ALL the data sets including supplied by O Sinnen. Optimal A Star.
     */
    private void testOptimalAStar(String graphName, int processors, int optimalScheduleLength) {
        // Single threaded DFS implementation
        Algorithm aStarAlgorithm = new AStarAlgorithm(
                Arborist.combine(new StartTimePruner(), new ProcessorOrderPruner()),
                LowerBound.combine(new CriticalPath())
        );

        Graph graph = IntegrationTest.readGraph(graphName);

        aStarAlgorithm.run(graph, processors);
        Schedule schedule = aStarAlgorithm.getCurrentBest();

        Assert.assertEquals(optimalScheduleLength, schedule.getEndTime());
        Assert.assertTrue(Validator.isValid(graph, schedule));
    }

    @TestFactory
    List<DynamicTest> generateTests() {
        return new IntegrationTest(GraphSet.ALL_REASONABLE(), this::testOptimalAStar).getList();
    }
}
