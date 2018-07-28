package common.graph;

/**
 * Stores information pertaining to a directed edge between two Nodes of a Graph object.
 */
public class Edge {

    private final Node _to;
    private final Node _from;
    private final int _cost;

    /**
     * Default constructor for an Edge
     * @param _to destination node
     * @param _from origin node
     * @param _cost edge computation cost
     */
    public Edge(Node _to, Node _from, int _cost) {
        this._to = _to;
        this._from = _from;
        this._cost = _cost;
    }

    /**
     * Method returns Node at the end of the directed edge
     * @return destination Node
     */
    public Node getNodeTo() {
        return _to;
    }

    /**
     * Method returns Node at start of the directed edge
     * @return origin Node
     */
    public Node getNodeFrom() {
        return _from;
    }

    /**
     * Method returns computation cost associated to this edge
     * @return edge computation cost
     */
    public int getCost() {
        return _cost;
    }
}
