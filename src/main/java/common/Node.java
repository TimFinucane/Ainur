package common;

import java.util.List;

/**
 * Stores information pertaining to a Node object in regards to incoming and outgoing edges and cost of computation.
 */
public class Node {

    private final List<Edge> _incomingEdges;
    private final List<Edge> _outgoingEdges;
    private final int _computationCost;

    /**
     * General constructor for a Node object
     * @param incomingEdges Edges with this node as destination
     * @param outgoingEdges Edges with this node as origin
     * @param computationCost cost to compute this node
     */
    public Node(List<Edge> incomingEdges, List<Edge> outgoingEdges, int computationCost) {
        this._incomingEdges = incomingEdges;
        this._outgoingEdges = outgoingEdges;
        this._computationCost = computationCost;
    }

    /**
     * Returns a list of the incoming edges for this node.
     * @return incoming edges
     */
    public List<Edge> get_incomingEdges() {
        return _incomingEdges;
    }

    /**
     * Returns a list of the outgoing edges for this node.
     * @return outgoing edges
     */
    public List<Edge> get_outgoingEdges() {
        return _outgoingEdges;
    }

    /**
     * Returns the cost associated to computing the task of this node.
     * @return computation cost of this node
     */
    public int get_computationCost() {
        return _computationCost;
    }

    /**
     * Returns true only if the Node has no outgoing edges, ie the Node is terminal.
     * @return true if Node is terminal
     */
    public boolean isTerminal(){
        return _outgoingEdges.isEmpty();
    }

    public boolean isEntry() {
        return _incomingEdges.isEmpty();
    }


}
