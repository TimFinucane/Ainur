package integration.tests;

import algorithm.*;
import algorithm.heuristics.lowerbound.CriticalPath;
import algorithm.heuristics.lowerbound.NaiveBound;
import algorithm.heuristics.pruner.IsNotAPruner;
import algorithm.heuristics.pruner.ProcessorOrderPruner;
import algorithm.heuristics.pruner.StartTimePruner;
import common.Validator;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.SimpleSchedule;
import io.GraphReader;
import io.dot.DotGraphReader;
import org.junit.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class DFSAlgorithmMultiThreadedIT {

    private Algorithm _algorithmhAllHeuristics4Threads4Processors;
    private Algorithm _algorithmhAllHeuristics2Threads2Processors;

    private static final String SEP = File.separator;
    private static final String NODES_7_FILENAME = String.format("data%sgraphs%sNodes_7_OutTree.dot", SEP, SEP);
    private static final String NODES_11_FILENAME = String.format("data%sgraphs%sNodes_11_OutTree.dot", SEP, SEP);

    @BeforeAll
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
    @RepeatedTest(2)
    public void testAlgorithm11Node4ProcessorAllHeuristics() {

        Graph graph = getGraph(NODES_11_FILENAME);

        // Execute algorithm w/ all heuristics
        _algorithmhAllHeuristics4Threads4Processors.run(graph, 4);
        Schedule resultManual = _algorithmhAllHeuristics4Threads4Processors.getCurrentBest();

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
