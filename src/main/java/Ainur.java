import algorithm.Algorithm;
import algorithm.DFSAlgorithm;
import algorithm.TieredAlgorithm;
import algorithm.heuristics.lowerbound.CriticalPath;
import algorithm.heuristics.pruner.ProcessorOrderPruner;
import algorithm.heuristics.pruner.StartTimePruner;
import cli.Cli;
import common.graph.Graph;
import common.schedule.Schedule;
import io.GraphReader;
import io.ScheduleWriter;
import io.dot.DotGraphReader;
import io.dot.DotScheduleWriter;

import java.io.*;

public class Ainur {
    public static void main(String[] args) {
      Cli cli = new Cli(args);
      cli.parse();

      try {

          // Start the program
          Graph graph = readGraphFile(cli.getInputFile()); // read the graph
          Schedule schedule = startScheduling(graph, cli.getProcessors(), cli.getCores()); // start scheduling

          // write the output
          writeSchedule(graph, schedule, cli.getInputFile(), cli.getOutputFile());

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



    private static Schedule startScheduling(Graph graph, int processors, int cores) {
        Algorithm algorithm;
        if(cores == 1) { // Single-threaded DFS algorithm
            algorithm = new DFSAlgorithm(
                (pruningGraph, pruningSchedule, pruningTask) ->
                    new StartTimePruner().prune(pruningGraph, pruningSchedule, pruningTask)
                        || new ProcessorOrderPruner().prune(pruningGraph, pruningSchedule, pruningTask),
                new CriticalPath()
            );
        } else { // Multithreaded, Tiered DFS algorithm
            algorithm = new TieredAlgorithm(cores, (tier, communicator) ->
                new DFSAlgorithm(communicator,
                    (pruningGraph, pruningSchedule, pruningTask) ->
                        new StartTimePruner().prune(pruningGraph, pruningSchedule, pruningTask)
                            || new ProcessorOrderPruner().prune(pruningGraph, pruningSchedule, pruningTask),
                    new CriticalPath(),
                    tier == 0 ? 8 : Integer.MAX_VALUE // Depth is 8 for first tier, infinite for second tier
                )
            );
        }

        //Start
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
}