package cli;

import algorithm.Algorithm;
import algorithm.DFSAlgorithm;
import algorithm.heuristics.lowerbound.CriticalPath;
import algorithm.heuristics.pruner.ProcessorOrderPruner;
import algorithm.heuristics.pruner.StartTimePruner;
import common.Config;
import common.graph.Graph;
import common.schedule.Schedule;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * A class implementing the Abstract Cli class.
 * This class is to be used as the CLI for milestone 2.
 * It includes extra functionality including multiprocessing and visualisations.
 */
public class MilestoneTwoCli extends Cli {
    // Fields required for milestone two.
    protected boolean _visualise;
    protected int _cores;

    /**
     * Constructor responsible for assigning the args to a private field and assigning defaults.
     * Adds default args to the visualise and cores options.
     *
     * @param args a string array of arguments obtained from the command line.
     */
    public MilestoneTwoCli(String[] args) {
        super(args);
        _visualise = Config.VISUALISE_DEFAULT;
        _cores = Config.CORES_DEFAULT;
    }

    @Override
    protected Schedule startScheduling(Graph graph) {
        // Algorithm
        Algorithm algorithm = new DFSAlgorithm(
                _processors,
                (pruningGraph, pruningSchedule, pruningTask) ->
                        new StartTimePruner().prune(pruningGraph, pruningSchedule, pruningTask)
                                || new ProcessorOrderPruner().prune(pruningGraph, pruningSchedule, pruningTask),
                new CriticalPath()
        );

        //Start
        algorithm.start(graph);

        return algorithm.getCurrentBest();
    }

    @Override
    protected Options appendOptions(Options options) {
        options.addOption(new Option("p", true, "use <arg> cores for execution in parallel "
                + "(default is sequential"));
        options.addOption(new Option("v", "visualise", false, "visualise the search"));
        return options;
    }

    @Override
    protected void interrogateArgs(CommandLine cmdLine) {
        if (cmdLine.hasOption("p")) {
            _cores = Integer.parseInt(cmdLine.getOptionValue("p"));
            System.out.println("You instructed Ainur to be executed using " + _cores + " cores");
            System.out.println("Unfortunately this feature is yet to be implemented... stay tuned");
        }
        if (cmdLine.hasOption("v")) {
            _visualise = true;
            System.out.println("You instructed Ainur to visualise the scheduling process!");
            System.out.println("Unfortunately this feature is yet to be implemented... stay tuned");
        }
    }
}
