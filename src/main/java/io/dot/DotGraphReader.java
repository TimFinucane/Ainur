package io.dot;

import common.graph.Graph;
import common.graph.Node;
import io.GraphReader;
import sun.misc.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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

        List<Node> nodes = getNodes(streamText);

        Pattern edgePattern = Pattern.compile("\\s*([^s]+?(?=[\\s*|\\-]))\\s*\\-\\>\\s*([^s]+?(?=[\\s*|\\[]))\\s*\\[\\s*Weight\\s*=\\s*(\\d+)\\s*\\]\\s*;");

        return null;

    }


    private List<Node> getNodes(String string) {

        Pattern nodePattern = Pattern.compile("\\s*([^s]+?(?=[\\s*|\\[]))\\s*\\[\\s*Weight\\s*=\\s*(\\d+)\\s*\\]\\s*;");
        Matcher m = nodePattern.matcher(string);
        
        List<Node> nodes = new ArrayList<Node>();

        while (m.find()) {
            int nodeCost = Integer.parseInt(m.group(2));
            String nodeName = m.group(1);

            nodes.add(new Node(nodeCost, nodeName));
        }

        return nodes;
    }


    private String convertStreamToString(InputStream inputStream) {

        Scanner s = new Scanner(inputStream).useDelimiter("\\A");

        return s.hasNext() ? s.next() : "";

    }
}
