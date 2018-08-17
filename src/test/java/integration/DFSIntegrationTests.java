package integration;

import algorithm.Algorithm;
import algorithm.DFSAlgorithm;
import algorithm.heuristics.lowerbound.CriticalPath;
import algorithm.heuristics.pruner.Arborist;
import algorithm.heuristics.pruner.ProcessorOrderPruner;
import algorithm.heuristics.pruner.StartTimePruner;
import common.Validator;
import common.graph.Graph;
import common.schedule.Schedule;
import io.GraphReader;
import io.dot.DotGraphReader;
import javafx.util.Pair;
import org.junit.Assert;
import org.junit.platform.commons.JUnitException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DFSIntegrationTests extends IntegrationTest {


    public DFSIntegrationTests() {
        super();
    }

    @Override
    protected void runAgainstOptimal(String graph, int processors, int optimalScheduleLength) {
        try {

            // Single threaded DFS implementation
            Algorithm dfsAlgorithm = new DFSAlgorithm(
                    Arborist.combine(new StartTimePruner(), new ProcessorOrderPruner()),
                    new CriticalPath()
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
