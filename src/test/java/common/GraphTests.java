package common;

import common.graph.Edge;
import common.graph.Graph;
import common.graph.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class GraphTests {
    Graph _graph;

    @BeforeEach
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
        assertEquals(1, _graph.getEntryPoints().size());

        Node entryNode = _graph.getEntryPoints().get(0);

        List<Edge> outgoing = _graph.getOutgoingEdges(entryNode);

        assertEquals(2, outgoing.size());
        assertEquals(entryNode, outgoing.get(0).getOriginNode());
        assertEquals(3, _graph.size());

        // Check that we can find nodes by label, even though we shouldn't ever have to except for testing
        assertEquals(entryNode, _graph.findByLabel("a"));
    }
}
