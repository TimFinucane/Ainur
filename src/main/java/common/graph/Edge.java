package common.graph;

/**
 * Stores information pertaining to a directed edge between two Nodes of a Graph object.
 */
public class Edge {

    private final Node _dest;
    private final Node _origin;
    private final int _cost;

    /**
     * Default constructor for an Edge
     * @param origin origin node
     * @param dest destination node
     * @param cost edge computation cost
     */
    public Edge(Node origin, Node dest, int cost) {
        _origin = origin;
        _dest = dest;
        _cost = cost;
    }

    /**
     * Method returns Node at the end of the directed edge
     * @return destination Node
     */
    public Node getDestinationNode() {
        return _dest;
    }

    /**
     * Method returns Node at start of the directed edge
     * @return origin Node
     */
    public Node getOriginNode() {
        return _origin;
    }

    /**
     * Method returns computation cost associated to this edge
     * @return edge computation cost
     */
    public int getCost() {
        return _cost;
    }
}
