package cli;

import algorithm.Algorithm;
import algorithm.DFSAlgorithm;
import algorithm.heuristics.*;
import common.graph.Graph;
import common.schedule.Schedule;
import io.GraphReader;
import io.ScheduleWriter;
import io.dot.DotGraphReader;
import io.dot.DotScheduleWriter;

import java.io.*;

/**
 * A class implementing the abstract Cli class.
 * This class is to be used as the Cli for milestone one.
 * Extra command line args will be added in the milestone two CLI.
 */
public class MilestoneOneCli extends Cli {

    /**
     * Constructor responsible for assigning the args to a private field and assigning defaults.
     *
     * @param args a string array of arguments obtained from the command line.
     */
    public MilestoneOneCli(String[] args) {
        super(args);
    }

    @Override
    protected Schedule startScheduling(Graph graph) {
        // Algorithm
        Arborist arborist = new IsNotAPruner();
        LowerBound lowerBound = new NaiveBound();
        Algorithm algorithm = new DFSAlgorithm(_processors, arborist, lowerBound);

        //Start
        algorithm.start(graph);

        return algorithm.getCurrentBest();
    }
}