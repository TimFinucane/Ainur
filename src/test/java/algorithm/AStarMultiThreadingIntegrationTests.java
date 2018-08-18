package algorithm;

import algorithm.heuristics.lowerbound.CriticalPath;
import algorithm.heuristics.lowerbound.FillTimeBound;
import algorithm.heuristics.lowerbound.LowerBound;
import algorithm.heuristics.pruner.*;
import common.Validator;
import common.graph.Graph;
import common.schedule.Schedule;
import integration.GraphSet;
import integration.IntegrationTest;
import javafx.util.Pair;
import org.junit.Assert;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Runs thorough test suite for A* on giant data set. Runs multithreading with 4 threads.
 *
 * Also runs through all data provided to us on Canvas with varying numbers of threads allocated.
 */
@Tag("gandalf") // Gandalf tests may be slow, but they finish precisely when they mean to
class AStarMultiThreadingIntegrationTests {

    /**
     * Runs A Star with all optimal
     */
    private void testOptimal(String graphName, int processors, int optimalScheduleLength) {
        Graph graph = IntegrationTest.readGraph(graphName);

        // 4 threads A star
        Algorithm aStarAlgorithm = generateTieredAlgorithm(4, graph, processors);

        aStarAlgorithm.run(graph, processors);
        Schedule schedule = aStarAlgorithm.getCurrentBest();

        Assert.assertEquals(optimalScheduleLength, schedule.getEndTime());
        Assert.assertTrue(Validator.isValid(graph, schedule));
    }

    private void test40Threads(String graphName, int processors, int optimalScheduleLength) {
        Graph graph = IntegrationTest.readGraph(graphName);

        // Create and execute
        Algorithm algorithm = generateTieredAlgorithm(40, graph, processors);
        algorithm.run(graph, processors);

        //Manually start algorithm on graph
        Schedule resultManual = algorithm.getCurrentBest();

        assertEquals(optimalScheduleLength, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }

    private void test2Threads(String graphName, int processors, int optimalScheduleLength) {
        Graph graph = IntegrationTest.readGraph(graphName);

        // Create and execute
        Algorithm algorithm = generateTieredAlgorithm(2, graph, processors);
        algorithm.run(graph, processors);

        //Manually start algorithm on graph
        Schedule resultManual = algorithm.getCurrentBest();

        assertEquals(optimalScheduleLength, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }

    private void test1Thread(String graphName, int processors, int optimalScheduleLength) {
        Graph graph = IntegrationTest.readGraph(graphName);

        // Create and execute
        Algorithm algorithm = generateTieredAlgorithm(1, graph, processors);
        algorithm.run(graph, processors);

        //Manually start algorithm on graph
        Schedule resultManual = algorithm.getCurrentBest();

        assertEquals(optimalScheduleLength, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }

    @TestFactory
    List<DynamicContainer> generateTests() {
        return IntegrationTest.join(
            new Pair<>("40 threads", new IntegrationTest(GraphSet.OLIVER(), this::test40Threads)),
            new Pair<>("2 threads", new IntegrationTest(GraphSet.OLIVER(), this::test2Threads)),
            new Pair<>("1 thread", new IntegrationTest(GraphSet.OLIVER(), this::test1Thread)),
            new Pair<>("comprehensive", new IntegrationTest(GraphSet.ALL_REASONABLE(), this::testOptimal))
        );
    }

    /**
     * Generates a tiered AStar algorithm with the specified number of threads.
     * @param threads : number of threads for tiered algorithm to run on.
     */
    private TieredAlgorithm generateTieredAlgorithm(int threads, Graph graph, int processors) {
        GreedyAlgorithm greedyAlgorithm = new GreedyAlgorithm();
        greedyAlgorithm.run(graph, processors);

        Arborist arborist = Arborist.combine(new StartTimePruner(), new ProcessorOrderPruner(), new BetterStartPruner(), new BetterSwapPruner());
        LowerBound lowerBound = LowerBound.combine(new CriticalPath(), new FillTimeBound());

        return new TieredAlgorithm(threads,
            (tier, communicator) -> {
                if(tier == 0) // Expand to a few states for the purposes of running A stars in parallel
                    return new DFSAlgorithm(communicator, arborist, lowerBound, 4);
                else if(tier < (graph.size() / 2 + 1)) // Run A stars in parallel on the system
                    return new AStarAlgorithm(communicator, arborist, lowerBound);
                else
                    return new DFSAlgorithm(communicator, arborist, lowerBound, Integer.MAX_VALUE);
            },
            greedyAlgorithm.getCurrentBest()
        );

    }
}
