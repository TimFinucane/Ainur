package common.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds references to all nodes and edges in a Graph.
 */
public class Graph {
    private final List<Node>                _nodes;

    // Storage of edges is relative to how nodes access them.
    // N.B. The key is the node name as of now.
    // TODO: Look into using the slightly faster ArrayList, where nodes have indices assigned to them.
    private final Map<String, List<Edge>>   _incomingEdges = new HashMap<>();
    private final Map<String, List<Edge>>   _outgoingEdges = new HashMap<>();

    private final List<Node>                _entryPoints = new ArrayList<>();

    /**
     * Default constructor for a Graph object
     */
    public Graph(List<Node> nodes, List<Edge> edges) {
        _nodes = nodes;

        // Construct the incoming and outgoing edges, relative to the nodes connected to them
        for( Node node : nodes )
        {
            _outgoingEdges.put(node.getLabel(), new ArrayList<>());
            _incomingEdges.put(node.getLabel(), new ArrayList<>());
        }

        for( Edge edge : edges )
        {
            _outgoingEdges.get(edge.getOriginNode().getLabel()).add(edge);
            _incomingEdges.get(edge.getDestinationNode().getLabel()).add(edge);
        }

        // Find the entry points of the graph and cache them
        for( Node node : _nodes )
            if( _incomingEdges.get(node.getLabel()).isEmpty() )
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
     * @param node label to reference node
     * @return List of associated outgoing edges to input node
     */
    // TODO Implement method
    public List<Edge> getOutgoingEdges(Node node) {
        return _outgoingEdges.get(node);
    }

    /**
     * Get all incoming edges associated to a particular node
     * @param node label to reference node
     * @return List of associated incoming edges to input node
     */
    // TODO Implement method
    public List<Edge> getIncomingEdges(Node node) {
        return _incomingEdges.get(node);
    }

}
