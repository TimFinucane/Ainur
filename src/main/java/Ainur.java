import algorithm.Algorithm;
import algorithm.DFSAlgorithm;
import algorithm.TieredAlgorithm;
import algorithm.heuristics.lowerbound.CriticalPath;
import algorithm.heuristics.pruner.Arborist;
import algorithm.heuristics.pruner.ProcessorOrderPruner;
import algorithm.heuristics.pruner.StartTimePruner;
import cli.Cli;
import common.graph.Graph;
import common.schedule.Schedule;
import io.GraphReader;
import io.ScheduleWriter;
import io.dot.DotGraphReader;
import io.dot.DotScheduleWriter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.stage.Stage;
import visualisation.AinurVisualiser;

import java.io.*;
import java.util.function.Function;

/** The Main Class for Ainur **/
public class Ainur extends Application {
    private static Cli cli;
    private static Graph graph;
    private static Algorithm algorithm;
    private static AinurVisualiser av;

    /** MAIN **/
    public static void main(String[] args) {
      cli = new Cli(args);
      cli.parse();

      try {
          graph = readGraphFile(cli.getInputFile()); // read the graph
          algorithm = chooseAlgorithm(cli.getCores()); // choose an algorithm
          // TODO update upperbound when non optimal implemented
          av = new AinurVisualiser(algorithm, graph, 0, 100, cli.getProcessors());

          Thread schedulingThread =
                  new Thread(() -> runAlgorithm(graph, algorithm, cli.getProcessors(), Ainur::onAlgorithmComplete));

          if (cli.getVisualise()) {
              // Launch as javafx application.
              schedulingThread.start();
              launch(args);
          } else {
              schedulingThread.run();
              System.exit(0);
          }
      } catch (IOException io) {
          System.out.println("Invalid filename entered, try run it again with a valid filename."
                  + " Process terminated prematurely.");
          System.exit(1);
      }
    }

    public static Void onAlgorithmComplete(Schedule schedule) {
        if (cli.getVisualise() || av != null)
            Platform.runLater(() -> av.stop());
        try {
            writeSchedule(graph, schedule, cli.getInputFile(), cli.getOutputFile());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    /**
     * Reads a graph from the input dot file.
     *
     * @return A graph object created from the inputted dot file.
     * @throws FileNotFoundException if the .dot file could not be found
     */
    private static Graph readGraphFile(String inputFile) throws FileNotFoundException {
        InputStream is = new FileInputStream(inputFile);
        GraphReader graphReader = new DotGraphReader(is);
        return graphReader.read();
    }

    /**
     * Decides which algorithm should be used based on number of cores.
     *
     * @param cores The number of cores to run the algorithm on.
     *
     * @return A DFS algorithm if 1 core
     *      Tiered algorithm otherwise
     */
    private static Algorithm chooseAlgorithm(int cores) {
        Algorithm algorithm;
        if(cores == 1) { // Single-threaded DFS algorithm
            algorithm = new DFSAlgorithm(
                    Arborist.combine(new StartTimePruner(), new ProcessorOrderPruner()),
                    new CriticalPath()
            );
        } else { // Multithreaded, Tiered DFS algorithm
            algorithm = new TieredAlgorithm(cores, (tier, communicator) ->
                    new DFSAlgorithm(communicator,
                            Arborist.combine(new StartTimePruner(), new ProcessorOrderPruner()),
                            new CriticalPath(),
                            tier == 0 ? 8 : Integer.MAX_VALUE // Depth is 8 for first tier, infinite for second tier
                    )
            );
        }

        return algorithm;
    }

    /**
     * Starts scheduling using a provided algorithm.
     *
     * @param graph The graph to find a schedule on
     * @param algorithm The algorithm to use to find the schedule
     * @param processors The number of processors to use
     *
     * @return The schedule found by the algorithm.
     */
    private static void runAlgorithm(Graph graph, Algorithm algorithm, int processors, Function<Schedule, Void> onFinished) {
        // Run the algorithm
        algorithm.run(graph, processors);

        Schedule schedule = algorithm.getCurrentBest();
        onFinished.apply(schedule);
    }


    /**
     * Writes the schedule obtained from the scheduling algorithm to a dot file.
     *
     * @param schedule the schedule to write to the .dot file.
     * @throws FileNotFoundException
     */
    private static void writeSchedule(Graph graph, Schedule schedule, String inputFile, String outputFile) throws IOException {

        // Create a new file if file does not already exist
        File file = new File(outputFile);
        file.createNewFile();
        OutputStream os = new FileOutputStream(file);

        // Write schedule to output file
        ScheduleWriter scheduleWriter = new DotScheduleWriter(os);
        scheduleWriter.write(schedule, graph, new FileInputStream(inputFile));

    }

    /**
     * Starts the javafx visualisation.
     * Takes over control from main.
     */
    @Override
    public void start(Stage primaryStage) {
        // Start the program
        Scene scene = new Scene(av);
        primaryStage.setScene(scene);
        primaryStage.show();

        av.run();
    }
}