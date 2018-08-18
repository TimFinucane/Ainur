package integration;

import algorithm.AStarAlgorithm;
import algorithm.Algorithm;
import algorithm.heuristics.lowerbound.CriticalPath;
import algorithm.heuristics.lowerbound.FillTimeBound;
import algorithm.heuristics.lowerbound.LowerBound;
import algorithm.heuristics.pruner.Arborist;
import algorithm.heuristics.pruner.ProcessorOrderPruner;
import algorithm.heuristics.pruner.StartTimePruner;
import common.Validator;
import common.graph.Graph;
import common.schedule.Schedule;
import io.GraphReader;
import io.dot.DotGraphReader;
import org.junit.Assert;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Runs thorough test suite for A* on giant data set.
 */
public class AStarIntegrationTests extends IntegrationTest {


    public AStarIntegrationTests() {
        super();
    }

    @Override
    protected void runAgainstOptimal(String graph, int processors, int optimalScheduleLength) {

        // Single threaded DFS implementation
        Algorithm dfsAlgorithm = new AStarAlgorithm(
                Arborist.combine(new StartTimePruner(), new ProcessorOrderPruner()),
                LowerBound.combine(new CriticalPath())
        );


        GraphReader reader = null;
        try {
            reader = new DotGraphReader(new FileInputStream(graph));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Graph inputGraph = reader.read();

        dfsAlgorithm.run(inputGraph, processors);
        Schedule schedule = dfsAlgorithm.getCurrentBest();

        Assert.assertEquals(optimalScheduleLength, schedule.getEndTime());
        Assert.assertTrue(Validator.isValid(inputGraph, schedule));
    }
}
