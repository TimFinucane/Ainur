import algorithm.*;
import algorithm.heuristics.DefaultHeuristics;
import algorithm.heuristics.lowerbound.CriticalPath;
import algorithm.heuristics.lowerbound.LowerBound;
import algorithm.heuristics.pruner.Arborist;
import algorithm.heuristics.pruner.BetterStartPruner;
import algorithm.heuristics.pruner.ProcessorOrderPruner;
import algorithm.heuristics.pruner.StartTimePruner;
import cli.Cli;
import common.Config;
import common.graph.Graph;
import common.schedule.Schedule;
import io.GraphReader;
import io.ScheduleWriter;
import io.dot.DotGraphReader;
import io.dot.DotScheduleWriter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import visualisation.VisualiserWindow;

import java.io.*;
import java.util.function.Consumer;

/** The Main Class for Ainur **/
public class Ainur extends Application {

    /* Static Fields */
    private static Cli cli;
    private static Graph graph;
    private static Algorithm algorithm;
    private static VisualiserWindow window;

    /** MAIN **/

    public static void main(String[] args) {
      cli = new Cli(args);
      cli.parse();

      try {
          graph = readGraphFile(cli.getInputFile()); // read the graph
          algorithm = chooseAlgorithm(cli.getCores()); // choose an algorithm
          // TODO update upperbound when non optimal implemented

          Thread schedulingTask = new Thread(() -> runAlgorithm(graph, algorithm, cli.getProcessors(), Ainur::onAlgorithmComplete));

          if (cli.getVisualise()) {
              // Start the scheduling task in another thread, and begin javafx launch on the main thread
              window = new VisualiserWindow(algorithm, graph, cli.getProcessors());
              schedulingTask.start();

              Application.launch(args);
          } else {
              // Run the scheduling task in this thread
              schedulingTask.run();
          }
      } catch (IOException io) {
          System.out.println("Invalid filename entered, try run it again with a valid filename."
                  + " Process terminated prematurely.");
          System.exit(1);
      }
    }

    /* Functions */

    private static void onAlgorithmComplete(Schedule schedule) {
        if (cli.getVisualise())
            Platform.runLater(() -> window.stop());
        try {
            writeSchedule(graph, schedule, cli.getInputFile(), cli.getOutputFile());
            if (!cli.getVisualise())
                System.exit(0);
        } catch (IOException e) {
            System.out.println("Failed to write the outputted schedule to a file!");
            System.exit(1);
        }
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
        GreedyAlgorithm greedy = new GreedyAlgorithm();
        greedy.run(graph, cli.getProcessors());

        Algorithm algorithm;
        if(cores == 1) { // Single-threaded DFS algorithm
            algorithm = new DFSAlgorithm(
                Arborist.combine(new StartTimePruner(), new ProcessorOrderPruner(), new BetterStartPruner()),
                new CriticalPath(),
                greedy.getCurrentBest()
            );
        } else { // Multithreaded, Tiered AStar/DFS algorithm
            Arborist arborist = DefaultHeuristics.arborist();
            LowerBound lowerBound = DefaultHeuristics.lowerBound();

            return new TieredAlgorithm(cores,
                (tier, communicator) -> {
                    if(tier == 0) // Expand to a few states for the purposes of running A stars in parallel
                        return new DFSAlgorithm(communicator, arborist, lowerBound, Math.min(4, graph.size()));
                    else if(tier < (graph.size() / 2 + 1)) // Run A stars in parallel on the system
                        return new AStarAlgorithm(communicator, arborist, lowerBound);
                    else
                        return new DFSAlgorithm(communicator, arborist, lowerBound, Integer.MAX_VALUE);
                },
                greedy.getCurrentBest());
        }

        return algorithm;
    }

    /**
     * Starts scheduling using a provided algorithm.
     *
     * @param graph The graph to find a schedule on
     * @param algorithm The algorithm to use to find the schedule
     * @param processors The number of processors to use
     * @param onFinished Calback to be called on completion of algorithm
     */
    private static void runAlgorithm(Graph graph, Algorithm algorithm, int processors, Consumer<Schedule> onFinished) {
        // Run the algorithm
        algorithm.run(graph, processors);

        Schedule schedule = algorithm.getCurrentBest();
        onFinished.accept(schedule);
    }


    /**
     * Writes the schedule obtained from the scheduling algorithm to a dot file.
     *
     * @param schedule the schedule to write to the .dot file.
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

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(Config.APP_NAME);
        window.visualise(primaryStage);
    }
}