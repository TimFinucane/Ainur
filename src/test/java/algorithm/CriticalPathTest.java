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

/**
 * Basic class to test the functionality of the critical path finding implementation.
 */
public class CriticalPathTest {

    /**
     * Test checks that the critical path implementation generates a correct answer and runs without error.
     */
    @Test
    public void testCriticalPathSinglePath(){
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

        // Calls critical path method with dummy data.
        int criticalPath = lowerBound.estimate(graph, schedule, nodesToVisit);
        assertEquals(criticalPath, 7);
    }

    /**
     * Test makes sure the algorithm is able to generate a correct value for a critical path when passed a more
     * complex graph object.
     */
    @Test
    public void getCriticalPathTwoChoices(){
        //Generating dummy data for a simple graph and schedule
        Node nodeA = new Node(3, "A");
        Node nodeB = new Node(4, "B");
        Node nodeC = new Node(5, "C");
        Node nodeD = new Node(1, "D");
        Node nodeE = new Node(10, "E");
        Edge edgeAB = new Edge(nodeA, nodeB, 2);
        Edge edgeAC = new Edge(nodeA, nodeC, 1);
        Edge edgeCD = new Edge(nodeC, nodeD, 1);
        Edge edgeCE = new Edge(nodeC, nodeE, 1);

        List<Node> nodes = new ArrayList<>();
        nodes.add(nodeA);
        nodes.add(nodeB);
        nodes.add(nodeC);
        nodes.add(nodeD);
        nodes.add(nodeE);

        List<Node> nodesToVisit = new ArrayList<>();
        nodesToVisit.add(nodeA);

        List<Edge> edges = new ArrayList<>();
        edges.add(edgeAB);
        edges.add(edgeAC);
        edges.add(edgeCD);
        edges.add(edgeCE);

        Graph graph = new Graph(nodes, edges);

        Schedule schedule = new Schedule(1);

        LowerBound lowerBound = new CriticalPath();

        // Calls critical path method with dummy data.
        int criticalPath = lowerBound.estimate(graph, schedule, nodesToVisit);
        assertEquals(criticalPath, 18);
    }


    /**
     * Tests to ensure that the algorithm works correctly for a graph with multiple entry points
     */
    @Test
    public void getCriticalPathMultipleEntries(){
        //Generating dummy data for a simple graph and schedule
        Node nodeA = new Node(3, "A");
        Node nodeB = new Node(4, "B");
        Node nodeC = new Node(5, "C");
        Node nodeD = new Node(1, "D");
        Node nodeE = new Node(10, "E");
        Edge edgeAB = new Edge(nodeA, nodeB, 2);
        Edge edgeCD = new Edge(nodeC, nodeD, 1);
        Edge edgeCE = new Edge(nodeC, nodeE, 1);

        List<Node> nodes = new ArrayList<>();
        nodes.add(nodeA);
        nodes.add(nodeB);
        nodes.add(nodeC);
        nodes.add(nodeD);
        nodes.add(nodeE);

        List<Node> nodesToVisit = new ArrayList<>();
        nodesToVisit.add(nodeA);
        nodesToVisit.add(nodeC);

        List<Edge> edges = new ArrayList<>();
        edges.add(edgeAB);
        edges.add(edgeCD);
        edges.add(edgeCE);

        Graph graph = new Graph(nodes, edges);

        Schedule schedule = new Schedule(1);

        LowerBound lowerBound = new CriticalPath();

        // Calls critical path method with dummy data.
        int criticalPath = lowerBound.estimate(graph, schedule, nodesToVisit);
        assertEquals(criticalPath, 15);
    }



}
