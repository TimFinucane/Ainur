package io;

import common.Graph;

import java.io.InputStream;

/**
 * An abstract class for reading graphs from a file.
 * Inheritors should decide how a graph is read and what file format is used.
 */
public abstract class GraphReader {
    protected InputStream _is;

    /**
     * Constructor for GraphReader.
     * @param is The InputStream to read from.
     */
    protected GraphReader(InputStream is) {
        _is = is;
    }

    /**
     * Reads a graph. This could be from the InputStream specified in the constructor or in a manner decided by
     * inheritors.
     * @return The graph object which was read.
     */
    public abstract Graph read();
}
