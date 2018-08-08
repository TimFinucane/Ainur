package integration.tests;

import algorithm.Algorithm;
import algorithm.DFSAlgorithm;
import algorithm.heuristics.lowerbound.CriticalPath;
import algorithm.heuristics.lowerbound.NaiveBound;
import algorithm.heuristics.pruner.IsNotAPruner;
import algorithm.heuristics.pruner.ProcessorOrderPruner;
import algorithm.heuristics.pruner.StartTimePruner;
import cli.Cli;
import common.Validator;
import common.categories.GandalfIntegrationTestsCategory;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.SimpleSchedule;
import common.schedule.Task;
import io.GraphReader;
import io.dot.DotGraphReader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

/**
 * Class contain test suite for integration tests for algorithm. Each test tests all parts of the algorithm
 * code manually ie. calling each component in sequence. Each test also tests the CLI with arguements and compares
 * the two results to make sure they match.
 * Tests on two processors also read the output file and make sure that it is what is expected and that the
 * schedule written out matches the same schedule that was obtained by manually running the algorithm.
 * Any tests higher than 9/10 nodes on more than 2 processors will take a significant amount of time to run, and
 * hence have not been included in the test suite.
 */
@Category(GandalfIntegrationTestsCategory.class)
public class AlgorithmIntegrationTests {




    /**
     * Tests for reading in data from a file and ensuring algorithm returns valid and optimal schedule with no
     * lower bound or pruning.
     */
    @Test
    public void testAlgorithm7NodeNoHeuristics() {

        // Set up File
        File graphFile = new File("data/graphs/Nodes_7_OutTree.dot");
        InputStream graphStream = null;

        try {
            graphStream = new FileInputStream(graphFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail("File not found");
        }

        //Try making graph from file and check that it is correct
        GraphReader reader = new DotGraphReader(graphStream);
        Graph graph = reader.read();

        // Execute algorithm w/ no heuristics
        Algorithm algorithm = new DFSAlgorithm(2, new IsNotAPruner(), new NaiveBound());
        algorithm.start(graph);

        //Manually start algorithm on graph
        Schedule resultManual = algorithm.getCurrentBest();

        assertEquals(28, resultManual.getEndTime()); // Check answer is optimal
        Assert.assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }




    /**
     * Tests for reading in data from a file and ensuring algorithm returns valid and optimal schedule with
     * lower bounds and pruning.
     */
    @Test
    public void testAlgorithm7NodeAllHeuristics() {

        // Set up File
        File graphFile = new File("data/graphs/Nodes_7_OutTree.dot");
        InputStream graphStream = null;

        try {
            graphStream = new FileInputStream(graphFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail("File not found");
        }

        //Try making graph from file and check that it is correct
        GraphReader reader = new DotGraphReader(graphStream);
        Graph graph = reader.read();

        // Execute algorithm w/ all heuristics
        Algorithm algorithm = new DFSAlgorithm(2,
                (pruningGraph, pruningSchedule, pruningTask) ->
                        new StartTimePruner().prune(pruningGraph, pruningSchedule, pruningTask) ||
                        new ProcessorOrderPruner().prune(pruningGraph, pruningSchedule, pruningTask),
                new CriticalPath());
        algorithm.start(graph);
        Schedule resultManual = algorithm.getCurrentBest();

        assertEquals(28, resultManual.getEndTime()); // Check answer is optimal
        Assert.assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }




    // Tests for cli interacting with reader
    // Tests for writing out

    /**
     * Test tests algorithm against graph with 8 nodes and 3 layers, one two processors with critical path
     * heuristics
     */
    @Test
    public void testAlgorithm8Node2ProcessorNoHeuristics() {

        // Set up File
        File graphFile = new File("data/graphs/Nodes_8_Random.dot");
        InputStream graphStream = null;

        try {
            graphStream = new FileInputStream(graphFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail("File not found");
        }

        GraphReader reader = new DotGraphReader(graphStream);
        Graph graph = reader.read();

        // Execute algorithm w/ no heuristics
        Algorithm algorithm = new DFSAlgorithm(2, new IsNotAPruner(), new CriticalPath());
        algorithm.start(graph);
        Schedule resultManual = algorithm.getCurrentBest();

        assertEquals(581, resultManual.getEndTime()); // Check answer is optimal
        Assert.assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }




    /**
     * Test tests algorithm against graph with 8 nodes and 3 layers, one two processors with critical path
     * heuristics
     */
    @Test
    public void testAlgorithm8Node2ProcessorAllHeuristics() {

        // Set up File
        File graphFile = new File("data/graphs/Nodes_8_Random.dot");
        InputStream graphStream = null;

        try {
            graphStream = new FileInputStream(graphFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail("File not found");
        }

        GraphReader reader = new DotGraphReader(graphStream);
        Graph graph = reader.read();

        // Execute algorithm w/ no heuristics
        Algorithm algorithm = new DFSAlgorithm(2,
                (pruningGraph, pruningSchedule, pruningTask) ->
                        new StartTimePruner().prune(pruningGraph, pruningSchedule, pruningTask) ||
                                new ProcessorOrderPruner().prune(pruningGraph, pruningSchedule, pruningTask),
                new CriticalPath());
        algorithm.start(graph);
        Schedule resultManual = algorithm.getCurrentBest();

        assertEquals(581, resultManual.getEndTime()); // Check answer is optimal
        Assert.assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }





    /**
     * Test tests algorithm against graph with 8 nodes and 3 layers, one two processors with all heuristics
     */
    @Test
    public void testAlgorithm9Node4ProcessorAllHeuristics() {

        // Set up File
        File graphFile = new File("data/graphs/Nodes_9_SeriesParallel.dot");
        InputStream graphStream = null;

        try {
            graphStream = new FileInputStream(graphFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail("File not found");
        }

        GraphReader reader = new DotGraphReader(graphStream);
        Graph graph = reader.read();

        // Execute algorithm w/ no heuristics
        Algorithm algorithm = new DFSAlgorithm(2,
                (pruningGraph, pruningSchedule, pruningTask) ->
                        new StartTimePruner().prune(pruningGraph, pruningSchedule, pruningTask) ||
                                new ProcessorOrderPruner().prune(pruningGraph, pruningSchedule, pruningTask),
                new CriticalPath());
        algorithm.start(graph);
        Schedule resultManual = algorithm.getCurrentBest();

        assertEquals(55, resultManual.getEndTime()); // Check answer is optimal
        Assert.assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }





    /**
     * Test tests algorithm against graph with 10 nodes, on 4 processors with critical path and pruner
     * heuristics
     * 3min 40s with no heuristics
     * 28s with 2X heuristics
     */
    @Test
    public void testNodes_10_RandomFourProcessor() {

        // Set up File
        File graphFile = new File("data/graphs/Nodes_10_Random.dot");
        InputStream graphStream = null;
        try {
            graphStream = new FileInputStream(graphFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail("File not found");
        }
        //Try making graph from file and check that it is correct
        GraphReader reader = new DotGraphReader(graphStream);
        Graph graph = reader.read();

        Algorithm algorithm = new DFSAlgorithm(
            4,
            (pruningGraph, pruningSchedule, pruningTask) ->
                new StartTimePruner().prune(pruningGraph, pruningSchedule, pruningTask)
                    || new ProcessorOrderPruner().prune(pruningGraph, pruningSchedule, pruningTask),
            new CriticalPath()
        );

        algorithm.start(graph);
        //Manually start algorithm on graph and check that final answer is correct
        Schedule resultManual = algorithm.getCurrentBest();

        assertEquals(50, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid

    }




    /**
     * Test tests algorithm against graph with 11 nodes on 4 processors with all heuristics
     */
    @Test
    public void testNode_11_OutTreeFourProcessor() {

        // Set up File
        File graphFile = new File("data/graphs/Nodes_11_OutTree.dot");
        InputStream graphStream = null;
        try {
            graphStream = new FileInputStream(graphFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail("File not found");
        }
        //Try making graph from file and check that it is correct
        GraphReader reader = new DotGraphReader(graphStream);
        Graph graph = reader.read();

        Algorithm algorithm = new DFSAlgorithm(
            4,
            (pruningGraph, pruningSchedule, pruningTask) ->
                new StartTimePruner().prune(pruningGraph, pruningSchedule, pruningTask)
                    || new ProcessorOrderPruner().prune(pruningGraph, pruningSchedule, pruningTask),
            new CriticalPath()
        );
        algorithm.start(graph);
        //Manually start algorithm on graph and check that final answer is correct
        Schedule resultManual = algorithm.getCurrentBest();

        assertEquals(227, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check result is valid

    }
}
