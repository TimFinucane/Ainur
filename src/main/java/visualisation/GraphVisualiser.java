package visualisation;

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
import java.util.List;

public class GraphVisualiser extends Region {
    /* MACROS */

    public static final int WINDOW_HEIGHT = 500;
    public static final int WINDOW_WIDTH = 1000;

    /* FIELDS */

    private final SwingNode _swingNode;
    private org.graphstream.graph.Graph _gsGraph;
    private Dimension _dimension;

    /* Constructors */

    /**
     *
     * @param graph
     */
    public GraphVisualiser(Graph graph) {
        _gsGraph = createGSGraph(graph);
        _swingNode = new SwingNode();
        _dimension = new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT);

        setUpWindow();
    }

    /* Functions */

    /**
     *
     */
    private void setUpWindow () {
        this.createSwingContent(_swingNode);
        this.getChildren().add(_swingNode);
    }

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
            gsGraph.addNode(node.getLabel());
        }

        for (Edge edge: edges) {
            this.addGSEdge(gsGraph, edge);
        }

        return gsGraph;
    }

    /**
     *
     * @param swingNode
     * @param viewPanel
     */
    private void createSwingContent(SwingNode swingNode) {
        SwingUtilities.invokeLater(() -> {

            Viewer viewer = new Viewer(_gsGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
            ViewPanel view = viewer.addDefaultView(false);
            view.setPreferredSize(_dimension);
            viewer.enableAutoLayout();

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

        gsGraph.addEdge(edgeLabel, originLabel, destinationLabel);
    }

    /**
     *
     * @param currentNode
     */
    public void update(Node currentNode) {

    }
}
