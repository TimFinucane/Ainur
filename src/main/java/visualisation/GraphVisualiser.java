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

public class GraphVisualiser extends Region {
    /* MACROS */

    // Window dimensions
    public static final int WINDOW_HEIGHT = 500;
    public static final int WINDOW_WIDTH = 1000;

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
     *
     * @param graph
     */
    public GraphVisualiser(Graph graph) {
        _gsGraph = createGSGraph(graph);
        _swingNode = new SwingNode();
        _dimension = new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT);
        
        this.createSwingContent(_swingNode);
        this.getChildren().add(_swingNode);

    }

    /* Public Methods */

    /**
     *
     * @param node
     */
    public void update(Node node) {
        if (_currentNodeId != null) {
            _gsGraph.getNode(_currentNodeId).removeAttribute(UI_CLASS);
        }

        _currentNodeId = node.getLabel();
        _gsGraph.getNode(_currentNodeId).setAttribute(UI_CLASS, MARKED_CLASS);

    }

    /* Private Helper Methods */

    /**
     *
     * @param graph
     * @return
     */
    private org.graphstream.graph.Graph createGSGraph(Graph graph) {
        org.graphstream.graph.Graph gsGraph = new SingleGraph(graph.getName());
        List<Node> nodes = graph.getNodes();
        List<Edge> edges = graph.getAllEdges();

        for (Node node : nodes) {
            String label = node.getLabel();
            gsGraph.addNode(label);
            gsGraph.getNode(label).addAttribute(UI_LABEL, label);
        }

        for (Edge edge: edges) {
            this.addGSEdge(gsGraph, edge);
        }

        gsGraph.addAttribute(UI_STYLE_SHEET, STYLE_SHEET);

        return gsGraph;
    }

    /**
     *
     * @param swingNode
     */
    private void createSwingContent(SwingNode swingNode) {
        SwingUtilities.invokeLater(() -> {

            Viewer viewer = new Viewer(_gsGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
            ViewPanel view = viewer.addDefaultView(false);
            view.setPreferredSize(_dimension);

            HierarchicalLayout hl = new HierarchicalLayout();
            hl.setRoots("0");
            viewer.enableAutoLayout(hl);

            MouseMotionListener mouseMotionListener = view.getMouseMotionListeners()[0];
            view.removeMouseMotionListener(mouseMotionListener);


            swingNode.setContent(view);
        });
    }

    /**
     *
     * @param gsGraph
     * @param edge
     */
    private void addGSEdge(org.graphstream.graph.Graph gsGraph, Edge edge) {
        String originLabel = edge.getOriginNode().getLabel();
        String destinationLabel = edge.getDestinationNode().getLabel();
        String edgeLabel = originLabel + destinationLabel;

        gsGraph.addEdge(edgeLabel, originLabel, destinationLabel, true);
    }
}
