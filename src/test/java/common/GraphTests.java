package common;

import common.graph.Edge;
import common.graph.Graph;
import common.graph.Node;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class GraphTests {
    Graph _graph;

    @Before
    public void    initializeGraph()
    {
        List<Node> nodes = Arrays.asList(
            new Node(1, "a"),
            new Node(2, "b"),
            new Node(3, "c")
        );

        List<Edge> edges = Arrays.asList(
            new Edge(nodes.get(0), nodes.get(1), 1),
            new Edge(nodes.get(1), nodes.get(2), 1),
            new Edge(nodes.get(0), nodes.get(2), 1)
        );

        _graph = new Graph(nodes, edges);
    }

    /**
     * A basic test to ensure that basic access to components of the graph works
     */
    @Test
    public void    testAccessors()
    {
        Assert.assertEquals(1, _graph.getEntryPoints().size());

        Node entryNode = _graph.getEntryPoints().get(0);

        Assert.assertEquals(entryNode, _graph.getEntryPoints().get(0));

        List<Edge> outgoing = _graph.getOutgoingEdges(entryNode);

        Assert.assertEquals(2, outgoing.size());
        Assert.assertEquals(entryNode, outgoing.get(0).getOriginNode());
    }
}
