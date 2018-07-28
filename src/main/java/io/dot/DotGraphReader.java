package io.dot;

import common.graph.Graph;
import io.GraphReader;

import java.io.InputStream;

/**
 * Takes an InputStream in .dot format and converts it to a Graph object.
 */
public class DotGraphReader extends GraphReader {
    /**
     * Constructor for GraphReader.
     * @param is The InputStream to read from.
     */
    protected DotGraphReader(InputStream is) {
        super(is);
    }

    // TODO Implement method
    @Override
    public Graph read() {
        return null;
    }
}
