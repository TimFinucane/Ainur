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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

public class AlgorithmIntegrationTests {

    @Test
    public void testNodes_7_OutTreeGraph() {

        // Set up File
        File graphFile = new File("data/graphs/Nodes_7_OutTree.dot");
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
        String[] args = {"data/graphs/Nodes_7_OutTree.dot", "2"};
        Cli cli = new Cli(args) {
            @Override
            protected Schedule startScheduling(Graph graph) {
                Algorithm algorithm = new DFSAlgorithm(2, new IsNotAPruner(), new NaiveBound());

                algorithm.start(graph);
                Schedule result = algorithm.getCurrentBest();
                return result;
            }
        };
        cli.parse();

        // Check that output file is all good
        File outputFile = new File("data/graphs/Nodes_7_OutTree_processed.dot");
        assertTrue(outputFile.exists());

        InputStream outputGraphStream = null;
        try {
            outputGraphStream = new FileInputStream(outputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail();
        }

        // Somehow make sure schedule given by dot file matches schedule given by manually making one with methods
        // Convert schedules to graphs to use in comparison
        String schedule1 = "digraph \"Processor#1\" {\n" +
                "\t0\t [Weight=5];\n" +
                "\t3\t [Weight=6];\n" +
                "\t0 -> 3\t [Weight=0];\n" +
                "\t1\t [Weight=6];\n" +
                "\t3 -> 1\t [Weight=0];\n" +
                "\t6\t [Weight=7];\n" +
                "\t1 -> 6\t [Weight=0];\n" +
                "\t4\t [Weight=4];\n" +
                "\t6 -> 4\t [Weight=0];\n" +
                "}\n";
        InputStream stream1 = new ByteArrayInputStream(schedule1.getBytes(StandardCharsets.UTF_8));
        GraphReader reader1 = new DotGraphReader(stream1);
        Graph graph1 = reader1.read();

        String schedule2 = "digraph \"Processor#0\" {\n" +
                "\t2\t [Weight=5];\n" +
                "\t5\t [Weight=7];\n" +
                "\t2 -> 5\t [Weight=0];\n" +
                "}\n";
        InputStream stream2 = new ByteArrayInputStream(schedule2.getBytes(StandardCharsets.UTF_8));
        GraphReader reader2 = new DotGraphReader(stream2);
        Graph graph2 = reader2.read();

        //Make Schedules
        Schedule outputSchedule = new Schedule(2);
        int startTime = 0;
        Processor p = outputSchedule.getProcessors().get(0);
            for(Node n : graph1.getNodes()){
                p.addTask(new Task(startTime, n));
                startTime = startTime + n.getComputationCost();
            }

        startTime = 0;
        Processor p1 = outputSchedule.getProcessors().get(1);
            for(Node n : graph2.getNodes()){
                p1.addTask(new Task(startTime, n));
                startTime = startTime + n.getComputationCost();
            }

        assertEquals(28, outputSchedule.getTotalTime());

    }



}
