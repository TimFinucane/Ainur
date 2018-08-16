package algorithm;

import algorithm.heuristics.lowerbound.CriticalPath;
import algorithm.heuristics.pruner.Arborist;
import algorithm.heuristics.pruner.ProcessorOrderPruner;
import algorithm.heuristics.pruner.StartTimePruner;
import common.Validator;
import common.graph.Graph;
import common.schedule.Schedule;
import integration.IntegrationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OptimizedDFSTest extends IntegrationTest {

    protected void runAgainstOptimal(String graphName, int processors, int optimalScheduleLength) {
        Graph graph = readGraph(graphName);

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
}
