package algorithm;

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
import io.GraphReader;
import io.dot.DotGraphReader;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

/**
 * Calss is a test suite that tests GreedyAlgorithm. Runs it with pre-made graphs and test graph files. Each test
 * verifys that the schedule is a valid schedule, not that it is optimal. Does not
 */
@Category(GandalfIntegrationTestsCategory.class)
public class GreedyAlgorithmTests {

    private static final String SEP = File.separator;
    private static final String NODES_7_FILENAME = String.format("data%sgraphs%sNodes_7_OutTree.dot", SEP, SEP);
    private static final String NODES_8_FILENAME = String.format("data%sgraphs%sNodes_8_Random.dot", SEP, SEP);
    private static final String NODES_9_FILENAME = String.format("data%sgraphs%sNodes_9_SeriesParallel.dot", SEP, SEP);
    private static final String NODES_10_FILENAME = String.format("data%sgraphs%sNodes_10_Random.dot", SEP, SEP);
    private static final String NODES_11_FILENAME = String.format("data%sgraphs%sNodes_11_OutTree.dot", SEP, SEP);

    @Test
    public void testBasic3NodeGraph() {
        Graph g = new Graph.Builder()
                .node("a", 2)
                .node("b", 3)
                .node("c", 5)
                .node("d", 1)
                .node("e", 3)
                .edge("a", "b", 1)
                .edge("a", "c", 1)
                .edge("c", "d", 1)
                .edge("b", "e", 1)
                .edge("d", "e", 2)
                .build();
        Algorithm algorithm = new GreedyAlgorithm();
        algorithm.run(g, 2);

        assertTrue(Validator.isValid(g, algorithm.getCurrentBest())); // Check answer is valid
    }

    @Test
    public void test7Node2ProcessorGraph() {
        Graph graph = getGraph(NODES_7_FILENAME);

        // Execute algorithm w/ no heuristics
        Algorithm algorithm = new GreedyAlgorithm();
        algorithm.run(graph, 2);

        //Manually start algorithm on graph
        Schedule resultManual = algorithm.getCurrentBest();

        assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }

    @Test
    public void test7Node4ProcessorGraph() {
        Graph graph = getGraph(NODES_7_FILENAME);

        // Execute algorithm w/ no heuristics
        Algorithm algorithm = new GreedyAlgorithm();
        algorithm.run(graph, 4);

        //Manually start algorithm on graph
        Schedule resultManual = algorithm.getCurrentBest();

        assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }

    @Test
    public void test8Node2ProcessorGraph() {
        Graph graph = getGraph(NODES_8_FILENAME);

        // Execute algorithm w/ no heuristics
        Algorithm algorithm = new GreedyAlgorithm();
        algorithm.run(graph, 2);

        //Manually start algorithm on graph
        Schedule resultManual = algorithm.getCurrentBest();

        assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }

    @Test
    public void test8Node4ProcessorGraph() {
        Graph graph = getGraph(NODES_8_FILENAME);

        // Execute algorithm w/ no heuristics
        Algorithm algorithm = new GreedyAlgorithm();
        algorithm.run(graph, 4);

        //Manually start algorithm on graph
        Schedule resultManual = algorithm.getCurrentBest();

        assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }

    @Test
    public void test9Node2ProcessorGraph() {
        Graph graph = getGraph(NODES_9_FILENAME);

        // Execute algorithm w/ no heuristics
        Algorithm greedyAlgorithm = new GreedyAlgorithm();
        greedyAlgorithm.run(graph, 2);

        //Manually start algorithm on graph
        Schedule resultGreedy = greedyAlgorithm.getCurrentBest();

        assertTrue(Validator.isValid(graph, resultGreedy)); // Check answer is valid

    }

    @Test
    public void test9Node4ProcessorGraph() {
        Graph graph = getGraph(NODES_9_FILENAME);

        // Execute algorithm w/ no heuristics
        Algorithm greedyAlgorithm = new GreedyAlgorithm();
        greedyAlgorithm.run(graph, 4);

        //Manually start algorithm on graph
        Schedule resultGreedy = greedyAlgorithm.getCurrentBest();

        assertTrue(Validator.isValid(graph, resultGreedy)); // Check answer is valid

    }

    @Test
    public void test10Node2ProcessorGraph() {
        Graph graph = getGraph(NODES_10_FILENAME);

        // Execute algorithm w/ no heuristics
        Algorithm greedyAlgorithm = new GreedyAlgorithm();
        greedyAlgorithm.run(graph, 2);

        //Manually start algorithm on graph
        Schedule resultGreedy = greedyAlgorithm.getCurrentBest();

        assertTrue(Validator.isValid(graph, resultGreedy)); // Check answer is valid

    }

    @Test
    public void test10Node4ProcessorGraph() {
        Graph graph = getGraph(NODES_10_FILENAME);

        // Execute algorithm w/ no heuristics
        Algorithm greedyAlgorithm = new GreedyAlgorithm();
        greedyAlgorithm.run(graph, 4);

        //Manually start algorithm on graph
        Schedule resultGreedy = greedyAlgorithm.getCurrentBest();

        assertTrue(Validator.isValid(graph, resultGreedy)); // Check answer is valid

    }

    @Test
    public void test11Node2ProcessorGraph() {
        Graph graph = getGraph(NODES_11_FILENAME);

        // Execute algorithm w/ no heuristics
        Algorithm greedyAlgorithm = new GreedyAlgorithm();
        greedyAlgorithm.run(graph, 2);

        //Manually start algorithm on graph
        Schedule resultGreedy = greedyAlgorithm.getCurrentBest();

        assertTrue(Validator.isValid(graph, resultGreedy)); // Check answer is valid

    }

    @Test
    public void test11Node4ProcessorGraph() {
        Graph graph = getGraph(NODES_11_FILENAME);

        // Execute algorithm w/ no heuristics
        Algorithm greedyAlgorithm = new GreedyAlgorithm();
        greedyAlgorithm.run(graph, 4);

        //Manually start algorithm on graph
        Schedule resultGreedy = greedyAlgorithm.getCurrentBest();

        assertTrue(Validator.isValid(graph, resultGreedy)); // Check answer is valid

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
