package integration;

import algorithm.AStarAlgorithm;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

/**
 * Runs thorough test suite for A* on giant data set. Runs multithreading with 4 threads.
 *
 * Also runs through all data provided to us on Canvas with varying numbers of threads allocated.
 */
public class AStarMultiThreadingIntegrationTests extends IntegrationTest {

    private Algorithm _algorithmhAllHeuristics4Threads;
    private Algorithm _algorithmhAllHeuristics2Threads;
    private Algorithm _algorithmhAllHeuristics40Threads;

    private static final String SEP = File.separator;
    private static final String NODES_7_FILENAME = String.format("data%sgraphs%sNodes_7_OutTree.dot", SEP, SEP);
    private static final String NODES_11_FILENAME = String.format("data%sgraphs%sNodes_11_OutTree.dot", SEP, SEP);
    private static final String NODES_10_FILENAME = String.format("data%sgraphs%sNodes_10_Random.dot", SEP, SEP);

    public AStarMultiThreadingIntegrationTests() {
        super();
    }

    @Override
    protected void runAgainstOptimal(String graph, int processors, int optimalScheduleLength) {

        // Single threaded DFS implementation
        Algorithm dfsAlgorithm = new TieredAlgorithm(4,
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


        GraphReader reader = null;
        try {
            reader = new DotGraphReader(new FileInputStream(graph));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Graph inputGraph = reader.read();

        dfsAlgorithm.run(inputGraph, processors);
        System.out.println("graph name: " + inputGraph.getName());
        System.out.println("processors: " + processors);
        Schedule schedule = dfsAlgorithm.getCurrentBest();

        Assert.assertEquals(optimalScheduleLength, schedule.getEndTime());
        Assert.assertTrue(Validator.isValid(inputGraph, schedule));
    }

    /**
     * The following tests run on the ALL the original data sets supplied by O Sinnen.
     * Keeping them in because it is imperative our code runs properly on these graphs and they will not integrate
     * nicely with our current method of running automated tests.
     */

    @BeforeEach
    public void setup() {
        // Set up algorithm classes
        _algorithmhAllHeuristics4Threads = generateTieredAlgorithm(4);

        _algorithmhAllHeuristics2Threads = generateTieredAlgorithm(2);

        _algorithmhAllHeuristics40Threads = generateTieredAlgorithm(40);

    }

    @Test
    public void testAStarAlgorithm7Node4ProcessorAllHeuristics4Threads(){
        Graph graph = getGraph(NODES_7_FILENAME);

        // Execute algorithm w/ no heuristics
        _algorithmhAllHeuristics4Threads.run(graph, 4);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Manually start algorithm on graph
        Schedule resultManual = _algorithmhAllHeuristics4Threads.getCurrentBest();

        assertEquals(22, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }

    @Test
    public void testAStarAlgorithm7Node4ProcessorAllHeuristics2Threads(){
        Graph graph = getGraph(NODES_7_FILENAME);

        // Execute algorithm w/ no heuristics
        _algorithmhAllHeuristics2Threads.run(graph, 4);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Manually start algorithm on graph
        Schedule resultManual = _algorithmhAllHeuristics2Threads.getCurrentBest();

        assertEquals(22, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }

    @Test
    public void testAStarAlgorithm7Node4ProcessorAllHeuristics40Threads(){
        Graph graph = getGraph(NODES_7_FILENAME);

        // Execute algorithm w/ no heuristics
        _algorithmhAllHeuristics40Threads.run(graph, 4);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Manually start algorithm on graph
        Schedule resultManual = _algorithmhAllHeuristics40Threads.getCurrentBest();

        assertEquals(22, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }

    @Test
    public void testAStarAlgorithm11Node4ProcessorAllHeuristics4Threads() {

        Graph graph = getGraph(NODES_11_FILENAME);

        // Execute algorithm w/ all heuristics
        _algorithmhAllHeuristics4Threads.run(graph, 4);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Schedule resultManual = _algorithmhAllHeuristics4Threads.getCurrentBest();

        assertEquals(227, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check result is valid
    }

    @Test
    public void testAStarAlgorithm11Node4ProcessorAllHeuristics2Threads() {

        Graph graph = getGraph(NODES_11_FILENAME);

        // Execute algorithm w/ all heuristics
        _algorithmhAllHeuristics2Threads.run(graph, 4);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Schedule resultManual = _algorithmhAllHeuristics2Threads.getCurrentBest();

        assertEquals(227, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check result is valid
    }

    @Test
    public void testAStarAlgorithm11Node4ProcessorAllHeuristics40Threads() {

        Graph graph = getGraph(NODES_11_FILENAME);

        // Execute algorithm w/ all heuristics
        _algorithmhAllHeuristics40Threads.run(graph, 4);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Schedule resultManual = _algorithmhAllHeuristics40Threads.getCurrentBest();

        assertEquals(227, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check result is valid
    }

    @Test
    public void testAStarAlgorithm10Node2ProcessorAllHeuristics4Threads(){
        Graph graph = getGraph(NODES_10_FILENAME);

        // Execute algorithm w/ all heuristics
        _algorithmhAllHeuristics4Threads.run(graph, 2);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Schedule resultManual = _algorithmhAllHeuristics4Threads.getCurrentBest();

        assertEquals(50, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }

    @Test
    public void testAStarAlgorithm10Node2ProcessorAllHeuristics2Threads(){
        Graph graph = getGraph(NODES_10_FILENAME);

        // Execute algorithm w/ all heuristics
        _algorithmhAllHeuristics2Threads.run(graph, 2);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Schedule resultManual = _algorithmhAllHeuristics2Threads.getCurrentBest();

        assertEquals(50, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }

    @Test
    public void testAStarAlgorithm10Node2ProcessorAllHeuristics40Threads(){
        Graph graph = getGraph(NODES_10_FILENAME);

        // Execute algorithm w/ all heuristics
        _algorithmhAllHeuristics40Threads.run(graph, 2);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Schedule resultManual = _algorithmhAllHeuristics40Threads.getCurrentBest();

        assertEquals(50, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }


    /**
     * Generates a tiered AStar algorithm with the specified number of threads.
     * @param threads : number of threads for tiered algorithm to run on.
     */
    private TieredAlgorithm generateTieredAlgorithm(int threads) {
        return new TieredAlgorithm(threads,
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
