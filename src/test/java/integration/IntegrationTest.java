package integration;

import common.graph.Graph;
import io.dot.DotGraphReader;
import javafx.util.Pair;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Tag("gandalf") // Gandalf tests may be slow, but they finish precisely when they mean to
public abstract class IntegrationTest {
    // These are protected to allow subclasses to add or modify them
    protected List<String>                        graphs;
    protected List<List<Pair<Integer, Integer>>>  optimalSchedules;

    /**
     * Integration tests that run on lots of graph inputs
     */
    public IntegrationTest() {
        graphs = Arrays.asList(
            Paths.get("data", "graphs", "Nodes_7_OutTree.dot").toString(),
            Paths.get("data", "graphs", "Nodes_8_Random.dot").toString(),
            Paths.get("data", "graphs", "Nodes_9_SeriesParallel.dot").toString(),
            Paths.get("data", "graphs", "Nodes_10_Random.dot").toString(),
            Paths.get("data", "graphs", "Nodes_11_OutTree.dot").toString()
        );
        optimalSchedules = Arrays.asList(
            Arrays.asList(new Pair<>(2,  28), new Pair<>(4,  22)), // 7 nodes
            Arrays.asList(new Pair<>(2, 581), new Pair<>(4, 581)), // 8 nodes
            Arrays.asList(new Pair<>(2,  55), new Pair<>(4,  55)), // 9 nodes
            Arrays.asList(new Pair<>(2,  50), new Pair<>(4,  50)), // 10 nodes
            Arrays.asList(new Pair<>(2, 350), new Pair<>(4, 227))  // 11 nodes
        );
    }

    /**
     * Integration tests that run on lots of graph inputs
     * @param firstN only run the first N graphs in the list
     */
    public IntegrationTest(int firstN) {
        this();

        graphs = graphs.subList(0, firstN);
        optimalSchedules = optimalSchedules.subList(0, firstN);

    }


    @TestFactory
    public List<DynamicTest> testOnGraphs() {
        ArrayList<DynamicTest> optimalTests = new ArrayList<>();

        for(int graphIdx = 0; graphIdx < graphs.size(); ++graphIdx) {
            String graphName = graphs.get(graphIdx);
            for(Pair<Integer, Integer> processorOptimalTime : optimalSchedules.get(graphIdx)) {
                optimalTests.add(DynamicTest.dynamicTest(
                    generateName(graphName, processorOptimalTime),
                    () -> runAgainstOptimal(
                        graphName,
                        processorOptimalTime.getKey(),
                        processorOptimalTime.getValue()
                    )
                ));
            }
        }
        return optimalTests;
    }

    protected abstract void runAgainstOptimal(String graph, int processors, int optimalScheduleLength);

    /**
     * Reads the graph from the given graph file name. Fails if graph does not exist.
     */
    protected Graph readGraph(String graphName) {
        // Try to read the file and run the test
        try {
            DotGraphReader graphReader = new DotGraphReader(new FileInputStream(new File(graphName)));
            return graphReader.read();
        } catch (FileNotFoundException e) {
            // Abort the test, rather than failing, as an assumption (that the graph is loadable) failed.
            Assumptions.assumeTrue(false, "File not found: " + graphName);
            return null;
        }
    }

    private String generateName(String graph, Pair<Integer, Integer> processorOptimalTime) {
        return graph.substring(graph.lastIndexOf(File.separatorChar) + 1, graph.lastIndexOf('.')) // Just the file name part
            + ", "
            + processorOptimalTime.getKey() + " Processors";
    }
}
