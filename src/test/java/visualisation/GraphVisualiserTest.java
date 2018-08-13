package visualisation;

import common.graph.Graph;
import common.graph.Node;
import io.dot.DotGraphReader;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static junit.framework.TestCase.fail;

/**
 * Class used to check the rendering of the GraphVisualiser class.
 */
@Ignore
public class GraphVisualiserTest extends Application {

    // Graph file to load in
    public static final String GRAPH_FILE = "data/graphs/Nodes_11_OutTree.dot";

    /**
     * Displays a graph visualisation from a static file input.
     */
    @Override
    public void start(Stage primaryStage) {
        // Create a graph visualizer from file.
        Graph graph = this.loadGraph(GRAPH_FILE);
        GraphVisualiser gv = new GraphVisualiser(graph);

        // Set up javafx scene
        Scene scene = new Scene(gv);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Test updating.
        // End result should have 1 highlighted and all other nodes should not be highlighted.
        this.testUpdate(graph.findByLabel("0"), gv);
        this.testUpdate(graph.findByLabel("1"), gv);
    }

    /**
     * Tests the update method of GraphVisualiser
     *
     * @param nodeToUpdate The node to highlight
     * @param gv The graphvisualiser to use
     */
    public void testUpdate(Node nodeToUpdate, GraphVisualiser gv) {
        gv.update(nodeToUpdate);
    }

    /**
     * Private helper method for loading in a Ainur graph from a file.
     *
     * @param pathname The filepath of the .dot file to read from.
     * @return The Ainur graph read from the .dot file
     */
    private Graph loadGraph(String pathname) {
        File graphFile = new File(pathname);
        InputStream is = null;
        try {
            is = new FileInputStream(graphFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail();
        }
        return new DotGraphReader(is).read();
    }
}
