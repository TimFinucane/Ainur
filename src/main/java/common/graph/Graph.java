package common.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds references to all nodes and edges in a Graph.
 */
public class Graph {
    /**
     * A simple definition of a node that can be fed into a graph for it to work on
     */
    public static class NodeDef {
        public NodeDef(String name, int computationCost) {
            _name = name;
            _computationCost = computationCost;
        }

        public String   name() {
            return _name;
        }
        public int      computationCost() {
            return _computationCost;
        }

        private String  _name;
        private int     _computationCost;
    }

    /**
     * A simple definition of an edge that can be fed into a graph for it to work on
     */
    public static class EdgeDef {
        public EdgeDef(String origin, String destination, int communicationCost) {
            _origin = origin;
            _destination = destination;
            _communicationCost = communicationCost;
        }

        public String   origin() {
            return _origin;
        }
        public String   destination() {
            return _destination;
        }
        public int      communicationCost() {
            return _communicationCost;
        }

        private String  _origin;
        private String  _destination;
        private int     _communicationCost;
    }

    /**
     * Default constructor for a Graph object
     */
    public Graph(List<Node> nodes, List<Edge> edges) {
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
     * Constructor for easier graph creation when creating manually.
     * We seriously, actually, have to use a static method because if it is a constructor java confuses it for the
     * constructor we already have because java doesnt undertand that static languages shouldnt have type erasure.
     */
    public static Graph createFrom(List<NodeDef> nodes, List<EdgeDef> edges) {
        Map<String, Node> actualNodes = new HashMap<>();

        for(int i = 0; i < nodes.size(); ++i)
            actualNodes.put(nodes.get(i).name(), new Node(nodes.get(i).computationCost(), nodes.get(i).name(), i));

        List<Edge> actualEdges = new ArrayList<>();
        for(EdgeDef edgeDef : edges) {
            actualEdges.add(
                new Edge(
                    actualNodes.get(edgeDef.origin()),
                    actualNodes.get(edgeDef.destination()),
                    edgeDef.communicationCost())
            );
        }

        return new Graph(new ArrayList<>(actualNodes.values()), actualEdges);
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

    private final List<Node>        _nodes;
    // Storage of edges is relative to how nodes access them.
    private final List<List<Edge>>  _incomingEdges = new ArrayList<>();
    private final List<List<Edge>>  _outgoingEdges = new ArrayList<>();
    private final List<Node>        _entryPoints = new ArrayList<>(); // Starting nodes of graph
}
