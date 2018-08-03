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

    private Node _nodeA;
    private Node _nodeB;
    private Node _nodeC;

    @Before
    public void initializeGraph()
    {
        _nodeA = new Node(10, "a", 1);
        _nodeB = new Node(10, "b", 2);
        _nodeC = new Node(10, "c", 3);

        List<Node> nodes = Arrays.asList(
                _nodeA,
                _nodeB,
                _nodeC
        );

        List<Edge> edges = Arrays.asList(
                new Edge(nodes.get(0), nodes.get(1), 5),
                new Edge(nodes.get(1), nodes.get(2), 5),
                new Edge(nodes.get(0), nodes.get(2), 5)
        );

        Graph.Builder builder =  new Graph.Builder();
        _graph = builder.node("a", 10)
                .node("b", 10)
                .node("c", 10)
                .edge("a", "b", 5)
                .edge("b", "c", 5)
                .edge("a", "c", 5)
                .build();
    }

    // This tests that the isValid() method asserts true on an empty schedule
    @Test
    public void testEmptySchedule() {

        // Arrange
        Schedule schedule = new Schedule(0);

        // Act / Assert
        Assert.assertTrue(Validator.isValid(_graph, schedule));

    }

    // This tests that the isValid method catches cases where tasks overlap.
    @Test
    public void testOverlapSingleProcessorSchedule() {

        // Arrange
        Schedule schedule = new Schedule(1);
        schedule.getProcessors().get(0).addTask(new Task(0, _graph.findByLabel("a")));
        schedule.getProcessors().get(0).addTask(new Task(1, _graph.findByLabel("b")));
        schedule.getProcessors().get(0).addTask(new Task(25, _graph.findByLabel("c")));

        // Act / Assert
        Assert.assertFalse(Validator.isValid(_graph, schedule));

    }

    // This tests that the isValid method catches cases where tasks end and start at exactly the same time.
    @Test
    public void testExactOverlapSingleProcessorSchedule() {

        // Arrange
        Schedule schedule = new Schedule(1);
        schedule.getProcessors().get(0).addTask(new Task(0, _graph.findByLabel("a")));
        schedule.getProcessors().get(0).addTask(new Task(0, _graph.findByLabel("b")));
        schedule.getProcessors().get(0).addTask(new Task(25, _graph.findByLabel("c")));

        // Act / Assert
        Assert.assertFalse(Validator.isValid(_graph, schedule));

    }

    // This tests that isValid fails on a schedule with incorrect ordering of tasks in relation to their dependencies.
    @Test
    public void testInvalidProcessorDependencyOrder() {

        // Arrange
        Schedule schedule = new Schedule(2);
        schedule.getProcessors().get(0).addTask(new Task(0, _graph.findByLabel("a")));
        schedule.getProcessors().get(0).addTask(new Task(10, _graph.findByLabel("b")));
        schedule.getProcessors().get(1).addTask(new Task(0, _graph.findByLabel("c")));

        // Act / Assert
        Assert.assertFalse(Validator.isValid(_graph, schedule));
    }

    // This tests that isValid passes on a schedule with correct ordering of tasks in relation to their dependencies.
    @Test
    public void testValidProcessorDependencyOrder() {

        // Arrange
        Schedule schedule = new Schedule(2);
        schedule.getProcessors().get(0).addTask(new Task(0, _graph.findByLabel("a")));
        schedule.getProcessors().get(0).addTask(new Task(10, _graph.findByLabel("b")));
        schedule.getProcessors().get(0).addTask(new Task(20, _graph.findByLabel("c")));

        // Act / Assert
        Assert.assertTrue(Validator.isValid(_graph, schedule));
    }

    // This tests that the communication cost is correctly taken into account when dependencies are shifted across processors.
    @Test
    public void testInvalidProcessorDependencyOrderCommunicationCost() {

        // Arrange
        Schedule schedule = new Schedule(2);
        schedule.getProcessors().get(0).addTask(new Task(0, _graph.findByLabel("a")));
        schedule.getProcessors().get(0).addTask(new Task(1, _graph.findByLabel("b")));
        schedule.getProcessors().get(1).addTask(new Task(21, _graph.findByLabel("c")));

        // Act / Assert
        Assert.assertFalse(Validator.isValid(_graph, schedule));
    }

    // This tests that the communication cost is correctly taken into account when dependencies are shifted across processors.
    @Test
    public void testValidProcessorDependencyOrderCommunicationCost() {

        // Arrange
        Schedule schedule = new Schedule(2);
        schedule.getProcessors().get(0).addTask(new Task(0, _graph.findByLabel("a")));
        schedule.getProcessors().get(0).addTask(new Task(10, _graph.findByLabel("b")));
        schedule.getProcessors().get(1).addTask(new Task(25, _graph.findByLabel("c")));

        // Act / Assert
        Assert.assertTrue(Validator.isValid(_graph, schedule));
    }

    // This tests that the isValid method correctly evaluates tasks across processors
    @Test
    public void testInvalidProcessorDependency() {

        // Arrange
        Schedule schedule = new Schedule(2);
        schedule.getProcessors().get(0).addTask(new Task(0, _graph.findByLabel("a")));
        schedule.getProcessors().get(0).addTask(new Task(10, _graph.findByLabel("b")));
        schedule.getProcessors().get(1).addTask(new Task(15, _graph.findByLabel("c")));

        // Act / Assert
        Assert.assertFalse(Validator.isValid(_graph, schedule));
    }

}
