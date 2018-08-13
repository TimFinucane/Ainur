package visualisation;

import common.graph.Edge;
import common.graph.Graph;
import common.graph.Node;
import javafx.embed.swing.SwingNode;
import javafx.scene.layout.Region;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.layout.HierarchicalLayout;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseMotionListener;
import java.util.List;

/**
 * This is a class used to visually render task graphs.
 * It works as an adapter between the Ainur graph class and the Graphstream graph class.
 * It also allows graphstream swing components to be displayable in a javafx GUI.
 * Once created the nodes on the graph can be highlighted to reflect the progress of Ainur.
 */
public class GraphVisualiser extends Region {

    /* MACROS */

    // Window dimensions
    public static final int WINDOW_HEIGHT = 500;
    public static final int WINDOW_WIDTH = 800;

    // Used for styling the graph and its nodes
    public static final String STYLE_SHEET =
            "node {" +
            "   fill-color: black;" +
            "}" +
            "node.marked {" +
            "   fill-color:red;" +
            "}";
    public static final String UI_CLASS = "ui.class";
    public static final String UI_LABEL = "ui.label";
    public static final String UI_STYLE_SHEET = "ui.stylesheet";
    public static final String MARKED_CLASS = "marked";

    // The label of the root node
    public static final String ROOT_NODE_LABEL = "0";

    /* Fields */

    // Used for rendering swing component in javafx
    private final SwingNode _swingNode;
    private Dimension _dimension;

    // Graph stream object used for display
    private org.graphstream.graph.Graph _gsGraph;

    // Keeps track of the current highlighted node
    private String _currentNodeId;

    /* Constructors */

    /**
     * Constuctor for GraphVisualiser class.
     * Takes an Ainur Graph, creates a graphstream graph swing element and wraps this in a javafx component.
     *
     * @param graph The Ainur graph to be visualised.
     */
    public GraphVisualiser(Graph graph) {
        // Create the graphstream graph
        _gsGraph = createGSGraph(graph);

        // Initialize the SwingNode
        // SwingNodes are javafx components which allow swing components to be used in a javafx application
        _swingNode = new SwingNode();
        _dimension = new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT);

        // Create the swing content & adds it to the visualiser
        this.createSwingContent();
        this.getChildren().add(_swingNode);

        // Set visualiser dimensions
        this.setMinHeight(WINDOW_HEIGHT);
        this.setMinWidth(WINDOW_WIDTH);
    }

    /* Public Methods */

    /**
     * Updates the current node that is highlighted.
     * This node will be red.
     * All other nodes will be black.
     *
     * @param node The node which is to be selected
     */
    public void update(Node node) {
        // If there is already a selected node un highlight it.
        if (_currentNodeId != null) {
            _gsGraph.getNode(_currentNodeId).removeAttribute(UI_CLASS);
        }

        // Highlight the new node.
        _currentNodeId = node.getLabel();
        _gsGraph.getNode(_currentNodeId).setAttribute(UI_CLASS, MARKED_CLASS);

    }

    /* Private Helper Methods */

    /**
     * This is a private helper method.
     * This method takes an Ainur graph as input.
     * From this Ainur graph it constructs a graphstream graph.
     * This is done in order to be able to use graphstreams visualization library.
     *
     * @param graph The Ainur graph to be transformed into a graphstream graph.
     *
     * @return A graphstream graph corresponding to the inputted Ainur graph.
     */
    private org.graphstream.graph.Graph createGSGraph(Graph graph) {
        org.graphstream.graph.Graph gsGraph = new SingleGraph(graph.getName());

        // Get a list of nodes and edges from the inputted graph.
        List<Node> nodes = graph.getNodes();
        List<Edge> edges = graph.getAllEdges();

        // Cycle through edges and add them to the graphstream graph.
        for (Node node : nodes) {
            String label = node.getLabel();
            // Add to graphstream graph
            gsGraph.addNode(label);
            // Add the label to be displayed alongside the node
            gsGraph.getNode(label).addAttribute(UI_LABEL, label);
        }

        // Cycle through the edges and add them to the graphstream graph
        for (Edge edge: edges) {
            this.addGSEdge(gsGraph, edge);
        }

        // Add a stylesheet to the graph.
        gsGraph.addAttribute(UI_STYLE_SHEET, STYLE_SHEET);

        return gsGraph;
    }

    /**
     * This is a private helper method.
     * Creates the viewers necessary to display the graphstream graph.
     * Adds some custom display options...
     *      - Non-interactive nodes
     *      - Hierarchical display
     * Assigns the swing content to the _swingNode variable so it can be displayed in a javafx GUI.
     */
    private void createSwingContent() {
        SwingUtilities.invokeLater(() -> {

            // Create the viewers for displaying the graphstream graph
            Viewer viewer = new Viewer(_gsGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
            ViewPanel view = viewer.addDefaultView(false);

            // Set Swing window dimensions
            view.setPreferredSize(_dimension);

            // Set Hierarchical layout with node "0" being the root
            HierarchicalLayout hl = new HierarchicalLayout();
            hl.setRoots(ROOT_NODE_LABEL);
            viewer.enableAutoLayout(hl);

            // Remove the ability to move nodes on the graph with mouse
            MouseMotionListener mouseMotionListener = view.getMouseMotionListeners()[0];
            view.removeMouseMotionListener(mouseMotionListener);

            // Assogn the view to the swingNode component
            _swingNode.setContent(view);
        });
    }

    /**
     * Private helper method for assigning edges to a graphstream graph.
     * Takes an Ainur edge object, extracts necessary information and uses it to add an edge to a graphstream graph.
     *
     * @param gsGraph The graphstream graph to insert the edge into
     * @param edge The Ainur edge object to insert into the graphstream graph
     */
    private void addGSEdge(org.graphstream.graph.Graph gsGraph, Edge edge) {
        // Extract Edge labels
        String originLabel = edge.getOriginNode().getLabel();
        String destinationLabel = edge.getDestinationNode().getLabel();
        String edgeLabel = originLabel + destinationLabel;

        // Add edge to graphstream graph
        // Make the edges directed
        gsGraph.addEdge(edgeLabel, originLabel, destinationLabel, true);
    }
}
