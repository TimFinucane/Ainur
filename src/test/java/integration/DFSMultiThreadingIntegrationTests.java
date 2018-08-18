package integration;

import algorithm.Algorithm;
import algorithm.DFSAlgorithm;
import algorithm.TieredAlgorithm;
import algorithm.heuristics.lowerbound.CriticalPath;
import algorithm.heuristics.pruner.Arborist;
import algorithm.heuristics.pruner.ProcessorOrderPruner;
import algorithm.heuristics.pruner.StartTimePruner;
import common.Validator;
import common.graph.Graph;
import common.schedule.Schedule;
import io.GraphReader;
import io.dot.DotGraphReader;
import org.junit.Assert;
import org.junit.platform.commons.JUnitException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Runs thorough test suite for DFS multi threading on giant data set using 4 threads.
 */
public class DFSMultiThreadingIntegrationTests extends IntegrationTest {

    public DFSMultiThreadingIntegrationTests(){
        super();
    }

    @Override
    protected void runAgainstOptimal(String graph, int processors, int optimalScheduleLength) {
        try {

            // Single threaded DFS implementation
            // Execute algorithm w/ all heuristics
            Algorithm dfsAlgorithm = new TieredAlgorithm(4,
                    (tier, communicator) -> new DFSAlgorithm(
                            communicator,
                            Arborist.combine(new StartTimePruner(), new ProcessorOrderPruner()),
                            new CriticalPath(),
                            tier == 0 ? 8 : Integer.MAX_VALUE
                    )
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

        } catch (JUnitException ju) {
            ju.printStackTrace();
        }
    }

}
