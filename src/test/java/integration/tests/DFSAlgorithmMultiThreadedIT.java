package integration.tests;

import algorithm.*;
import algorithm.heuristics.lowerbound.CriticalPath;
import algorithm.heuristics.lowerbound.NaiveBound;
import algorithm.heuristics.pruner.IsNotAPruner;
import algorithm.heuristics.pruner.ProcessorOrderPruner;
import algorithm.heuristics.pruner.StartTimePruner;
import common.Validator;
import common.categories.GandalfIntegrationTestsCategory;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.SimpleSchedule;
import integration.tests.repeatable.test.RepeatTest;
import integration.tests.repeatable.test.RepeatedTestRule;
import io.GraphReader;
import io.dot.DotGraphReader;
import org.junit.*;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

/**
 * Class provides test to test algorithms running with threading. Makes use of @RepeatTest rule to run a test
 * multiple times. This is to try and counter and catch errors in threads that might ocour sipiradiacouly. By
 * running a test multiple times we are thoroughly testing it being run in threads.
 */
@Category(GandalfIntegrationTestsCategory.class)
public class DFSAlgorithmMultiThreadedIT {

    private Algorithm _algorithmhAllHeuristics4Threads4Processors;
    private Algorithm _algorithmhAllHeuristics2Threads2Processors;

    private static final String SEP = File.separator;
    private static final String NODES_7_FILENAME = String.format("data%sgraphs%sNodes_7_OutTree.dot", SEP, SEP);
    private static final String NODES_11_FILENAME = String.format("data%sgraphs%sNodes_11_OutTree.dot", SEP, SEP);
    private static final String NODES_10_FILENAME = String.format("data%sgraphs%sNodes_10_Random.dot", SEP, SEP);

    @Rule
    public RepeatedTestRule repeatRule = new RepeatedTestRule();

    @Before
    public void setup() {

        // Set up algorithm classes
        _algorithmhAllHeuristics4Threads4Processors = new TieredAlgorithm(4, 4,
                (tier, notifier, globalBest) -> new DFSAlgorithm(
                    (pruningGraph, pruningSchedule, pruningTask) ->
                            new StartTimePruner().prune(pruningGraph, pruningSchedule, pruningTask) ||
                                    new ProcessorOrderPruner().prune(pruningGraph, pruningSchedule, pruningTask),
                new CriticalPath(),
                notifier,
                globalBest
        ));

        _algorithmhAllHeuristics2Threads2Processors = new TieredAlgorithm(2, 2,
                (tier, notifier, globalBest) -> new DFSAlgorithm(
                        (pruningGraph, pruningSchedule, pruningTask) ->
                                new StartTimePruner().prune(pruningGraph, pruningSchedule, pruningTask) ||
                                        new ProcessorOrderPruner().prune(pruningGraph, pruningSchedule, pruningTask),
                        new CriticalPath(),
                        notifier,
                        globalBest
                ));
    }

    @Test
    @RepeatTest(times = 2)
    public void testAlgorithm7Node4ProcessorAllHeuristics4Threads(){
        Graph graph = getGraph(NODES_7_FILENAME);

        // Execute algorithm w/ no heuristics
        _algorithmhAllHeuristics4Threads4Processors.run(graph, 4);

        //Manually start algorithm on graph
        Schedule resultManual = _algorithmhAllHeuristics4Threads4Processors.getCurrentBest();

        assertEquals(22, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }

    @Test
    @RepeatTest(times = 3)
    public void testAlgorithm11Node4ProcessorAllHeuristics4Threads() {

        Graph graph = getGraph(NODES_11_FILENAME);

        // Execute algorithm w/ all heuristics
        _algorithmhAllHeuristics4Threads4Processors.run(graph, 4);
        Schedule resultManual = _algorithmhAllHeuristics4Threads4Processors.getCurrentBest();

        assertEquals(227, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check result is valid
    }

    @Test
    @RepeatTest(times = 3)
    public void testAlgorithm10Node2ProcessorAllHeuristics2Threads(){
        Graph graph = getGraph(NODES_10_FILENAME);

        // Execute algorithm w/ all heuristics
        _algorithmhAllHeuristics2Threads2Processors.run(graph, 2);
        Schedule resultManual = _algorithmhAllHeuristics2Threads2Processors.getCurrentBest();

        assertEquals(50, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
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
