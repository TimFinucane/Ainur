package io;

import common.Schedule;

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

    /**
     * Writes a schedule to a .dot file.
     * @param schedule The schedule to write.
     */
    // TODO Implement method
    @Override
    public void write(Schedule schedule) {}
}
