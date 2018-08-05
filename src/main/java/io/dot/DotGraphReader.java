package io.dot;

import common.graph.Edge;
import common.graph.Graph;
import common.graph.Node;
import io.GraphReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.MalformedInputException;
import java.util.*;
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


    /**
     * Reads an input stream  formatted in .dot format. This method parses the text into a common.Graph type,
     * with all appropriate node labels and costs mapped over, as well as edge origin, destination and weight.
     * The Method assumes either the test starts immediately with the .dot content OR starts with a header in the
     * for of:
     * <header> {
     *     ...dot content...
     * }
     * @return graph : Graph
     */
    @Override
    public Graph read() {

        Graph.Builder graphBuilder = new Graph.Builder();

        String streamText = convertStreamToString(_is);

        getName(streamText, graphBuilder);
        getNodes(streamText, graphBuilder);
        getEdges(streamText, graphBuilder);

        return graphBuilder.build();

    }

    private void getName(String string, Graph.Builder builder) {
        Pattern namePattern = Pattern.compile("digraph \"([^\\\"]+)\"");
        Matcher m = namePattern.matcher(string);

        if(!m.find())
            //throw new RuntimeException("digraph not found in file!");
            return;

        builder.name(m.group(1));

        if(m.find())
            throw new RuntimeException("more than one digraph in the file!");
    }

    private void getNodes(String string, Graph.Builder builder) {

        // (?<=;|^|\{)                    :     Positive lookbehind ensuring that preceding the string is either a ';', '{'
        //                                      or '^' (beginning of string)
        // \s*([\w+])\s*                  :     Any whitespace, any alpha-numeric characters with node label as group 1, any whitespace
        // \[\s*Weight\s*=\s*(\d+)\s*]   :     String in form of [Weight=?] with ?=node weight as group 2, any whitespace between
        //                                      [, Weight, =, ?, ] allowed.
        // /s*;                           :     Any whitespace followed by semicolon
        Pattern nodePattern = Pattern.compile("(?<=;|^|\\{)\\s*(\\w+)\\s*\\[\\s*Weight\\s*=\\s*(\\d+)\\s*]\\s*;");
        Matcher m = nodePattern.matcher(string); // Match pattern to input

        while (m.find()) {
            int nodeCost = Integer.parseInt(m.group(2)); // Cost
            String nodeName = m.group(1);  // Label

            builder.node(nodeName, nodeCost);
        }
    }


    private void getEdges(String string, Graph.Builder builder) {

        // (?<=;|^|\{)                    :     Positive lookbehind ensuring that preceding the string is either a ';', '{'
        //                                      or '^' (beginning of string)
        // \s*([\w+])\s*                  :     Any whitespace, any alpha-numeric characters with node label as group 1, any whitespace
        // ->\s*                          :     String in form of -> followed by any whitespace
        // ([\w+])\s*                     :     Any alpha-numeric characters with node label as group 2, any whitespace
        // \[\s*Weight\s*=\s*(\d+)\s*]    :     String in form of [Weight=?] with ?=node weight as group 3, any whitespace between
        //                                      [, Weight, =, ?, ] allowed.
        // /s*;                           :     Any whitespace followed by semicolon
        Pattern edgePattern = Pattern.compile("(?<=;|^|\\{)\\s*(\\w+)\\s*->\\s*(\\w+)\\s*\\[\\s*Weight\\s*=\\s*(\\d+)\\s*]\\s*;");
        Matcher m = edgePattern.matcher(string); // Match pattern to input

        while (m.find()) {
            int edgeCost = Integer.parseInt(m.group(3)); // Cost
            String originLabel = m.group(1); // Origin node label, get from nodes map
            String destLabel = m.group(2); // Destination node label, get from nodes map

            builder.edge(originLabel, destLabel, edgeCost);
        }

    }


    private String convertStreamToString(InputStream inputStream) {

        Scanner s = new Scanner(inputStream).useDelimiter("\\A");

        return s.hasNext() ? s.next() : "";

    }
}
