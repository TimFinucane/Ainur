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

public class Ainur extends Application {
    private static String[] ARGUMENTS;

    public static void main(String[] args) {
      ARGUMENTS = args;
      Cli cli = new Cli(ARGUMENTS);
      cli.parse();

      try {
          if (cli.getVisualise()) {
              launch(args);
          } else {
              // Start the program
              Graph graph = readGraphFile(cli.getInputFile()); // read the graph
              Algorithm algorithm = chooseAlgorithm(cli.getCores()); // choose an algorithm
              Schedule schedule = startScheduling(graph, algorithm, cli.getProcessors()); // start scheduling
              // write the output
              writeSchedule(graph, schedule, cli.getInputFile(), cli.getOutputFile());
          }

      } catch (IOException io) {
          System.out.println("Invalid filename entered, try run it again with a valid filename."
                  + " Process terminated prematurely.");
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

    private static void visualisationScheduling(Graph graph, Algorithm algorithm, AinurVisualiser av, int processors,
                                                String input, String output) {
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
                startScheduling(graph, algorithm, processors);
                return null;
            }

            @Override
            protected void done() {
                super.done();
                Platform.runLater(() -> av.stop());
                Schedule schedule = algorithm.getCurrentBest();
                try {
                    writeSchedule(graph, schedule, input, output);
                } catch (IOException e) {
                    System.out.println("Invalid filename entered, try run it again with a valid filename."
                            + " Process terminated prematurely.");
                }
            }
        };

        new Thread(visualiserTask).start();
        new Thread(algorithmTask).start();
    }

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

    private static Schedule startScheduling(Graph graph, Algorithm algorithm, int processors) {
        // Run the algorithm
        algorithm.run(graph, processors);

        return algorithm.getCurrentBest();
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

    @Override
    public void start(Stage primaryStage) throws Exception {
        Cli cli = new Cli(ARGUMENTS);
        cli.parse();
        // Start the program
        Graph graph = readGraphFile(cli.getInputFile()); // read the graph
        Algorithm algorithm = chooseAlgorithm(cli.getCores()); // choose an algorithm
        AinurVisualiser av = new AinurVisualiser(algorithm, graph, 0, 100, cli.getProcessors());

        Scene scene = new Scene(av);
        primaryStage.setScene(scene);
        primaryStage.show();

        visualisationScheduling(graph, algorithm, av, cli.getProcessors(), cli.getInputFile(), cli.getOutputFile());
    }
}