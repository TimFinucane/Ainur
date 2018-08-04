package algorithm;

import algorithm.heuristics.CriticalPath;
import algorithm.heuristics.LowerBound;
import common.categories.HobbitonUnitTests;
import common.graph.Edge;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.Task;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Basic class to test the functionality of the critical path finding implementation.
 */
@Category(HobbitonUnitTests.class)
public class CriticalPathTests {

    /**
     * Test checks that the critical path implementation generates a correct answer and runs without error.
     */
    @Test
    public void testCriticalPathSinglePath(){
        //Generating dummy data for a simple graph and schedule
        Graph graph = new Graph.Builder()
            .node("A", 3)
            .node("B", 4)
            .edge("A", "B", 2)
            .build();

        Schedule schedule = new Schedule(1);

        LowerBound lowerBound = new CriticalPath();

        // Calls critical path method with dummy data.
        int criticalPath = lowerBound.estimate(graph, schedule, graph.getEntryPoints());
        assertEquals(criticalPath, 7);
    }

    /**
     * Test makes sure the algorithm is able to generate a correct value for a critical path when passed a more
     * complex graph object.
     */
    @Test
    public void getCriticalPathTwoChoices(){
        //Generating dummy data for a simple graph and schedule
        Graph graph = new Graph.Builder()
            .node("A", 3)
            .node("B", 4)
            .node("C", 5)
            .node("D", 1)
            .node("E", 10)
            .edge("A", "B", 2)
            .edge("A", "C", 1)
            .edge("C", "D", 1)
            .edge("C", "E", 1)
            .build();

        Schedule schedule = new Schedule(1);

        LowerBound lowerBound = new CriticalPath();

        // Calls critical path method with dummy data.
        int criticalPath = lowerBound.estimate(graph, schedule, graph.getEntryPoints());
        assertEquals(criticalPath, 18);
    }


    /**
     * Tests to ensure that the algorithm works correctly for a graph with multiple entry points
     */
    @Test
    public void getCriticalPathMultipleEntries(){
        //Generating dummy data for a simple graph and schedule
        Graph graph = new Graph.Builder()
                .node("A", 3)
                .node("B", 4)
                .node("C", 5)
                .node("D", 1)
                .node("E", 10)
                .edge("A", "B", 2)
                .edge("C", "D", 1)
                .edge("C", "E", 1)
                .build();

        Schedule schedule = new Schedule(1);

        LowerBound lowerBound = new CriticalPath();

        // Calls critical path method with dummy data.
        int criticalPath = lowerBound.estimate(graph, schedule, graph.getEntryPoints());
        assertEquals(criticalPath, 15);
    }

    /**
     * Verifies that the algorithm runs correctly when passed a partial schedule that has been populated.
     */
    @Test
    public void getCriticalPathWithPopulatedSchedule(){
        //Generating dummy data for a simple graph and schedule
        Graph graph = new Graph.Builder()
                .node("A", 3)
                .node("B", 4)
                .node("C", 5)
                .node("D", 1)
                .node("E", 10)
                .edge("A", "B", 2)
                .edge("A", "C", 1)
                .edge("C", "D", 1)
                .edge("C", "E", 1)
                .build();

        // Only the root node, A is added to the schedule.
        Node nodeA = graph.getEntryPoints().get(0);
        Task taskA = new Task(0, nodeA);

        Schedule schedule = new Schedule(1);
        schedule.getProcessors().get(0).addTask(taskA);

        LowerBound lowerBound = new CriticalPath();

        // Next nodes to visit are the children of the root A
        List<Node> nodesToVisit = new ArrayList<>();

        for (Edge edge : graph.getOutgoingEdges(nodeA)) {
            nodesToVisit.add(edge.getDestinationNode());
        }

        int criticalPath = lowerBound.estimate(graph, schedule, nodesToVisit);
        assertEquals(criticalPath, 15);
    }
}
