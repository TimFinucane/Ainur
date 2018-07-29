package io.dot;

import common.graph.Graph;
import io.GraphReader;
import sun.misc.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Takes an InputStream in .dot format and converts it to a Graph object.
 */
public class DotGraphReader extends GraphReader {
    /**
     * Constructor for GraphReader.
     * @param is The InputStream to read from.
     */
    public DotGraphReader(InputStream is) {

        super(is);

    }

    // TODO Implement method
    @Override
    public Graph read() {

        String streamText = convertStreamToString(_is);

        
        Pattern nodePattern = Pattern.compile("(\\s*)([^s]+?(?=[\\s*|\\[]))(\\s*\\[\\s*Weight\\s*=\\s*)(\\d+)(\\s*\\]\\s*;)");

        return null;

    }

    private String convertStreamToString(InputStream inputStream) {

        Scanner s = new Scanner(inputStream).useDelimiter("\\A");

        return s.hasNext() ? s.next() : "";

    }
}
