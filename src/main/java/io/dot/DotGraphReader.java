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

    // TODO: Make node labels able to be more than one character long?
    @Override
    public Graph read() {

        String streamText = convertStreamToString(_is);

        Map<String, Node> nodes = getNodes(streamText);
        List<Edge> edges = getEdges(streamText, nodes);

        return new Graph(new ArrayList<>(nodes.values()), edges);

    }


    private Map<String, Node> getNodes(String string) {

        // (?<![\s*|\>])                  :     Negative lookbehind to make sure no whitespace or '>' appears before matched string
        //                                      which ensures is a node not an edge
        // \s*([\w+])\s*                  :     Any whitespace, any alpha-numeric characters with node label as group 1, any whitespace
        // \[\s*Weight\s*=\s*(\d+)\s*\]   :     String in form of [Weight=?] with ?=node weight as group 2, any whitespace between
        //                                      [, Weight, =, ?, ] allowed.
        // /s*;                           :     Any whitespace followed by semicolon
        Pattern nodePattern = Pattern.compile("(?<![\\s*|\\>])\\s*([\\w+])\\s*\\[\\s*Weight\\s*=\\s*(\\d+)\\s*\\]\\s*;");
        Matcher m = nodePattern.matcher(string); // Match pattern to input

        Map<String, Node> nodes = new HashMap<>(); // Use hash map for edge nodes lookup later

        while (m.find()) {
            int nodeCost = Integer.parseInt(m.group(2)); // Cost
            String nodeName = m.group(1);  // Label

            nodes.put(nodeName, new Node(nodeCost, nodeName));
        }

        return nodes;
    }


    private List<Edge> getEdges(String string, Map<String, Node> nodes) {

        // \s*([\w+])\s*                  :     Any whitespace, any alpha-numeric characters with node label as group 1, any whitespace
        // \-\>\s*                        :     String in form of -> followed by any whitespace
        // ([^w]+?(?=[\s*|\[]))           :     Any alpha-numeric characters with node label as group 2 on condition they are followed
        //                                      by either whitespace or a '[' (prevents misreading of sequential lines)
        // \s*                            :     Any whitespace
        // \[\s*Weight\s*=\s*(\d+)\s*\]   :     String in form of [Weight=?] with ?=node weight as group 3, any whitespace between
        //                                      [, Weight, =, ?, ] allowed.
        // /s*;                           :     Any whitespace followed by semicolon
        Pattern edgePattern = Pattern.compile("\\s*([\\w+])\\s*\\-\\>\\s*([^w]+?(?=[\\s*|\\[]))\\s*\\[\\s*Weight\\s*=\\s*(\\d+)\\s*\\]\\s*;");
        Matcher m = edgePattern.matcher(string); // Match pattern to input

        List<Edge> edges = new ArrayList<>();

        while (m.find()) {
            int edgeCost = Integer.parseInt(m.group(3)); // Cost
            Node nodeFrom = nodes.get(m.group(1)); // Origin node label
            Node nodeTo = nodes.get(m.group(2)); // Destination node label

            if (nodeFrom == null || nodeTo == null) { // Some node does not exist for given edge, invalid graph
                throw new UncheckedIOException(new IOException("Invalid input graph semantics"));
            }

            edges.add(new Edge(nodeFrom, nodeTo, edgeCost));
        }

        return edges;

    }


    private String convertStreamToString(InputStream inputStream) {

        Scanner s = new Scanner(inputStream).useDelimiter("\\A");

        return s.hasNext() ? s.next() : "";

    }
}
