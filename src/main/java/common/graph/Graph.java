package common.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds references to all nodes and edges in a Graph.
 */
public class Graph {
    private final List<Node>        _nodes = new ArrayList<>();
    // Storage of edges is relative to how nodes access them.
    private final List<List<Edge>>  _incomingEdges = new ArrayList<>();
    private final List<List<Edge>>  _outgoingEdges = new ArrayList<>();
    private final List<Node>        _entryPoints = new ArrayList<>(); // Starting nodes of graph

    /**
     * Default constructor for a Graph object
     */
    public Graph(List<Node> nodes, List<Edge> edges) {
        // Construct the incoming and outgoing edges, relative to the nodes connected to them
        int id = 0;
        for( Node node : nodes ) {
            _nodes.add(new Node(node.getComputationCost(), node.getLabel(), id));
            _outgoingEdges.add(new ArrayList<>());
            _incomingEdges.add(new ArrayList<>());

            id++;
        }

        // Add every edge to it's incoming node edge list and outgoing node edge list
        for( Edge edge : edges ) {
            _incomingEdges.get(edge.getDestinationNode().getId()).add(edge);
            _outgoingEdges.get(edge.getOriginNode().getId()).add(edge);
        }

        // Find the entry points of the graph and cache them
        for( Node node : _nodes )
            if( _incomingEdges.get(node.getId()).isEmpty() )
                _entryPoints.add(node);
    }

    /**
     * Returns a list of all the nodes that have no incoming edges
     */
    public List<Node> getEntryPoints(){
        return _entryPoints;
    }

    /**
     * Get all outgoing edges associated to a particular node
     */
    public List<Edge> getOutgoingEdges(Node node) {
        return _outgoingEdges.get(node.getId());
    }

    /**
     * Get all incoming edges associated to a particular node
     */
    public List<Edge> getIncomingEdges(Node node) {
        return _incomingEdges.get(node.getId());
    }

    /**
     * Gets the number of nodes in the graph
     */
    public int        size() {
        return _nodes.size();
    }
}
