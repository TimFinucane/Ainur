package common;

import algorithm.Algorithm;
import algorithm.DFSAlgorithm;
import algorithm.heuristics.IsNotAPruner;
import algorithm.heuristics.NaiveBound;
import cli.Cli;
import common.graph.Edge;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Processor;
import common.schedule.Schedule;
import common.schedule.Task;
import io.GraphReader;
import io.dot.DotGraphReader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class AlgorithmIntegrationTests {

    private final File DATA_DIRECTORY = new File("data/graphs/");

    @Before
    public void setUpFolderName(){

    }

    @Test
    public void testNodes_7_OutTreeGraph() {

        // Set up File
        File graphFile = new File(DATA_DIRECTORY + "Nodes_7_OutTree.dot");
        InputStream graphStream = null;
        try {
            graphStream = new FileInputStream(graphFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail();
        }
        GraphReader reader = new DotGraphReader(graphStream);
        Graph graph = reader.read();

        //Assert that graph is as expected
        Node entryNode = graph.getEntryPoints().get(0);

        assertEquals(entryNode.getLabel(), "0");
        assertEquals(5, entryNode.getComputationCost());
        assertEquals(7, graph.size());


        Algorithm algorithm = new DFSAlgorithm(2, new IsNotAPruner(), new NaiveBound());

        algorithm.start(graph);
        Schedule resultManual = algorithm.getCurrentBest();

        assertEquals(28, resultManual.getTotalTime());
        // Now run graph through CLI
        String[] args = {"\"data/graphs/Nodes_7_OutTree.dot\"", "2"};
        Cli cli = new Cli(args) {
            @Override
            protected Schedule startScheduling(Graph graph) {
                Algorithm algorithm = new DFSAlgorithm(2, new IsNotAPruner(), new NaiveBound());

                algorithm.start(graph);
                Schedule result = algorithm.getCurrentBest();
                return result;
            }
        };

        // Check that output file is all good
        File outputFile = new File("somewhere file is");
        assertEquals(graphFile.getName() + "_processed", outputFile.getName());
        InputStream outputGraphStream = null;
        try {
            outputGraphStream = new FileInputStream(outputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail();
        }

        // Somehow make sure schedule given by dot file matches schedule given by manually making one with methods
    }



}
