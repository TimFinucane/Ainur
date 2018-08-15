package algorithm;

import common.graph.Graph;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GreedyAlgorithmTests {

    @Test
    public void testBasicGraph(){
        Graph g = new Graph.Builder()
                .node("a", 2)
                .node("b", 3)
                .node("c", 5)
                .node("d", 1)
                .node("e", 3)
                .edge("a", "b", 1)
                .edge("a", "c", 1)
                .edge("c", "d", 1)
                .edge("b", "e", 1)
                .edge("d", "e", 2)
                .build();
        GreedyAlgorithm algorithm = new GreedyAlgorithm();
        algorithm.run(g, 2);
        assertEquals(9, algorithm.getCurrentBest().getEndTime());
    }

}
