package io;

import common.Schedule;

import java.io.OutputStream;

/**
 * An interface for writing schedules to a file.
 * Inheritors should decide how a schedule is written and what file format is used.
 */
public abstract class ScheduleWriter {
    protected OutputStream _os;

    /**
     * Constructor for ScheduleWriter.
     * @param os The output stream to write to.
     */
    protected ScheduleWriter(OutputStream os) {
        _os = os;
    }

    /**
     * Writes a schedule. This could either be to the OutputStream specified in the constructor or in a manner decided
     * by inheritors.
     * @param schedule The schedule to write.
     */
    public abstract void write(Schedule schedule);
}
