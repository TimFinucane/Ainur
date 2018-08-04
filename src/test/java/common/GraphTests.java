package common;

import common.categories.HobbitonUnitTests;
import common.graph.Edge;
import common.graph.Graph;
import common.graph.Node;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

@Category(HobbitonUnitTests.class)
public class GraphTests {
    Graph _graph;

    @Before
    public void    initializeGraph()
    {
        _graph = new Graph.Builder()
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
        // A is the only entry node
        Assert.assertEquals(1, _graph.getEntryPoints().size());

        Node entryNode = _graph.getEntryPoints().get(0);

        List<Edge> outgoing = _graph.getOutgoingEdges(entryNode);

        Assert.assertEquals(2, outgoing.size());
        Assert.assertEquals(entryNode, outgoing.get(0).getOriginNode());
        Assert.assertEquals(3, _graph.size());

        // Check that we can find nodes by label, even though we shouldn't ever have to except for testing
        Assert.assertEquals(entryNode, _graph.findByLabel("a"));
    }
}
