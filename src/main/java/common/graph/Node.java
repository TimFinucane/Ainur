package common.graph;

/**
 * Stores information pertaining to a Node object in regards to cost of computation and its label.
 */
public class Node {

    private final int _computationCost;
    private final String _label;

    /**
     * General constructor for a Node object
     * @param computationCost cost to compute this node
     * @param label node label
     */
    public Node(int computationCost, String label) {
        _computationCost = computationCost;
        _label = label;
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

}
