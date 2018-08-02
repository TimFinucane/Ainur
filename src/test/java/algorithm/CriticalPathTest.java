package algorithm;

import algorithm.heuristics.CriticalPath;
import algorithm.heuristics.LowerBound;
import common.graph.Edge;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CriticalPathTest {
    /**
     * Test checks that the critical path implementation generates a correct answer and runs without error.
     */
    @Test
    public void testCriticalPath(){
        //Generating dummy data for a simple graph and schedule
        Node nodeA = new Node(3, "A");
        Node nodeB = new Node(4, "B");
        Edge edge = new Edge(nodeA, nodeB, 2);

        List<Node> nodes = new ArrayList<>();
        nodes.add(nodeA);
        nodes.add(nodeB);

        List<Node> nodesToVisit = new ArrayList<>();
        nodesToVisit.add(nodeA);

        List<Edge> edges = new ArrayList<>();
        edges.add(edge);

        Graph graph = new Graph(nodes, edges);

        Schedule schedule = new Schedule(1);

        LowerBound lowerBound = new CriticalPath();

        int criticalPath = lowerBound.estimate(graph, schedule, nodesToVisit);

        assertEquals(criticalPath, 7);
    }


}
