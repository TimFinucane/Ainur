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

@Ignore
public class GraphVisualiserTest extends Application {
    /* MACROS */
    public static final String GRAPH_FILE = "data/graphs/Nodes_11_OutTree.dot";

    @Override
    public void start(Stage primaryStage) {
        Graph graph = this.loadGraph(GRAPH_FILE);
        GraphVisualiser gv = new GraphVisualiser(graph);
        Scene scene = new Scene(gv);
        primaryStage.setScene(scene);
        primaryStage.show();

        this.testUpdate(graph.findByLabel("0"), gv);

        this.testUpdate(graph.findByLabel("1"), gv);
    }

    @Test
    public void testUpdate(Node nodeToUpdate, GraphVisualiser gv) {
        gv.update(nodeToUpdate);
    }

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
