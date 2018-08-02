package common;

import common.graph.Edge;
import common.graph.Graph;
import common.graph.GraphBuilder;
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
        _graph = new GraphBuilder()
            .node("a", 1)
            .node("b", 2)
            .node("c", 3)
            .edge("a", "b", 1)
            .edge("b", "c", 1)
            .edge("a", "c", 1)
            .build();
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
        Assert.assertEquals(3, _graph.size());
    }
}
