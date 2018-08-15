package integration.tests;

import algorithm.AStarAlgorithm;
import algorithm.Algorithm;
import algorithm.DFSAlgorithm;
import algorithm.TieredAlgorithm;
import algorithm.heuristics.lowerbound.CriticalPath;
import algorithm.heuristics.lowerbound.NaiveBound;
import algorithm.heuristics.pruner.Arborist;
import algorithm.heuristics.pruner.IsNotAPruner;
import algorithm.heuristics.pruner.ProcessorOrderPruner;
import algorithm.heuristics.pruner.StartTimePruner;
import common.Validator;
import common.categories.GandalfIntegrationTestsCategory;
import common.graph.Graph;
import common.schedule.Schedule;
import integration.tests.repeatable.test.RepeatTest;
import io.GraphReader;
import io.dot.DotGraphReader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import scala.Int;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static junit.framework.TestCase.*;

/**
 * This class's purpose is to provide a suite for testing AStarAlgorithm's functionality with regards to reading in a graph
 * object and outputting a schedule that is both optimal and valid. Graphs are sourced from data/graphs/, reading in of
 * of these also occurs before every test.
 */
@Category(GandalfIntegrationTestsCategory.class)
public class AStarAlgorithmIT {

    private static final String SEP = File.separator;

    private Algorithm _algorithmWithAllHeuristics;
    private Algorithm _algorithmhAllHeuristics4Threads;

    private static final String NODES_7_FILENAME = String.format("data%sgraphs%sNodes_7_OutTree.dot", SEP, SEP);
    private static final String NODES_8_FILENAME = String.format("data%sgraphs%sNodes_8_Random.dot", SEP, SEP);
    private static final String NODES_9_FILENAME = String.format("data%sgraphs%sNodes_9_SeriesParallel.dot", SEP, SEP);
    private static final String NODES_10_FILENAME = String.format("data%sgraphs%sNodes_10_Random.dot", SEP, SEP);
    private static final String NODES_11_FILENAME = String.format("data%sgraphs%sNodes_11_OutTree.dot", SEP, SEP);


    @Before
    public void setup() {

        // Set up algorithm classes
        _algorithmWithAllHeuristics = new AStarAlgorithm(
                Arborist.combine(new StartTimePruner(), new ProcessorOrderPruner()),
                new CriticalPath());

        // Set up algorithm classes
        _algorithmhAllHeuristics4Threads = new TieredAlgorithm(4,
                (tier, communicator) -> {
                if (tier == 0) {
                    return new AStarAlgorithm(
                            communicator,
                            Arborist.combine(new StartTimePruner(), new ProcessorOrderPruner()),
                            new CriticalPath());
                } else {
                    return new DFSAlgorithm(
                            communicator,
                            Arborist.combine(new StartTimePruner(), new ProcessorOrderPruner()),
                            new CriticalPath(),
                            Integer.MAX_VALUE);
                }
                });
    }



    /**
     * Tests for reading in data from a file and ensuring algorithm returns valid and optimal schedule with no
     * lower bound or pruning.
     */
    @Test
    public void testAlgorithm7Node4ProcessorNoHeuristics() {

        Graph graph = getGraph(NODES_7_FILENAME);

        // Execute algorithm w/ no heuristics
        Algorithm algorithm = new AStarAlgorithm(new IsNotAPruner(), new NaiveBound());
        algorithm.run(graph, 4);

        //Manually start algorithm on graph
        Schedule resultManual = algorithm.getCurrentBest();

        assertEquals(22, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }




    /**
     * Tests for reading in data from a file and ensuring algorithm returns valid and optimal schedule with
     * lower bounds and pruning.
     */
    @Test
    public void testAlgorithm7Node2ProcessorAllHeuristics() {

        Graph graph = getGraph(NODES_7_FILENAME);

        // Execute algorithm w/ all heuristics
        _algorithmWithAllHeuristics.run(graph, 2);
        Schedule resultManual = _algorithmWithAllHeuristics.getCurrentBest();

        assertEquals(28, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }




    /**
     * Tests for reading in data from a file and ensuring algorithm returns valid and optimal schedule with
     * lower bounds and pruning.
     */
    @Test
    public void testAlgorithm7Node4ProcessorAllHeuristics() {

        Graph graph = getGraph(NODES_7_FILENAME);

        // Execute algorithm w/ all heuristics
        _algorithmWithAllHeuristics.run(graph, 4);
        Schedule resultManual = _algorithmWithAllHeuristics.getCurrentBest();

        assertEquals(22, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }




    // Tests for cli interacting with reader
    // Tests for writing out

    /**
     * Test tests algorithm against graph with 8 nodes and 2 layers, one two processors with critical path
     * heuristics
     */
    @Test
    public void testAlgorithm8Node2ProcessorAllHeuristics() {

        Graph graph = getGraph(NODES_8_FILENAME);

        // Execute algorithm w/ all heuristics
        _algorithmWithAllHeuristics.run(graph, 2);
        Schedule resultManual = _algorithmWithAllHeuristics.getCurrentBest();

        assertEquals(581, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }




    /**
     * Test tests algorithm against graph with 8 nodes and 4 layers, one two processors with critical path
     * heuristics
     */
    @Test
    public void testAlgorithm8Node4ProcessorAllHeuristics() {

        Graph graph = getGraph(NODES_8_FILENAME);

        // Execute algorithm w/ all heuristics
        _algorithmWithAllHeuristics.run(graph, 4);
        Schedule resultManual = _algorithmWithAllHeuristics.getCurrentBest();

        assertEquals(581, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }




    /**
     * Test tests algorithm against graph with 9 nodes and 2 layers, one two processors with all heuristics
     */
    @Test
    public void testAlgorithm9Node2ProcessorAllHeuristics() {

        Graph graph = getGraph(NODES_9_FILENAME);

        // Execute algorithm w/ all heuristics
        _algorithmWithAllHeuristics.run(graph, 2);
        Schedule resultManual = _algorithmWithAllHeuristics.getCurrentBest();

        assertEquals(55, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }




    /**
     * Test tests algorithm against graph with 9 nodes and 4 layers, one two processors with all heuristics
     */
    @Test
    public void testAlgorithm9Node4ProcessorAllHeuristics() {

        Graph graph = getGraph(NODES_9_FILENAME);

        // Execute algorithm w/ all heuristics
        _algorithmWithAllHeuristics.run(graph, 2);
        Schedule resultManual = _algorithmWithAllHeuristics.getCurrentBest();

        assertEquals(55, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }




    /**
     * Test tests algorithm against graph with 10 nodes, on 2 processors with critical path and pruner
     * heuristics
     */
    @Test
    public void testAlgorithm10Node2ProcessorAllHeuristics() {

        Graph graph = getGraph(NODES_10_FILENAME);

        // Execute algorithm w/ all heuristics
        _algorithmWithAllHeuristics.run(graph, 2);
        Schedule resultManual = _algorithmWithAllHeuristics.getCurrentBest();

        assertEquals(50, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }


    /**
     * Test tests algorithm against graph with 10 nodes, on 4 processors with critical path and pruner
     * heuristics
     */
    @Test
    public void testAlgorithm10Node4ProcessorAllHeuristics() {

        Graph graph = getGraph(NODES_10_FILENAME);

        // Execute algorithm w/ all heuristics
        _algorithmWithAllHeuristics.run(graph, 4);
        Schedule resultManual = _algorithmWithAllHeuristics.getCurrentBest();

        assertEquals(50, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }




    /**
     * Test tests algorithm against graph with 11 nodes on 2 processors with all heuristics
     */
    @Test
    public void testAlgorithm11Node2ProcessorAllHeuristics() {

        Graph graph = getGraph(NODES_11_FILENAME);

        // Execute algorithm w/ all heuristics
        _algorithmWithAllHeuristics.run(graph, 2);
        Schedule resultManual = _algorithmWithAllHeuristics.getCurrentBest();

        assertEquals(350, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check result is valid
    }




    /**
     * Test tests algorithm against graph with 11 nodes on 4 processors with all heuristics
     */
    @Test
    public void testAlgorithm11Node4ProcessorAllHeuristics() {

        Graph graph = getGraph(NODES_11_FILENAME);

        // Execute algorithm w/ all heuristics
        _algorithmWithAllHeuristics.run(graph, 4);
        Schedule resultManual = _algorithmWithAllHeuristics.getCurrentBest();

        assertEquals(227, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check result is valid
    }


    /**
     * WARNING: Test will not reliably pass without sleep function as there are intermittent concurrency issues with
     * current implementation of threading with A*.
     */
    @Test
    @RepeatTest(times = 3)
    public void testAlgorithm11Node4ProcessorAllHeuristics4Threads() {

        Graph graph = getGraph(NODES_11_FILENAME);

        // Execute algorithm w/ all heuristics
        _algorithmhAllHeuristics4Threads.run(graph, 4);

        // TODO fix bug that requires sleeping
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Schedule resultManual = _algorithmhAllHeuristics4Threads.getCurrentBest();

        assertEquals(227, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check result is valid
    }




    private Graph getGraph(String filePath) {
        // Set up File
        File graphFile = new File(filePath);
        InputStream graphStream = null;

        try {
            graphStream = new FileInputStream(graphFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail("File not found: " + filePath);
        }

        //Try making graph from file and check that it is correct
        GraphReader reader = new DotGraphReader(graphStream);
        return reader.read();
    }
}
