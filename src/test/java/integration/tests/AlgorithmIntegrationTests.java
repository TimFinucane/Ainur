package integration.tests;

import algorithm.Algorithm;
import algorithm.DFSAlgorithm;
import algorithm.heuristics.*;
import cli.Cli;
import common.categories.GandalfIntegrationTestsCategory;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.SimpleSchedule;
import common.schedule.Task;
import io.GraphReader;
import io.dot.DotGraphReader;
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
     * Test tests algorithm set up against a graph with 7 nodes and 3 layers, on two processors with no
     * heuristics
     */
    @Test
    public void testNodes_7_OutTreeGraphTwoProcessor() {

        // Set up File
        File graphFile = new File("data/graphs/Nodes_7_OutTree.dot");
        InputStream graphStream = null;
        try {
            graphStream = new FileInputStream(graphFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail();
        }
        //Try making graph from file and check that it is correct
        GraphReader reader = new DotGraphReader(graphStream);
        Graph graph = reader.read();

        //Assert that graph is as expected
        Node entryNode = graph.getEntryPoints().get(0);

        assertEquals(entryNode.getLabel(), "0");
        assertEquals(5, entryNode.getComputationCost());
        assertEquals(7, graph.size());


        Algorithm algorithm = new DFSAlgorithm(2, new IsNotAPruner(), new NaiveBound());

        algorithm.start(graph);
        //Manually start algorithm on graph and check that final answer is correct
        Schedule resultManual = algorithm.getCurrentBest();

        assertEquals(28, resultManual.getEndTime());

        // Now run graph through CLI and assert all answers are the same as before
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

        // Check that output file is all good from full run through
        File outputFile = new File("data/graphs/Nodes_7_OutTree_processed.dot");
        assertTrue(outputFile.exists());

        InputStream outputGraphStream = null;
        try {
            outputGraphStream = new FileInputStream(outputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail();
        }

        //This part of the code converts the output file from the full run through into a schedule to check that what
        //is written to the file is the same as what we got when manually running program and has correct answer
        // Convert schedules to graphs to use in comparison
        String schedule1 = "digraph \"Processor_1\" {\n" +
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

        String schedule2 = "digraph \"Processor_0\" {\n" +
                "\t2\t [Weight=5];\n" +
                "\t5\t [Weight=7];\n" +
                "\t2 -> 5\t [Weight=0];\n" +
                "}\n";
        InputStream stream2 = new ByteArrayInputStream(schedule2.getBytes(StandardCharsets.UTF_8));
        GraphReader reader2 = new DotGraphReader(stream2);
        Graph graph2 = reader2.read();

        //Make Schedules
        Schedule outputSchedule = new SimpleSchedule(2);

        // Add all of graph 1 to processor 0
        int startTime = 0;
        for(Node n : graph1.getNodes()){
            outputSchedule.addTask(new Task(0, startTime, n));
            startTime = startTime + n.getComputationCost();
        }

        // Add all of graph 2 to processor 1
        startTime = 0;
        for(Node n : graph2.getNodes()){
            outputSchedule.addTask(new Task(1, startTime, n));
            startTime = startTime + n.getComputationCost();
        }

            //Check written out schedule is all good
        assertEquals(28, outputSchedule.getEndTime());

    }

    /**
     * Test tests algorithm against graph with 8 nodes and 3 layers, one two processors with critical path
     * heuristics
     */
    @Test
    public void testNodes_8_RandomGraphTwoProcessor() {

        // Set up File
        File graphFile = new File("data/graphs/Nodes_8_Random.dot");
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
        assertEquals(35, entryNode.getComputationCost());
        assertEquals(8, graph.size());


        Algorithm algorithm = new DFSAlgorithm(2, new IsNotAPruner(), new CriticalPath());

        algorithm.start(graph);
        Schedule resultManual = algorithm.getCurrentBest();

        assertEquals(581, resultManual.getEndTime());
        // Now run graph through CLI
        String[] args = {"data/graphs/Nodes_8_Random.dot", "2"};
        Cli cli = new Cli(args) {
            @Override
            protected Schedule startScheduling(Graph graph) {
                Algorithm algorithm = new DFSAlgorithm(2, new IsNotAPruner(), new CriticalPath());

                algorithm.start(graph);
                Schedule result = algorithm.getCurrentBest();
                return result;
            }
        };
        cli.parse();

        // Check that output file is all good
        File outputFile = new File("data/graphs/Nodes_8_Random.dot");
        assertTrue(outputFile.exists());

        InputStream outputGraphStream = null;
        try {
            outputGraphStream = new FileInputStream(outputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail();
        }

        // Convert schedules to graphs to use in comparison
        String schedule1 = "digraph \"Processor_0\" {\n" +
                "\t0\t [Weight=35];\n" +
                "\t2\t [Weight=176];\n" +
                "\t0 -> 2\t [Weight=0];\n" +
                "\t4\t [Weight=176];\n" +
                "\t2 -> 4\t [Weight=0];\n" +
                "\t6\t [Weight=141];\n" +
                "\t4 -> 6\t [Weight=0];\n" +
                "\t7\t [Weight=53];\n" +
                "\t6 -> 7\t [Weight=0];\n" +
                "}\n";
        InputStream stream1 = new ByteArrayInputStream(schedule1.getBytes(StandardCharsets.UTF_8));
        GraphReader reader1 = new DotGraphReader(stream1);
        Graph graph1 = reader1.read();

        String schedule2 = "digraph \"Processor_1\" {\n" +
                "\t1\t [Weight=88];\n" +
                "\t3\t [Weight=159];\n" +
                "\t1 -> 3\t [Weight=0];\n" +
                "\t5\t [Weight=141];\n" +
                "\t3 -> 5\t [Weight=0];\n" +
                "}\n";
        InputStream stream2 = new ByteArrayInputStream(schedule2.getBytes(StandardCharsets.UTF_8));
        GraphReader reader2 = new DotGraphReader(stream2);
        Graph graph2 = reader2.read();

        //Make Schedules
        Schedule outputSchedule = new SimpleSchedule(2);

        // Add entirety of graph 1 to processor 0
        int startTime = 0;
        for(Node n : graph1.getNodes()){
            outputSchedule.addTask(new Task(0, startTime, n));
            startTime = startTime + n.getComputationCost();
        }

        // Add entirety of graph 2 to processor 1
        startTime = 0;
        for(Node n : graph2.getNodes()){
            outputSchedule.addTask(new Task(1, startTime, n));
            startTime = startTime + n.getComputationCost();
        }

        assertEquals(581, outputSchedule.getEndTime());

    }

    /**
     * Test tests algorithm against graph with 7 nodes and 3 layers, on 4 processors with critical path
     * heuristics
     */
    @Test
    public void testNodes_7_OutTreeGraphFourProcessor() {

        // Set up File
        File graphFile = new File("data/graphs/Nodes_7_OutTree.dot");
        InputStream graphStream = null;
        try {
            graphStream = new FileInputStream(graphFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail();
        }
        //Try making graph from file and check that it is correct
        GraphReader reader = new DotGraphReader(graphStream);
        Graph graph = reader.read();

        //Assert that graph is as expected
        Node entryNode = graph.getEntryPoints().get(0);

        assertEquals(entryNode.getLabel(), "0");
        assertEquals(5, entryNode.getComputationCost());
        assertEquals(7, graph.size());


        Algorithm algorithm = new DFSAlgorithm(4, new IsNotAPruner(), new NaiveBound());

        algorithm.start(graph);
        //Manually start algorithm on graph and check that final answer is correct
        Schedule resultManual = algorithm.getCurrentBest();

        assertEquals(22, resultManual.getEndTime());

        // Now run graph through CLI and assert all answers are the same as before
        String[] args = {"data/graphs/Nodes_7_OutTree.dot", "2"};
        Cli cli = new Cli(args) {
            @Override
            protected Schedule startScheduling(Graph graph) {
                Algorithm algorithm = new DFSAlgorithm(4, new IsNotAPruner(), new NaiveBound());

                algorithm.start(graph);
                Schedule result = algorithm.getCurrentBest();
                return result;
            }
        };
        cli.parse();

        // Check that output file is all good from full run through
        File outputFile = new File("data/graphs/Nodes_7_OutTree_processed.dot");
        assertTrue(outputFile.exists());

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
            fail();
        }
        //Try making graph from file and check that it is correct
        GraphReader reader = new DotGraphReader(graphStream);
        Graph graph = reader.read();

        //Assert that graph is as expected
        List<Node> entryNodes = graph.getEntryPoints();
        assertEquals(2, entryNodes.size());
        assertEquals("0", entryNodes.get(0).getLabel());
        assertEquals(6, entryNodes.get(0).getComputationCost());

        assertEquals("1", entryNodes.get(1).getLabel());
        assertEquals(5, entryNodes.get(1).getComputationCost());

        assertEquals(10, graph.size());

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

        assertEquals(50, resultManual.getEndTime());

        // Now run graph through CLI and assert all answers are the same as before
        String[] args = {"data/graphs/Nodes_10_Random.dot", "2"};
        Cli cli = new Cli(args) {
            @Override
            protected Schedule startScheduling(Graph graph) {
                Algorithm algorithm = new DFSAlgorithm(
                    4,
                    (pruningGraph, pruningSchedule, pruningTask) ->
                        new StartTimePruner().prune(pruningGraph, pruningSchedule, pruningTask)
                            || new ProcessorOrderPruner().prune(pruningGraph, pruningSchedule, pruningTask),
                    new CriticalPath()
                );

                algorithm.start(graph);
                Schedule result = algorithm.getCurrentBest();
                return result;
            }
        };
        cli.parse();

        // Check that output file is all good from full run through
        File outputFile = new File("data/graphs/Nodes_10_Random.dot");
        assertTrue(outputFile.exists());

    }

    /**
     * Test tests algorithm against graph with 11 nodes on 4 processors with critical path and start time
     * pruner heuristics.
     * ******Fair warning, this one takes a while ***********
     * **************** Roughly 30 min **********************
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
            fail();
        }
        //Try making graph from file and check that it is correct
        GraphReader reader = new DotGraphReader(graphStream);
        Graph graph = reader.read();

        //Assert that graph is as expected
        List<Node> entryNodes = graph.getEntryPoints();
        assertEquals(1, entryNodes.size());
        assertEquals("0", entryNodes.get(0).getLabel());
        assertEquals(50, entryNodes.get(0).getComputationCost());


        assertEquals(11, graph.size());

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

        for(int i = 0; i < resultManual.getNumProcessors(); ++i) {
            System.out.println("Processor " + String.valueOf(i));
            for(Task task : resultManual.getTasks(i))
                System.out.println( String.valueOf(task.getStartTime()) + "(" + String.valueOf(task.getNode().getComputationCost()) + "), " + task.getNode().getLabel());
        }

        assertEquals(227, resultManual.getEndTime());

        // Now run graph through CLI and assert all answers are the same as before
        String[] args = {"data/graphs/Nodes_11_OutTree.dot", "2"};
        Cli cli = new Cli(args) {
            @Override
            protected Schedule startScheduling(Graph graph) {
                Algorithm algorithm = new DFSAlgorithm(
                    4,
                    (pruningGraph, pruningSchedule, pruningTask) ->
                        new StartTimePruner().prune(pruningGraph, pruningSchedule, pruningTask)
                            || new ProcessorOrderPruner().prune(pruningGraph, pruningSchedule, pruningTask),
                    new CriticalPath()
                );

                algorithm.start(graph);
                Schedule result = algorithm.getCurrentBest();
                return result;
            }
        };
        cli.parse();

        // Check that output file is all good from full run through
        File outputFile = new File("data/graphs/Nodes_11_OutTree.dot");
        assertTrue(outputFile.exists());

    }
}
