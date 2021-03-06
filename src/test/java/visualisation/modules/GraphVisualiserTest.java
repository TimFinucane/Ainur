package visualisation.modules;

import common.graph.Graph;
import common.graph.Node;
import io.dot.DotGraphReader;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Disabled;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.fail;

/**
 * Class used to check the rendering of the GraphVisualiser class.
 */
@Disabled
public class GraphVisualiserTest extends Application {

    // Graph file to load in
    public static final String GRAPH_FILE = "data/graphs/Nodes_11_OutTree.dot";

    private Graph _graph;

    /**
     * Displays several graph visualisations from a single static file input.
     */
    @Override
    public void start(Stage primaryStage) {
        _graph = this.loadGraph(GRAPH_FILE);

        // Test single update on primary stage
        GraphVisualiser gv = new GraphVisualiser(_graph);
        this.setScene(primaryStage, gv);

        // Create a new stage and test multi update on it
        Stage secondStage = new Stage();
        GraphVisualiser gv2 = new GraphVisualiser(_graph);
        this.setScene(secondStage, gv2);

        // Create a new stage and test multi and single update;
        Stage thirdStage = new Stage();
        // Graph should be rendered with low quality
        GraphVisualiser gv3 = new GraphVisualiser(_graph, false);
        this.setScene(thirdStage, gv3);

        // Test rendering quality set low
        Stage fourthStage = new Stage();
        GraphVisualiser gv4 = new GraphVisualiser(_graph);
        this.setScene(fourthStage, gv4);
        this.testLowQualityGraph(gv4);

        // Test rendering quality set low then high
        Stage fifthStage = new Stage();
        GraphVisualiser gv5 = new GraphVisualiser(_graph);
        this.setScene(fifthStage, gv5);
        this.testLowHighQualityGraph(gv5);
    }

    /**
     * Tests the graphvisualisers render quality method succesully switches to low quality
     *
     * @param gv graphvisualiser to test on.
     */
    public void testLowQualityGraph(GraphVisualiser gv) {
        // Should set to low quality rendering
        gv.setHighRenderQuality(false);
    }

    /**
     *Tests the graphvisualisers render quality method successfully switches to low then high
     *
     * @param gv graphvisualiser to test on.
     */
    public void testLowHighQualityGraph(GraphVisualiser gv) {
        // Should set to low quality rendering
        gv.setHighRenderQuality(false);

        // Set the quality to high again
        gv.setHighRenderQuality(true);
    }

    /**
     * Private helper method for creating new windows
     *
     * @param stage The stage to put the graph visualiser in.
     * @param gv The graph visualiser to put in the stage.
     */
    private void setScene(Stage stage, GraphVisualiser gv) {
        // Set up javafx scene
        Scene scene = new Scene(gv);
        stage.setScene(scene);
        stage.show();
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
