package common;

import common.graph.Edge;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.Task;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ValidatorTests {

    private Graph _graph;

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

    // This tests that the isValid() method asserts true on an empty schedule
    @Test
    public void testEmptySchedule() {

        // Arrange
        Schedule schedule = new Schedule(0);

        // Act / Assert
        Assert.assertTrue(Validator.isValid(_graph, schedule));

    }

    // This tests that isValid asserts true with a single processor graph containing a single task.
    @Test
    public void testSingleTaskSchedule() {

        // Arrange
        Schedule schedule = new Schedule(1);
        schedule.getProcessors().get(0).addTask(new Task(0, new Node(10, "a")));

        // Act / Assert
        Assert.assertTrue(Validator.isValid(_graph, schedule));

    }

    @Test
    public void testOverlapSingleProcessorSchedule() {

        // Arrange
        Schedule schedule = new Schedule(1);
        schedule.getProcessors().get(0).addTask(new Task(0, new Node(10, "a")));
        schedule.getProcessors().get(0).addTask(new Task(0, new Node(10, "b")));

        // Act / Assert
        Assert.assertFalse(Validator.isValid(_graph, schedule));

    }

}
