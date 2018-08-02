package common.graph;

/**
 * Stores information pertaining to a Node object in regards to cost of computation and its label.
 */
public class Node {

    private final int _computationCost;
    private final String _label;
    private final int _id;

    /**
     * Constructor for a Node object by the graph, to assign it an index
     * @param computationCost cost to compute this node
     * @param label node label
     * @param id id, usually provided by a graph or graphreader, for efficient accesses
     */
    public Node(int computationCost, String label, int id) {
        _computationCost = computationCost;
        _label = label;
        _id = id;
    }

    /**
     * Returns the cost associated to computing the task of this node.
     * @return computation cost of this node
     */
    public int getComputationCost() {
        return _computationCost;
    }

    /**
     * Returns the label of the node (ie a, b, c...)
     * @return node label
     */
    public String getLabel() {
        return _label;
    }

    public int getId() { return _id; }
}
