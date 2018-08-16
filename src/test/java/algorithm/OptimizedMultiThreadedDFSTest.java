package algorithm;

import algorithm.heuristics.lowerbound.CriticalPath;
import algorithm.heuristics.pruner.Arborist;
import algorithm.heuristics.pruner.ProcessorOrderPruner;
import algorithm.heuristics.pruner.StartTimePruner;
import common.Validator;
import common.graph.Graph;
import common.schedule.Schedule;
import integration.IntegrationTest;

import static org.junit.jupiter.api.Assertions.*;

public class OptimizedMultiThreadedDFSTest extends IntegrationTest {

    @Override
    protected void runAgainstOptimal(String graphName, int processors, int optimalScheduleLength) {
        Graph graph = readGraph(graphName);

        // Execute algorithm w/ all heuristics
        Algorithm algorithm = new TieredAlgorithm(4,
            (tier, communicator) -> new DFSAlgorithm(
                communicator,
                Arborist.combine(new StartTimePruner(), new ProcessorOrderPruner()),
                new CriticalPath(),
                tier == 0 ? 8 : Integer.MAX_VALUE
            )
        );

        algorithm.run(graph, processors);
        Schedule resultManual = algorithm.getCurrentBest();

        assertEquals(optimalScheduleLength, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check result is valid
    }
}
