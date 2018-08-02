package common.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphBuilder {
    private Map<String, Node>   _nodes = new HashMap<>();
    private List<Edge>          _edges = new ArrayList<>();
    private int                 _idCounter = 0;

    public GraphBuilder() {}

    /**
     * Creates a new node
     */
    public GraphBuilder node(int computationCost, String name) {
        _nodes.put(name, new Node(computationCost, name, _idCounter++)); // The ++ increments the _idCounter after usage
        return this;
    }
    public GraphBuilder node(String name, int computationCost) {
        return node(computationCost, name);
    }

    public GraphBuilder edge(String origin, String destination, int communicationCost) {
        _edges.add(new Edge(_nodes.get(origin), _nodes.get(destination), communicationCost));
        return this;
    }
    public GraphBuilder edge(int communicationCost, String origin, String destination) {
        return edge(origin, destination, communicationCost);
    }

    public Graph        build() {
        return new Graph(new ArrayList<>(_nodes.values()), _edges);
    }
}
