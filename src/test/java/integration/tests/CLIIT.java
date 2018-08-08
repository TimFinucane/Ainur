package integration.tests;

import cli.Cli;
import cli.MilestoneTwoCli;
import common.categories.GandalfIntegrationTestsCategory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;

/**
 * Aside from the banging name, this class aims to test the CLI functionality by passing in arguments to the CLI from
 * this test class and waiting for output. There are not any strenuous tests here computation wise, as there are done
 * elsewhere. The goal is to ensure that implemented options do their job correctly, and that correct output files
 * are written out.
 */
@Category(GandalfIntegrationTestsCategory.class)
public class CLIIT {

    private static final String NODES_7_FILENAME = "data/graphs/Nodes_7_OutTree.dot";

    private static final File NODES_7_OUTPUT_FILE = new File("data/graphs/Nodes_7_OutTree-output.dot");
    private static final File NODES_8_OUTPUT_FILE = new File("data/graphs/Nodes_8_Random-output.dot");
    private static final File NODES_9_OUTPUT_FILE = new File("data/graphs/Nodes_9_SeriesParallel-output.dot");
    private static final File NODES_10_OUTPUT_FILE = new File("data/graphs/Nodes_10_Random-output.dot");
    private static final File NODES_11_OUTPUT_FILE = new File("data/graphs/Nodes_11_OutTree-output.dot");


    @Before
    public void setup() {
        cleanUp();
    }

    @AfterClass
    public static void cleanUp() {
        NODES_7_OUTPUT_FILE.delete();
        NODES_8_OUTPUT_FILE.delete();
        NODES_9_OUTPUT_FILE.delete();
        NODES_10_OUTPUT_FILE.delete();
        NODES_11_OUTPUT_FILE.delete();
    }

    @Test
    public void test() {

        Cli cli = new MilestoneTwoCli(new String[]{"", ""});

    }

}
