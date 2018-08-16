package visualisation.modules;

import common.graph.Edge;
import common.graph.Graph;
import common.graph.Node;
import javafx.embed.swing.SwingNode;
import javafx.scene.layout.Region;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

import javax.swing.*;
import java.awt.*;
import java.util.*;
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
    public static final int WINDOW_WIDTH = 750;

    // Used for styling the graph and its nodes
    public static final String STYLE_SHEET =
            "node {" +
            "   fill-color: grey, blue;" +
            "   fill-mode: dyn-plain;" +
            "   text-color: black;" +
            "   text-background-mode: rounded-box;" +
            "   text-alignment: above;" +
            "   text-size: 15px;" +
            "   size: 15px;" +
            "   stroke-mode: plain;" +
            "   stroke-color: black;" +
            "   shadow-color: blue, white;" +
            "   shadow-mode: gradient-radial;" +
            "   shadow-offset: 0;" +
            "}" +
            "node.marked {" +
            "   fill-color:red;"  +
            "   size: 20px;" +
            "   text-color: red;" +
            "   text-style: bold;" +
            "}" +
            "edge {" +
            "   arrow-shape: arrow;" +
            "   arrow-size: 15px, 5px;" +
            "   size: 1.5px;" +
            "}";
    public static final String UI_LABEL = "ui.label";
    public static final String UI_STYLE_SHEET = "ui.stylesheet";

    /* Fields */

    // Used for rendering swing component in javafx
    private final SwingNode _swingNode;
    private Dimension _dimension;

    // Graph stream object used for display
    private org.graphstream.graph.Graph _gsGraph;

    // Keeps track of how many times a node has been visited
    private Map<Node, Long> _nodeFrequencies;

    // Interpolation arrays
    private double[] _oldProportions;
    private double[] _newProportions;

    /* Constructors */

    /**
     * Constuctor for GraphVisualiser class.
     * Takes an Ainur Graph, creates a graphstream graph swing element and wraps this in a javafx component.
     *
     * @param graph The Ainur graph to be visualised.
     * @param highQualityRender True for high quality render, false otherwise
     */
    public GraphVisualiser(Graph graph, boolean highQualityRender) {
        // Use the fully compliant css renderer for graphstream
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        _nodeFrequencies = new HashMap<>();
        _oldProportions = new double[graph.size()];
        _newProportions = new double[graph.size()];

        // Create the graphstream graph
        _gsGraph = createGSGraph(graph);

        // Initialize the SwingNode
        // SwingNodes are javafx components which allow swing components to be used in a javafx application
        _swingNode = new SwingNode();
        _dimension = new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT);

        // If instructed to render with high quality add attribute
        if (highQualityRender)
            this.setHighRenderQuality(true);

        // Create the swing content & adds it to the visualiser
        this.createSwingContent();
        this.getChildren().add(_swingNode);

        // Set visualiser dimensions
        this.setMinHeight(WINDOW_HEIGHT);
        this.setMinWidth(WINDOW_WIDTH);
    }

    /**
     * Constuctor for GraphVisualiser class.
     * Takes an Ainur Graph, creates a graphstream graph swing element and wraps this in a javafx component.
     *
     * @param graph The Ainur graph to be visualised.
     */
    public GraphVisualiser(Graph graph) {
        this(graph, true);
    }

    /* Public Methods */

    /**
     * Updates the display to show the current node frequencies.
     */
    public void update(double interpolation) {
        for (int i = 0; i < _newProportions.length; i++) {
            double interpVal = _newProportions[i] *  interpolation + _oldProportions[i] * (1 - interpolation);
            _gsGraph.getNode(i).setAttribute("ui.style", String.format("shadow-width: %f;", interpVal * 2));
            _gsGraph.getNode(i).setAttribute("ui.color", interpVal / 2);
        }
    }

    /**
     * Resets the node frequencies.
     * Updates interpolation values.
     */
    public void flush() {
        _oldProportions = _newProportions;

        // Get the sum of frequencies (lol)
        long total = _nodeFrequencies.values().stream().mapToLong(Long::valueOf).sum();
        if (total == 0)
            total = 1; // cheeky one Tim ;)

        double average = total / (double) _nodeFrequencies.size();
        for (Map.Entry<Node, Long> pair : _nodeFrequencies.entrySet()) {
            _newProportions[pair.getKey().getId()] = pair.getValue() / average;
        }
        _nodeFrequencies.replaceAll((node, Long) -> 0L);
        }

    /**
     * Increment a nodes frequency.
     *
     * @param node The node whose frequency to increment.
     */
    public void nodeVisited(Node node) {
        if (node != null)
            _nodeFrequencies.put(node, _nodeFrequencies.get(node) + 1);
    }

    /**
     * Sets the render quality of the graph.
     *
     * @param highQuality True sets the quality to high
     *                    False sets the quality to low
     */
    public void setHighRenderQuality(boolean highQuality) {
        if (highQuality) {
            _gsGraph.addAttribute("ui.quality");
            _gsGraph.addAttribute("ui.antialias");
        } else {
            _gsGraph.removeAttribute("ui.quality");
            _gsGraph.removeAttribute("ui.antialias");
        }
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

            // Initialise the node frequencies
            _nodeFrequencies.put(node, 0L);
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

            // Handle setting up viewer
            viewer.enableAutoLayout();

            // Assign the view to the swingNode component
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
