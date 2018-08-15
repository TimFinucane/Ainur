package visualisation;

import algorithm.Algorithm;
import algorithm.DFSAlgorithm;
import algorithm.TieredAlgorithm;
import algorithm.heuristics.lowerbound.CriticalPath;
import algorithm.heuristics.lowerbound.NaiveBound;
import algorithm.heuristics.pruner.Arborist;
import algorithm.heuristics.pruner.IsNotAPruner;
import algorithm.heuristics.pruner.StartTimePruner;
import common.graph.Graph;
import io.dot.DotGraphReader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Ignore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static junit.framework.TestCase.fail;

@Ignore
public class AinurVisualiserTest extends Application {

    // Graph file to load in
    public static final String GRAPH_FILE = "data/graphs/Nodes_11_OutTree.dot";

    private Graph _graph;

    /**
     * Displays visualiser
     */
    @Override
    public void start(Stage primaryStage) {
        _graph = this.loadGraph(GRAPH_FILE);

        // Test the dfs implementation on visualiser
        this.testDfs(primaryStage);

        // Test the tiered algorithm implementation on visualiser
        Stage secondStage = new Stage();
        //this.testTiered(secondStage);
    }

    /**
     * Test the visualiser on a dfs algorithm
     *
     * @param stage the stage to display the visualiser in
     */
    public void testDfs(Stage stage) {
        Algorithm dfsAlgorithm = new DFSAlgorithm(
                Arborist.combine(new StartTimePruner()),
                new CriticalPath()
        );
        AinurVisualiser av = new AinurVisualiser(dfsAlgorithm, _graph, 0, 100, 4);
        this.setScene(stage, av);
        this.runThreads(dfsAlgorithm, av);
    }

    /**
     * Test the visualiser on a tiered algorithm
     *
     * @param stage the stage to display the visualiser in
     */
    public void testTiered(Stage stage) {
        int cores = 4;
        Algorithm tieredAlgorithm = new TieredAlgorithm(cores, (tier, communicator) ->
                new DFSAlgorithm(communicator,
                        new IsNotAPruner(),
                        new NaiveBound(),
                        tier == 0 ? 8 : Integer.MAX_VALUE // Depth is 8 for first tier, infinite for second tier
                )
        );
        AinurVisualiser av = new AinurVisualiser(tieredAlgorithm, _graph, 0, 100, cores);
        this.setScene(stage, av);
        this.runThreads(tieredAlgorithm, av);
    }

    /**
     * Private helper method for running algorithm and visualisation threads.
     *
     * @param algorithm algorithm to run in algorithm thread
     * @param av visualiser to run in visualiser thread
     */
    private void runThreads(Algorithm algorithm, AinurVisualiser av) {
        Task visualiserTask = new Task<Void>() {
            @Override
            public Void call() throws InterruptedException {
                av.run();
                return null;
            }
        };

        Task algorithmTask = new Task<Void>() {
            @Override
            public Void call() {
                algorithm.run(_graph, 4);
                return null;
            }

            @Override
            protected void done() {
                super.done();
                Platform.runLater(() -> {
                    av.stop();
                });
            }
        };

        new Thread(visualiserTask).start();
        new Thread(algorithmTask).start();
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

    /**
     * Private helper method for creating new windows
     *
     * @param stage The stage to put the graph visualiser in.
     * @param av The graph visualiser to put in the stage.
     */
    private void setScene(Stage stage, AinurVisualiser av) {
        // Set up javafx scene
        Scene scene = new Scene(av);
        stage.setScene(scene);
        stage.show();
    }
}
