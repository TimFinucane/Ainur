package io.dot;

import common.schedule.Schedule;
import io.ScheduleWriter;

import java.io.OutputStream;

/**
 * This class writes a schedule to a .dot file format.
 */
public class DotScheduleWriter extends ScheduleWriter {

    private final String NODE_DEPENDENCY_FORMAT = "%d -> %d\t [Weight=%d];";
    private final String NODE_SINGLE_FORMAT = "%d\t [Weight=%d];";
    private final String OUTPUT_FILE_NAME = "output.dot";
    
    /**
     * Constructor for ScheduleWriter
     * @param os The output stream to write to.
     */
    public DotScheduleWriter(OutputStream os) {
        super(os);
    }

    // TODO Implement method
    @Override
    public void write(Schedule schedule) {}
}
