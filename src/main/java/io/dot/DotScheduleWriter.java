package io.dot;

import common.schedule.Processor;
import common.schedule.Schedule;
import common.schedule.Task;
import io.ScheduleWriter;

import java.io.*;

/**
 * This class writes a schedule to a .dot file format.
 */
public class DotScheduleWriter extends ScheduleWriter {

    private final String COMMUNICATION_COST_FORMAT = "%d -> %d\t [Weight=%d];";
    private final String COMPUTATION_COST_FORMAT = "%d\t [Weight=%d];";
    private final String OUTPUT_FILE_NAME = "output.dot";
    private final String DOT_GRAPH_OPENING = "digraph %d {";

    /**
     * Constructor for ScheduleWriter
     * @param os The output stream to write to.
     */
    public DotScheduleWriter(OutputStream os) {
        super(os);
    }

    // TODO Implement method
    @Override
    /**
     * Method to write schedule out to .dot file. There is only one output file for a schedule,
     * but there can be multiple digraphs in the dot file ie. as many digraphs as there are
     * processors as each processors tasks goes in a separate digraph
     * @params schedule the schedule to be written out to file
     */
    public void write(Schedule schedule) {

        File output = new File(OUTPUT_FILE_NAME);
        PrintWriter pw = null;
        try{
            pw = new PrintWriter(output);
        } catch (FileNotFoundException e){
            System.out.println(e.getMessage());
        }

        int processorCount = 0;
        for(Processor processor : schedule.getProcessors()){
            pw.write(String.format(DOT_GRAPH_OPENING, processorCount)); // Starting of a digraph
            for (Task task : processor.getTasks()){
                pw.write(String.format(COMPUTATION_COST_FORMAT, task.getNode().getLabel()));
            }
        }


    }
}
