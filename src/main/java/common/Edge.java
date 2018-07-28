package common;

/**
 * Stores information pertaining to a directed edge between two Nodes of a Graph object.
 */
public class Edge {

    private final Node _to;
    private final Node _from;
    private final int _cost;

    /**
     * General constructor for an Edge object
     * @param to destination node
     * @param from origin node
     * @param cost computation cost
     */
    public Edge(Node to, Node from, int cost) {
        this._to = to;
        this._from = from;
        this._cost = cost;
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
