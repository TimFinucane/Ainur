package integration;

import common.graph.Graph;
import io.dot.DotGraphReader;
import javafx.util.Pair;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Tag("gandalf") // Gandalf tests may be slow, but they finish precisely when they mean to
public abstract class IntegrationTest {
    // These are protected to allow subclasses to add or modify them
    protected List<String>                        graphs;
    // Shows the number of processors and the end time of the schedule.
    protected List<Pair<Integer, Integer>>  optimalSchedules;

    /**
     * Integration tests that run on lots of graph inputs
     */
    public IntegrationTest() {

        List<String> tempGraphs = new ArrayList<>();
        // Get all files in data/SampleData/Input, override graphs for this to be the value
        File inputFolder = new File(String.valueOf(Paths.get("data", "SampleData", "Input")));
        for (String fileString : inputFolder.list()) {
            if (!fileString.contains("Fork_Node") && !fileString.contains("21") && !fileString.contains("30"))
                tempGraphs.add(fileString);
        }

        graphs = tempGraphs;

        for (int i = 0; i < graphs.size(); i++)
            graphs.set(i, String.valueOf(Paths.get("data", "SampleData", "Input")) + File.separator + graphs.get(i));

        // Make a list of lists with one pair in each corresponding to the number of processors and
        // optimal solution for that graph.
        File outputFolder = new File(String.valueOf(Paths.get("data", "SampleData", "Output")));
        List<String> outputFiles = new ArrayList<>();
        for (String fileString : outputFolder.list()) {
            //TODO take out this IF statement - these are the hardest graphs!! (Note we only need to run on ones up to 20).
            // This removes all fork node graphs and any graphs with either 21 or 30 nodes so "quick" tests can run.
            if (!fileString.contains("Fork_Node") && !fileString.contains("21") && !fileString.contains("30")) {
                outputFiles.add(fileString);
            }
        }
        List<Pair<Integer, Integer>> optimalSchedulesReplacement = new ArrayList<>();

        for (String outputFileNameString : outputFiles) {

            InputStream is1 = null;
            InputStream is2 = null;
            try {
                is1 = new FileInputStream(String.valueOf(Paths.get("data", "SampleData", "Output")) + File.separator + outputFileNameString);
                is2 = new FileInputStream(String.valueOf(Paths.get("data", "SampleData", "Output")) + File.separator + outputFileNameString);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            int graphProcessorNo = scheduleProcessors(is1);
            int graphOptimalSolution = scheduleLength(is2);

            Pair<Integer, Integer> pair = new Pair<>(graphProcessorNo, graphOptimalSolution);
            optimalSchedulesReplacement.add(pair);
        }

        optimalSchedules = optimalSchedulesReplacement;
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

        Pattern taskPattern = Pattern.compile("(?<=;|^|\\{)\\s*(\\w+)\\s*\\[\\s*Processor=(\\d+),\\s*Start=(\\d+),\\s*Weight=(\\d+)\\s*\\]");
        Matcher m = taskPattern.matcher(inputTextAsString);

        int maxTaskEndTime = 0;
        // loop through all nodes looking for latest start time
        while (m.find()) {
            // Start time + Weight
            int taskEndTime = Integer.parseInt(m.group(3)) + Integer.parseInt(m.group(4));
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

        Pattern taskPattern = Pattern.compile("(?<=;|^|\\{)\\s*(\\w+)\\s*\\[\\s*Processor=(\\d+),\\s*Start=(\\d+),\\s*Weight=(\\d+)\\s*\\]");
        Matcher m = taskPattern.matcher(inputTextAsString);

        int maxProcessors = 0;
        while (m.find()) {
            int processorNo = Integer.parseInt(m.group(2));
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
