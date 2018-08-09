package integration.tests;

import cli.Cli;
import cli.MilestoneTwoCli;
import common.Validator;
import common.categories.GandalfIntegrationTestsCategory;
import common.graph.Graph;
import io.GraphReader;
import io.dot.DotGraphReader;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Aside from the banging name, this class aims to test the CLI functionality by passing in arguments to the CLI from
 * this test class and waiting for output. There are not any strenuous tests here computation wise, as there are done
 * elsewhere. The goal is to ensure that implemented options do their job correctly, and that correct output files
 * are written out.
 */
@Category(GandalfIntegrationTestsCategory.class)
public class CliIT {

    private static final String DATA_PATH_NAME = "data/graphs/";

    private static final String CUSTOM_OUTPUT_NAME_NO_SUFFIX = "my_special_file";
    private static final String CUSTOM_OUTPUT_NAME_SUFFIX = "my_special_file.dot";

    private static final String NODES_7_FILENAME = "data/graphs/Nodes_7_OutTree.dot";
    private static final String NODES_7_OUTPUT_FILENAME = "data/graphs/Nodes_7_OutTree-output.dot";

    /**
     * This tests that the Nodes_7_OutTree-output.dot file is correctly handled by the CLI and is passed through the
     * program to make an output schedule. This output schedule is then read back in to ensure writing occurred correctly.
     */
    @Test
    public void test7Node() {

        // Parse Nodes_7_OutTree.dot through program
        Cli cli = new MilestoneTwoCli(new String[]{ NODES_7_FILENAME, "4" });
        cli.parse();

        // Will be compared for validity
        String outputText = null;
        Graph inputGraph = null;

        // Read in necessary graph and output schedule
        try {
            // Get output file in the form of a string
            Scanner scanner = new Scanner(NODES_7_OUTPUT_FILENAME);
            outputText = scanner.useDelimiter("\\A").next();
            scanner.close();

            // Get input graph in the form of a graph
            GraphReader graphReader = new DotGraphReader(new FileInputStream(NODES_7_FILENAME));
            inputGraph = graphReader.read();

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
        Cli cli = new MilestoneTwoCli(new String[]{ NODES_7_FILENAME, "4", "-o", CUSTOM_OUTPUT_NAME_SUFFIX });
        cli.parse();

        // Will be compared for validity
        String outputText = null;
        Graph inputGraph = null;

        // Read in necessary graph and output schedule
        try {
            // Get output file in the form of a string
            File file = new File(DATA_PATH_NAME + CUSTOM_OUTPUT_NAME_SUFFIX);

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
    }




    /**
     * This tests that the Nodes_7_OutTree-output.dot file is correctly handled by the CLI and is passed through the
     * program to make an output schedule, with extra parameters -o <filename> minus a .dot suffix. This output
     * schedule is then read back in to ensure writing occurred correctly.
     */
    @Test
    public void test7NodeWithOutputArgumentNoSuffix() {

        // Parse Nodes_7_OutTree.dot through program
        Cli cli = new MilestoneTwoCli(new String[]{ NODES_7_FILENAME, "4", "-o", CUSTOM_OUTPUT_NAME_NO_SUFFIX });
        cli.parse();

        // Will be compared for validity
        String outputText = null;
        Graph inputGraph = null;

        // Read in necessary graph and output schedule
        try {
            File file = new File(DATA_PATH_NAME + CUSTOM_OUTPUT_NAME_SUFFIX);

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
    }

}
