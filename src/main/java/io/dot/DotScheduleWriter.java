package io.dot;

import common.Schedule;
import io.ScheduleWriter;

import java.io.OutputStream;

/**
 * This class writes a schedule to a .dot file format.
 */
public class DotScheduleWriter extends ScheduleWriter {
    /**
     * Constructor for ScheduleWriter
     * @param os The output stream to write to.
     */
    protected DotScheduleWriter(OutputStream os) {
        super(os);
    }

    // TODO Implement method
    @Override
    public void write(Schedule schedule) {}
}
