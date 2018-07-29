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

    // TODO: add exception throwing if not in right format
    @Override
    public Graph read() {

        String streamText = convertStreamToString(_is);

        Map<String, Node> nodes = getNodes(streamText);
        List<Edge> edges = getEdges(streamText, nodes);

        return new Graph(new ArrayList<>(nodes.values()), edges);

    }


    private Map<String, Node> getNodes(String string) {

        // TODO: Enable node names to be non integer
        Pattern nodePattern = Pattern.compile("(?<![\\s*|\\>])\\s*([\\d+])\\s*\\[\\s*Weight\\s*=\\s*(\\d+)\\s*\\]\\s*;");
        Matcher m = nodePattern.matcher(string);

        Map<String, Node> nodes = new HashMap<>();

        while (m.find()) {
            int nodeCost = Integer.parseInt(m.group(2));
            String nodeName = m.group(1);

            nodes.put(nodeName, new Node(nodeCost, nodeName));
        }

        return nodes;
    }


    // TODO: Add error checking
    private List<Edge> getEdges(String string, Map<String, Node> nodes) {

        // TODO: should node names be able to be strings as well
        Pattern edgePattern = Pattern.compile("\\s*([\\d+])\\s*\\-\\>\\s*([^s]+?(?=[\\s*|\\[]))\\s*\\[\\s*Weight\\s*=\\s*(\\d+)\\s*\\]\\s*;");
        Matcher m = edgePattern.matcher(string);

        List<Edge> edges = new ArrayList<>();

        while (m.find()) {
            int edgeCost = Integer.parseInt(m.group(3));
            Node nodeFrom = nodes.get(m.group(1));
            Node nodeTo = nodes.get(m.group(2));

            if (nodeFrom == null || nodeTo == null) {
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
