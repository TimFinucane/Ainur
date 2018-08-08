package integration.tests;

import algorithm.Algorithm;
import algorithm.DFSAlgorithm;
import algorithm.heuristics.lowerbound.CriticalPath;
import algorithm.heuristics.lowerbound.NaiveBound;
import algorithm.heuristics.pruner.IsNotAPruner;
import algorithm.heuristics.pruner.ProcessorOrderPruner;
import algorithm.heuristics.pruner.StartTimePruner;
import common.Validator;
import common.categories.GandalfIntegrationTestsCategory;
import common.graph.Graph;
import common.schedule.Schedule;
import io.GraphReader;
import io.dot.DotGraphReader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static junit.framework.TestCase.*;

/**
 * This class's purpose is to provide a suite for testing DFSAlgorithm's functionality with regards to reading in a graph
 * object and outputting a schedule that is both optimal and valid. Graphs are sourced from data/graphs/, reading in of
 * of these also occurs before every test.
 */
@Category(GandalfIntegrationTestsCategory.class)
public class DFSAlgorithmIT {


    private Algorithm _algorithmWith2Processors;
    private Algorithm _algorithmWith4Processors;


    @Before
    public void setup() {

        _algorithmWith2Processors = new DFSAlgorithm(2,
                (pruningGraph, pruningSchedule, pruningTask) ->
                        new StartTimePruner().prune(pruningGraph, pruningSchedule, pruningTask) ||
                                new ProcessorOrderPruner().prune(pruningGraph, pruningSchedule, pruningTask),
                new CriticalPath());

        _algorithmWith4Processors = new DFSAlgorithm(4,
                (pruningGraph, pruningSchedule, pruningTask) ->
                        new StartTimePruner().prune(pruningGraph, pruningSchedule, pruningTask) ||
                                new ProcessorOrderPruner().prune(pruningGraph, pruningSchedule, pruningTask),
                new CriticalPath());

    }



    /**
     * Tests for reading in data from a file and ensuring algorithm returns valid and optimal schedule with no
     * lower bound or pruning.
     */
    @Test
    public void testAlgorithm7Node4ProcessorNoHeuristics() {

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
        Algorithm algorithm = new DFSAlgorithm(4, new IsNotAPruner(), new NaiveBound());
        algorithm.start(graph);

        //Manually start algorithm on graph
        Schedule resultManual = algorithm.getCurrentBest();

        assertEquals(22, resultManual.getEndTime()); // Check answer is optimal
        Assert.assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }




    /**
     * Tests for reading in data from a file and ensuring algorithm returns valid and optimal schedule with
     * lower bounds and pruning.
     */
    @Test
    public void testAlgorithm7Node2ProcessorAllHeuristics() {

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
        _algorithmWith2Processors.start(graph);
        Schedule resultManual = _algorithmWith2Processors.getCurrentBest();

        assertEquals(28, resultManual.getEndTime()); // Check answer is optimal
        Assert.assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }




    /**
     * Tests for reading in data from a file and ensuring algorithm returns valid and optimal schedule with
     * lower bounds and pruning.
     */
    @Test
    public void testAlgorithm7Node4ProcessorAllHeuristics() {

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
        _algorithmWith4Processors.start(graph);
        Schedule resultManual = _algorithmWith4Processors.getCurrentBest();

        assertEquals(22, resultManual.getEndTime()); // Check answer is optimal
        Assert.assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }




    // Tests for cli interacting with reader
    // Tests for writing out

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

        // Execute algorithm w/ all heuristics
        _algorithmWith2Processors.start(graph);
        Schedule resultManual = _algorithmWith2Processors.getCurrentBest();

        assertEquals(581, resultManual.getEndTime()); // Check answer is optimal
        Assert.assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }




    /**
     * Test tests algorithm against graph with 8 nodes and 3 layers, one two processors with critical path
     * heuristics
     */
    @Test
    public void testAlgorithm8Node4ProcessorAllHeuristics() {

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

        // Execute algorithm w/ all heuristics
        _algorithmWith4Processors.start(graph);
        Schedule resultManual = _algorithmWith4Processors.getCurrentBest();

        assertEquals(581, resultManual.getEndTime()); // Check answer is optimal
        Assert.assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }




    /**
     * Test tests algorithm against graph with 8 nodes and 3 layers, one two processors with all heuristics
     */
    @Test
    public void testAlgorithm9Node2ProcessorAllHeuristics() {

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

        // Execute algorithm w/ all heuristics
        _algorithmWith2Processors.start(graph);
        Schedule resultManual = _algorithmWith2Processors.getCurrentBest();

        assertEquals(55, resultManual.getEndTime()); // Check answer is optimal
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

        // Execute algorithm w/ all heuristics
        _algorithmWith4Processors.start(graph);
        Schedule resultManual = _algorithmWith4Processors.getCurrentBest();

        assertEquals(55, resultManual.getEndTime()); // Check answer is optimal
        Assert.assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }




    /**
     * Test tests algorithm against graph with 10 nodes, on 4 processors with critical path and pruner
     * heuristics
     */
    @Test
    public void testAlgorithm10Node2ProcessorAllHeuristics() {

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

        // Execute algorithm w/ all heuristics
        _algorithmWith2Processors.start(graph);
        Schedule resultManual = _algorithmWith2Processors.getCurrentBest();

        assertEquals(50, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }





    /**
     * Test tests algorithm against graph with 10 nodes, on 4 processors with critical path and pruner
     * heuristics
     */
    @Test
    public void testAlgorithm10Node4ProcessorAllHeuristics() {

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

        // Execute algorithm w/ all heuristics
        _algorithmWith4Processors.start(graph);
        Schedule resultManual = _algorithmWith4Processors.getCurrentBest();

        assertEquals(50, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }




    /**
     * Test tests algorithm against graph with 11 nodes on 4 processors with all heuristics
     */
    @Test
    public void testAlgorithm11Node2ProcessorAllHeuristics() {

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

        // Execute algorithm w/ all heuristics
        _algorithmWith2Processors.start(graph);
        Schedule resultManual = _algorithmWith2Processors.getCurrentBest();

        assertEquals(350, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check result is valid
    }




    /**
     * Test tests algorithm against graph with 11 nodes on 4 processors with all heuristics
     */
    @Test
    public void testAlgorithm11Node4ProcessorAllHeuristics() {

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

        // Execute algorithm w/ all heuristics
        _algorithmWith4Processors.start(graph);
        Schedule resultManual = _algorithmWith4Processors.getCurrentBest();

        assertEquals(227, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check result is valid
    }
}
