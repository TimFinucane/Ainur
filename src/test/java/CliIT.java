import common.Validator;
import common.graph.Graph;
import integration.GraphSet;
import integration.IntegrationTest;
import integration.ScheduleReading;
import io.GraphReader;
import io.dot.DotGraphReader;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Aside from the banging name, this class aims to test the CLI functionality by passing in arguments to the CLI from
 * this test class and waiting for output. There are not any strenuous tests here computation wise, as there are done
 * elsewhere. The goal is to ensure that implemented options do their job correctly, and that correct output files
 * are written out.
 *
 * This file needs to be in the default package so it can access the Ainur main class.
 */
@Tag("gandalf") // Gandalf tests may be slow, but they finish precisely when they mean to
@Tag("last-alliance") // The last alliance against buggy code
public class CliIT {
    private static final String CUSTOM_OUTPUT_NAME_NO_SUFFIX = "my_special_file";
    private static final String CUSTOM_OUTPUT_NAME_SUFFIX = "my_special_file.dot";

    private static final String NODES_7_FILENAME = Paths.get("data", "graphs", "Nodes_7_OutTree.dot").toString();
    private static final String NODES_7_OUTPUT_FILENAME = Paths.get("data", "graphs", "Nodes_7_OutTree-output.dot").toString();

    @AfterEach
    public void clear() {
        new File(NODES_7_OUTPUT_FILENAME).delete();
        new File(CUSTOM_OUTPUT_NAME_SUFFIX).delete();
        new File("Nodes_7_OutTree.dot").delete();
    }

    /**
     * This tests that the Nodes_7_OutTree-output.dot file is correctly handled by the CLI and is passed through the
     * program to make an output schedule. This output schedule is then read back in to ensure writing occurred correctly.
     */
    @Test
    public void test7Node() {
        // Parse Nodes_7_OutTree.dot through program
        Ainur.main(new String[]{ NODES_7_FILENAME, "4" });

        // Will be compared for validity
        String outputText = null;
        Graph inputGraph = null;

        // Read in necessary graph and output schedule
        try {
            File file = new File(NODES_7_OUTPUT_FILENAME);

            // Get output file in the form of a string
            Scanner scanner = new Scanner(file);
            outputText = scanner.useDelimiter("\\A").next();
            scanner.close();

            // Get input graph in the form of a graph
            GraphReader graphReader = new DotGraphReader(new FileInputStream(NODES_7_FILENAME));
            inputGraph = graphReader.read();

            file.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail("Could not find output file: " + NODES_7_FILENAME);
        }

        assertTrue(Validator.isValid(inputGraph, outputText)); // Ensure is valid
        new File(NODES_7_OUTPUT_FILENAME).delete();
    }

    /**
     * This tests that the Nodes_7_OutTree-output.dot file is correctly handled by the CLI and is passed through the
     * program to make an output schedule, with extra parameters -o <filename>. This output schedule is then read
     * back in to ensure writing occurred correctly.
     */
    @Test
    public void test7NodeWithOutputArgumentSuffix() {
        // Parse Nodes_7_OutTree.dot through program
        Ainur.main(new String[]{ NODES_7_FILENAME, "4", "-o", CUSTOM_OUTPUT_NAME_SUFFIX });

        // Will be compared for validity
        String outputText = null;
        Graph inputGraph = null;

        // Read in necessary graph and output schedule
        try {
            // Get output file in the form of a string
            File file = new File(CUSTOM_OUTPUT_NAME_SUFFIX);

            Scanner scanner = new Scanner(file);
            outputText = scanner.useDelimiter("\\A").next();
            scanner.close();

            // Get input graph in the form of a graph
            GraphReader graphReader = new DotGraphReader(new FileInputStream(NODES_7_FILENAME));
            inputGraph = graphReader.read();

            file.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail("Could not find output file: " + CUSTOM_OUTPUT_NAME_SUFFIX);
        }

        assertTrue(Validator.isValid(inputGraph, outputText)); // Ensure is valid
        new File(CUSTOM_OUTPUT_NAME_SUFFIX).delete();
    }

    /**
     * This tests that the Nodes_7_OutTree-output.dot file is correctly handled by the CLI and is passed through the
     * program to make an output schedule, with extra parameters -o <filename> minus a .dot suffix. This output
     * schedule is then read back in to ensure writing occurred correctly.
     */
    @Test
    public void test7NodeWithOutputArgumentNoSuffix() {

        // Parse Nodes_7_OutTree.dot through program
        Ainur.main(new String[]{ NODES_7_FILENAME, "4", "-o", CUSTOM_OUTPUT_NAME_NO_SUFFIX });

        // Will be compared for validity
        String outputText = null;
        Graph inputGraph = null;

        // Read in necessary graph and output schedule
        try {
            File file = new File(CUSTOM_OUTPUT_NAME_SUFFIX);

            // Get output file in the form of a string
            Scanner scanner = new Scanner(file);
            outputText = scanner.useDelimiter("\\A").next();
            scanner.close();

            // Get input graph in the form of a graph
            GraphReader graphReader = new DotGraphReader(new FileInputStream(NODES_7_FILENAME));
            inputGraph = graphReader.read();

            file.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail("Could not find output file: " + CUSTOM_OUTPUT_NAME_SUFFIX);
        }

        assertTrue(Validator.isValid(inputGraph, outputText)); // Ensure is valid
        new File(CUSTOM_OUTPUT_NAME_SUFFIX).delete();
    }

    protected void run(String graph, int processors, int optimalScheduleLength) {
        // Pre-prepare output file
        File file = new File("./temp.dot");
        try {
            Ainur.main(new String[]{graph, String.valueOf(processors), "-o",  "temp.dot"});

            assertEquals(optimalScheduleLength, ScheduleReading.lengthFromFile(new FileInputStream(file)));
        } catch(FileNotFoundException e) {
            System.out.println("Output schedule file wasnt found!");
            e.printStackTrace();
            fail();
        } finally {
            file.delete();
        }
    }

    @TestFactory
    List<DynamicTest> cliTests() {
        return new IntegrationTest(GraphSet.ALL_REASONABLE(), this::run).getList();
    }
}
