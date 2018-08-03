package common.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds references to all nodes and edges in a Graph.
 */
public class Graph {
    public static class Builder {
        private Map<String, Node> _nodes = new HashMap<>();
        private List<Edge>          _edges = new ArrayList<>();
        private int                 _idCounter = 0;

        public Builder() {}

        /**
         * Creates a new node
         */
        public Builder node(String name, int computationCost) {
            _nodes.put(name, new Node(computationCost, name, _idCounter++)); // The ++ increments the _idCounter after usage
            return this;
        }

        /**
         * Constructs an edge between two nodes.
         * Nodes with the given names should already have been added to the GraphBuilder
         */
        public Builder edge(String origin, String destination, int communicationCost) {
            _edges.add(new Edge(_nodes.get(origin), _nodes.get(destination), communicationCost));
            return this;
        }

        public Graph        build() {
            return new Graph(new ArrayList<>(_nodes.values()), _edges);
        }
    }

    private final List<Node>        _nodes;
    // Storage of edges is relative to how nodes access them.
    private final List<List<Edge>>  _incomingEdges = new ArrayList<>();
    private final List<List<Edge>>  _outgoingEdges = new ArrayList<>();
    private final List<Node>        _entryPoints = new ArrayList<>(); // Starting nodes of graph

    /**
     * Default constructor for a Graph object
     */
    protected Graph(List<Node> nodes, List<Edge> edges) {
        _nodes = nodes;

        // Initialize the edge lists
        for(int i = 0; i < _nodes.size(); ++i) {
            _incomingEdges.add(new ArrayList<>());
            _outgoingEdges.add(new ArrayList<>());
        }

        // Add every edge to it's incoming node edge list and outgoing node edge list
        for( Edge edge : edges ) {
            // Use indexOf to get the new Node (with id) that we will be using in the future
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
     * Returns a list of all the nodes of the graph
     */
    public List<Node> getNodes(){
        return _nodes;
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
