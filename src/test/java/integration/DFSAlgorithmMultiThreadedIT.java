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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static junit.framework.TestCase.*;

/**
 * Class provides test to test algorithms running with threading. Makes use of @RepeatedTest to run a test
 * multiple times. This is to try and counter and catch errors in threads that might ocour sipiradiacouly. By
 * running a test multiple times we are thoroughly testing it being run in threads.
 */
@Tag("gandalf") // Gandalf tests may be slow, but they finish precisely when they mean to
public class DFSAlgorithmMultiThreadedIT {

    private Algorithm _algorithmhAllHeuristics4Threads;

    private static final String SEP = File.separator;
    private static final String NODES_7_FILENAME = String.format("data%sgraphs%sNodes_7_OutTree.dot", SEP, SEP);
    private static final String NODES_11_FILENAME = String.format("data%sgraphs%sNodes_11_OutTree.dot", SEP, SEP);
    private static final String NODES_10_FILENAME = String.format("data%sgraphs%sNodes_10_Random.dot", SEP, SEP);

    @BeforeEach
    public void setup() {
        // Set up algorithm classes
        _algorithmhAllHeuristics4Threads = new TieredAlgorithm(4,
                (tier, communicator) -> new DFSAlgorithm(
                    communicator,
                    Arborist.combine(new StartTimePruner(), new ProcessorOrderPruner()),
                    new CriticalPath(),
                    tier == 0 ? 8 : 10000
        ));
    }

    @RepeatedTest(2)
    public void testAlgorithm7Node4ProcessorAllHeuristics4Threads(){
        Graph graph = getGraph(NODES_7_FILENAME);

        // Execute algorithm w/ no heuristics
        _algorithmhAllHeuristics4Threads.run(graph, 4);

        //Manually start algorithm on graph
        Schedule resultManual = _algorithmhAllHeuristics4Threads.getCurrentBest();

        assertEquals(22, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check answer is valid
    }

    @RepeatedTest(3)
    public void testAlgorithm11Node4ProcessorAllHeuristics4Threads() {

        Graph graph = getGraph(NODES_11_FILENAME);

        // Execute algorithm w/ all heuristics
        _algorithmhAllHeuristics4Threads.run(graph, 4);
        Schedule resultManual = _algorithmhAllHeuristics4Threads.getCurrentBest();

        assertEquals(227, resultManual.getEndTime()); // Check answer is optimal
        assertTrue(Validator.isValid(graph, resultManual)); // Check result is valid
    }


    @RepeatedTest(3)
    public void testAlgorithm10Node2ProcessorAllHeuristics2Threads(){
        Graph graph = getGraph(NODES_10_FILENAME);

        // Execute algorithm w/ all heuristics
        _algorithmhAllHeuristics4Threads.run(graph, 2);
        Schedule resultManual = _algorithmhAllHeuristics4Threads.getCurrentBest();

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
