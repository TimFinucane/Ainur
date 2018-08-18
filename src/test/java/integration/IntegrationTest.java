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
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Tag("gandalf") // Gandalf tests may be slow, but they finish precisely when they mean to
public abstract class IntegrationTest {

    private static final String FORK_JOIN = "Fork_Join";
    private static final String FORK_NODE = "Fork_Node";
    private static final String TWENTY_ONE = "21";
    private static final String THIRTY = "30";
    private static final String[] exclusionList = { TWENTY_ONE, THIRTY };

    // These are protected to allow subclasses to add or modify them
    protected List<String>                        graphs;
    // Shows the number of processors and the end time of the schedule.
    protected List<Pair<Integer, Integer>>  optimalSchedules;

    /**
     * Integration tests that run on lots of graph inputs
     */
    public IntegrationTest() {
        graphs = new ArrayList<>();
        optimalSchedules = new ArrayList<>();


        // Get all files in data/SampleData/Input, override graphs for this to be the value
        File inputFolder = new File(String.valueOf(Paths.get("data", "SampleData", "Input")));

        // Loop through all files in input file folder
        for (int i = 0; i < inputFolder.list().length; i++) {
            String fileString = inputFolder.list()[i];

            // Decide whether to continue
            boolean cont = true;
            for (String exclusion : exclusionList) {
                if (fileString.contains(exclusion)) {
                    cont = false;
                    break;
                }
            }

            if (cont) {
                // Add graph name to list of testing graphs
                graphs.add(String.valueOf(Paths.get("data", "SampleData", "Input")) + File.separator + fileString);

                try {
                    // get 2 FIS's, one for each answer finding method
                    InputStream is1 = new FileInputStream(String.valueOf(Paths.get("data", "SampleData", "Output")) + File.separator + fileString);
                    InputStream is2 = new FileInputStream(String.valueOf(Paths.get("data", "SampleData", "Output")) + File.separator + fileString);

                    // Create new pair in optimalSchedules of the correct no. of processors and scheduleLength
                    // derived from output directory.
                    Pair<Integer, Integer> pair = new Pair<>(scheduleProcessors(is1), scheduleLength(is2));
                    optimalSchedules.add(pair);

                } catch (FileNotFoundException e) {
                    System.out.println("Couldn't find: " + String.valueOf(Paths.get("data", "SampleData", "Output")) + File.separator + fileString);
                }
            }

        }
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
        for (int graphIdx = 0; graphIdx < graphs.size(); ++graphIdx) {
            String graphName = graphs.get(graphIdx);
            Pair<Integer, Integer> processorOptimalTime = optimalSchedules.get(graphIdx);

            optimalTests.add(DynamicTest.dynamicTest(
                    generateName(graphName, processorOptimalTime),
                    () -> runAgainstOptimal(
                            graphName,
                            processorOptimalTime.getKey(),
                            processorOptimalTime.getValue()
                    )
            ));

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

    /**
     * Returns an integer value of the schedule length given an input stream of text in dot format. Attributes must be
     * specified in the form of [Processor=<>,Start=<>,Weight=<>]
     * @param is
     * @return
     */
    private int scheduleLength(InputStream is) {

        Scanner s = new Scanner(is).useDelimiter("\\A");
        String inputTextAsString = s.hasNext() ? s.next() : "";
        s.close();

        Pattern taskPattern = Pattern.compile("Weight=(\\d+),\\s*Start=(\\d+),\\s*Processor=(\\d+)");
        Matcher m = taskPattern.matcher(inputTextAsString);

        int maxTaskEndTime = 0;
        // loop through all nodes looking for latest start time
        while (m.find()) {
            // Start time + Weight
            int taskEndTime = Integer.parseInt(m.group(1)) + Integer.parseInt(m.group(2));
            maxTaskEndTime = taskEndTime > maxTaskEndTime ? taskEndTime : maxTaskEndTime;

        }

        return maxTaskEndTime;
    }


    /**
     * Returns the number of processors in a particular schedule.
     * @return
     */
    private int scheduleProcessors(InputStream is) {

        Scanner s = new Scanner(is).useDelimiter("\\A");
        String inputTextAsString = s.hasNext() ? s.next() : "";
        s.close();

        Pattern taskPattern = Pattern.compile("Weight=(\\d+),\\s*Start=(\\d+),\\s*Processor=(\\d+)");
        Matcher m = taskPattern.matcher(inputTextAsString);

        int maxProcessors = 0;
        while (m.find()) {
            int processorNo = Integer.parseInt(m.group(1));
            // As far as i can see processor count starts at 0, so add 1.
            maxProcessors = processorNo + 1 > maxProcessors ? processorNo + 1 : maxProcessors;

        }

        return maxProcessors;
    }


    private String generateName(String graph, Pair<Integer, Integer> processorOptimalTime) {
        return graph.substring(graph.lastIndexOf(File.separatorChar) + 1, graph.lastIndexOf('.')) // Just the file name part
            + ", "
            + processorOptimalTime.getKey() + " Processors";
    }
}
