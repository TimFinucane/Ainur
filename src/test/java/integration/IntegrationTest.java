package integration;

import common.graph.Graph;
import io.dot.DotGraphReader;
import javafx.util.Pair;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class IntegrationTest {
    public interface Testable {
        void runAgainstOptimal(String graph, int processors, int optimalScheduleLength);
    }

    private List<DynamicTest> _dynamicTests;

    /**
     * Generates tests for the given testable
     * @param set The set of graphs to supply
     * @param testable The method to test on
     * @return A list of dynamic tests that should be returned by a TestFactory
     */
    public IntegrationTest(GraphSet set, Testable testable) {
        _dynamicTests = new ArrayList<>();

        for (int graphIdx = 0; graphIdx < set.graphs.size(); ++graphIdx) {
            String graphName = set.graphs.get(graphIdx);
            for(Pair<Integer, Integer> processorOptimalTime : set.optimalScheduleLengths.get(graphIdx)) {
                _dynamicTests.add(DynamicTest.dynamicTest(
                    generateName(graphName, processorOptimalTime),
                    () -> testable.runAgainstOptimal(
                        graphName,
                        processorOptimalTime.getKey(),
                        processorOptimalTime.getValue()
                    )
                ));
            }
        }
    }

    public List<DynamicTest> getList() {
        return _dynamicTests;
    }

    /**
     * Joins multiple test sets, together with a name for each test set
     */
    @SafeVarargs
    public static List<DynamicContainer> join(Pair<String, IntegrationTest>... tests) {
        List<DynamicContainer> containers = new ArrayList<>();

        for(Pair<String, IntegrationTest> testSet : tests) {
            containers.add(DynamicContainer.dynamicContainer(testSet.getKey(), testSet.getValue().getList()));
        }

        return containers;
    }

    /**
     * Reads the graph from the given graph file name. Fails if graph does not exist.
     */
    public static Graph readGraph(String graphName) {
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
