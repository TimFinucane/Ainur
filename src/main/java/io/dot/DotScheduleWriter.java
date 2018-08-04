package io.dot;

import common.schedule.Schedule;
import common.schedule.Task;
import io.ScheduleWriter;

import java.io.*;

/**
 * This class writes a schedule to a .dot file format.
 */
public class DotScheduleWriter extends ScheduleWriter {

    private final String COMMUNICATION_COST_FORMAT = "\t%s -> %s\t [Weight=%d];\n";
    private final String COMPUTATION_COST_FORMAT = "\t%s\t [Weight=%d];\n";
    private final String DOT_GRAPH_OPENING = "digraph \"Processor#%d\" {\n";
    private final String DOT_GRAPH_CLOSING = "}\n\n";

    /**
     * Constructor for ScheduleWriter
     * @param os The output stream to write to.
     */
    public DotScheduleWriter(OutputStream os) {
        super(os);
    }

    @Override
    /**
     * Method to write schedule out to .dot file. There is only one output file for a schedule,
     * but there can be multiple digraphs in the dot file ie. as many digraphs as there are
     * processors as each processors tasks goes in a separate digraph
     * @params schedule the schedule to be written out to file
     */
    public void write(Schedule schedule) {

        PrintWriter pw = new PrintWriter(_os);

        for(int processor = 0; processor < schedule.getNumProcessors(); ++processor){
            pw.write(String.format(DOT_GRAPH_OPENING, processor)); // Starting of a digraph

            Task prevTask = null;
            for(Task curTask : schedule.getTasks(processor)) { // Start to write tasks
                pw.write(String.format(
                    COMPUTATION_COST_FORMAT,
                    curTask.getNode().getLabel(),
                    curTask.getNode().getComputationCost())
                );

                if(prevTask != null) { // If there is a node before ie dependency, add communication cost
                    pw.write(String.format(
                        COMMUNICATION_COST_FORMAT,
                        prevTask.getNode().getLabel(),
                        curTask.getNode().getLabel(),
                        // Get the communication cost by finding the difference of that last tasks end time and
                        // the current tasks start time
                        (curTask.getStartTime() - prevTask.getEndTime())
                        )
                    );
                }

                prevTask = curTask;
            }
            pw.write(DOT_GRAPH_CLOSING);
        }
        pw.close();
    }
}
