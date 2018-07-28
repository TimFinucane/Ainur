package common.graph;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds references to all nodes and edges in a Graph.
 */
public class Graph {

    private final List<Node> _nodes;
    private final List<Edge> _edges;

    /**
     * Default constructor for a Graph object
     */
    public Graph(List<Node> nodes, List<Edge> edges) {
        _nodes = nodes;
        _edges = edges;
    }

    /**
     * Returns a list of all the nodes that have no incoming edges, ie all the entry points of the graph.
     * @return entry point nodes.
     */
    // TODO Implement method
    public List<Node> getEntryPoints(){
        return null;
    }

    /**
     * Get all outgoing edges associated to a particular node
     * @param node label to reference node
     * @return List of associated outgoing edges to input node
     */
    // TODO Implement method
    public List<Edge> getOutgoingEdges(Node node) {
        return null;
    }

    /**
     * Get all incoming edges associated to a particular node
     * @param node label to reference node
     * @return List of associated incoming edges to input node
     */
    // TODO Implement method
    public List<Edge> getIncomingEdges(Node node) {
        return null;
    }

}
