package visualisation;

import common.graph.Graph;
import io.dot.DotGraphReader;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Ignore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static junit.framework.TestCase.fail;

@Ignore
public class GraphVisualiserTest extends Application {
    /* MACROS */
    public static final String GRAPH_FILE = "data/graphs/Nodes_7_OutTree.dot";

    @Override
    public void start(Stage primaryStage) throws Exception {
        GraphVisualiser gv = new GraphVisualiser(this.loadGraph(GRAPH_FILE));
        Scene scene = new Scene(gv);
        primaryStage.setScene(scene);
        primaryStage.show();
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
