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

    private final HashMap<String, Node> _nodes;
    private final List<Edge> _edges;

    /**
     * Default constructor for a Graph object
     */
    public Graph(Map<String, Integer> nodes, List<Pair<String, String>>  edges) {
        _nodes = new HashMap<>();
        _edges = new ArrayList<>();
        populateNodes(nodes);
        populateEdges(edges);
    }

    /**
     * Helper method that populates local variable "_nodes" by generating Nodes
     * @param nodes Map of node labels and their associated computation costs.
     */
    // TODO Implement method
    private void populateNodes(Map<String, Integer> nodes) {
    }

    /**
     * Helper method that populate local variable "_edges" by generating Edges
     * @param edges List of Pairs storing labels of destination and origin nodes
     */
    // TODO Implement method
    private void populateEdges(List<Pair<String, String>> edges) {
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
     * @param nodeLabel label to reference node
     * @return List of associated outgoing edges to input node
     */
    // TODO Implement method
    public List<Edge> getOutgoingEdges(String nodeLabel) {
        return null;
    }

    /**
     * Get all incoming edges associated to a particular node
     * @param nodeLabel label to reference node
     * @return List of associated incoming edges to input node
     */
    // TODO Implement method
    public List<Edge> getIncomingEdges(String nodeLabel) {
        return null;
    }

}
