package integration.tests;

import algorithm.*;
import algorithm.heuristics.lowerbound.CriticalPath;
import algorithm.heuristics.pruner.Arborist;
import algorithm.heuristics.pruner.ProcessorOrderPruner;
import algorithm.heuristics.pruner.StartTimePruner;
import common.Validator;
import common.graph.Graph;
import common.schedule.Schedule;
import integration.IntegrationTest;
import io.GraphReader;
import io.dot.DotGraphReader;
import org.junit.*;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

/**
 * Class provides test to test algorithms running with threading. Makes use of @RepeatTest rule to run a test
 * multiple times. This is to try and counter and catch errors in threads that might occur sipiradiacouly. By
 * running a test multiple times we are thoroughly testing it being run in threads.
 *
 * All graphs are run on both two and four threads, to ensure expected behavior is the same for both.
 */
@Category(IntegrationTest.class)
public class AStarAlgorithmMultiThreadedIT {

    private Algorithm _algorithmhAllHeuristics4Threads;
    private Algorithm _algorithmhAllHeuristics2Threads;
    private Algorithm _algorithmhAllHeuristics40Threads;

    private static final String SEP = File.separator;
    private static final String NODES_7_FILENAME = String.format("data%sgraphs%sNodes_7_OutTree.dot", SEP, SEP);
    private static final String NODES_11_FILENAME = String.format("data%sgraphs%sNodes_11_OutTree.dot", SEP, SEP);
    private static final String NODES_10_FILENAME = String.format("data%sgraphs%sNodes_10_Random.dot", SEP, SEP);


    @Before
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
