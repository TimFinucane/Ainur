package common;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds references to all nodes and edges in a Graph.
 */
public class Graph {

    private final List<Node> _nodes;
    private final List<Edge> _edges;

    /**
     * Default constructor for a Graph object
     * @param nodes all nodes in the Graph object
     * @param edges all edges in the Graph object
     */
    public Graph(List<Node> nodes, List<Edge> edges) {
        this._edges = edges;
        this._nodes = nodes;
    }

    /**
     * Returns a list of all the nodes that have no incoming edges, ie all the entry points of the graph.
     * @return entry point nodes.
     */
    public List<Node> getEntryPoints(){
        List<Node> entryPoints = new ArrayList<>();
        for (Node node : _nodes) {
            if (node.isEntry()) {
                entryPoints.add(node);
            }
        }
        return entryPoints;
    }

}
