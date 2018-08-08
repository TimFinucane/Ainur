package common;

import common.categories.HobbitonUnitTestsCategory;
import common.graph.Graph;
import common.schedule.Schedule;
import common.schedule.SimpleSchedule;
import common.schedule.Task;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(HobbitonUnitTestsCategory.class)
public class ValidatorTests {

    private Graph _graph;
    private Graph _complexGraph;

    @Before
    public void initializeGraph()
    {

        Graph.Builder builder =  new Graph.Builder();
        _graph = builder.node("a", 10)
                .node("b", 10)
                .node("c", 10)
                .edge("a", "b", 5)
                .edge("b", "c", 5)
                .edge("a", "c", 5)
                .build();

        _complexGraph = new Graph.Builder()
            .node("0", 5)
            .node("1", 6)
            .node("2", 5)
            .node("3", 6)
            .node("4", 4)
            .node("5", 7)
            .node("6", 7)
            .edge("0", "1", 15)
            .edge("0", "2", 11)
            .edge("0", "3", 11)
            .edge("1", "4", 19)
            .edge("1", "5", 4)
            .edge("1", "6", 21)
            .build();
    }

    // This tests that the isValid() method asserts true on an empty schedule
    @Test
    public void testEmptySchedule() {

        // Arrange
        Schedule schedule = new SimpleSchedule(0);

        // Act / Assert
        Assert.assertTrue(Validator.isValid(_graph, schedule));

    }

    // This tests that the isValid method catches cases where tasks overlap.
    @Test
    public void testOverlapSingleProcessorSchedule() {

        // Arrange
        Schedule schedule = new SimpleSchedule(1);
        schedule.addTask(new Task(0, 0, _graph.findByLabel("a")));
        schedule.addTask(new Task(0, 1, _graph.findByLabel("b")));
        schedule.addTask(new Task(0, 25, _graph.findByLabel("c")));

        // Act / Assert
        Assert.assertFalse(Validator.isValid(_graph, schedule));

    }

    // This tests that the isValid method catches cases where tasks end and start at exactly the same time.
    @Test
    public void testExactOverlapSingleProcessorSchedule() {

        // Arrange
        Schedule schedule = new SimpleSchedule(1);
        schedule.addTask(new Task(0, 0, _graph.findByLabel("a")));
        schedule.addTask(new Task(0, 0, _graph.findByLabel("b")));
        schedule.addTask(new Task(0, 25, _graph.findByLabel("c")));

        // Act / Assert
        Assert.assertFalse(Validator.isValid(_graph, schedule));

    }

    // This tests that isValid fails on a schedule with incorrect ordering of tasks in relation to their dependencies.
    @Test
    public void testInvalidProcessorDependencyOrder() {

        // Arrange
        Schedule schedule = new SimpleSchedule(2);
        schedule.addTask(new Task(0, 0, _graph.findByLabel("a")));
        schedule.addTask(new Task(0, 10, _graph.findByLabel("b")));
        schedule.addTask(new Task(1, 0, _graph.findByLabel("c")));

        // Act / Assert
        Assert.assertFalse(Validator.isValid(_graph, schedule));
    }

    // This tests that isValid passes on a schedule with correct ordering of tasks in relation to their dependencies.
    @Test
    public void testValidProcessorDependencyOrder() {

        // Arrange
        Schedule schedule = new SimpleSchedule(2);
        schedule.addTask(new Task(0, 0, _graph.findByLabel("a")));
        schedule.addTask(new Task(0, 10, _graph.findByLabel("b")));
        schedule.addTask(new Task(0, 20, _graph.findByLabel("c")));

        // Act / Assert
        Assert.assertTrue(Validator.isValid(_graph, schedule));
    }

    // This tests that the communication cost is correctly taken into account when dependencies are shifted across processors.
    @Test
    public void testInvalidProcessorDependencyOrderCommunicationCost() {

        // Arrange
        Schedule schedule = new SimpleSchedule(2);
        schedule.addTask(new Task(0, 0, _graph.findByLabel("a")));
        schedule.addTask(new Task(0, 1, _graph.findByLabel("b")));
        schedule.addTask(new Task(1, 21, _graph.findByLabel("c")));

        // Act / Assert
        Assert.assertFalse(Validator.isValid(_graph, schedule));
    }

    // This tests that the communication cost is correctly taken into account when dependencies are shifted across processors.
    @Test
    public void testValidProcessorDependencyOrderCommunicationCost() {

        // Arrange
        Schedule schedule = new SimpleSchedule(2);
        schedule.addTask(new Task(0, 0, _graph.findByLabel("a")));
        schedule.addTask(new Task(0, 10, _graph.findByLabel("b")));
        schedule.addTask(new Task(1, 25, _graph.findByLabel("c")));

        // Act / Assert
        Assert.assertTrue(Validator.isValid(_graph, schedule));
    }

    // This tests that the isValid method correctly evaluates tasks across processors
    @Test
    public void testInvalidProcessorDependency() {

        // Arrange
        Schedule schedule = new SimpleSchedule(2);
        schedule.addTask(new Task(0, 0, _graph.findByLabel("a")));
        schedule.addTask(new Task(0, 10, _graph.findByLabel("b")));
        schedule.addTask(new Task(1, 15, _graph.findByLabel("c")));

        // Act / Assert
        Assert.assertFalse(Validator.isValid(_graph, schedule));
    }

    @Test
    public void testValidStringFormat() {

        // Arrange
        String inputString = "digraph \"outputOutTree-Balanced-MaxBf-3_Nodes_7_CCR_2.0_WeightType_Random\" {\n" +
                "\t0\t [Weight=5, Start=0, Processor=1];\n" +
                "\t1\t [Weight=6, Start=5, Processor=1];\n" +
                "\t0 -> 1\t [Weight=15];\n" +
                "\t2\t [Weight=5, Start=16, Processor=3];\n" +
                "\t0 -> 2\t [Weight=11];\n" +
                "\t3\t [Weight=6, Start=16, Processor=4];\n" +
                "\t0 -> 3\t [Weight=11];\n" +
                "\t4\t [Weight=4, Start=11, Processor=1];\n" +
                "\t1 -> 4\t [Weight=19];\n" +
                "\t5\t [Weight=7, Start=15, Processor=2];\n" +
                "\t1 -> 5\t [Weight=4];\n" +
                "\t6\t [Weight=7, Start=15, Processor=1];\n" +
                "\t1 -> 6\t [Weight=21];\n" +
                "}\n";

        // Act / Assert
        Assert.assertTrue(Validator.isValid(_complexGraph, inputString));

    }

    @Test
    public void testInvalidStringFormat() {

        // Arrange
        String inputString = "digraph \"outputOutTree-Balanced-MaxBf-3_Nodes_7_CCR_2.0_WeightType_Random\" {\n" +
                "\t0\t [Weight=5, Start=0, Processor=1];\n" +
                "\t1\t [Weight=6, Start=4, Processor=1];\n" +
                "\t0 -> 1\t [Weight=15];\n" +
                "\t2\t [Weight=5, Start=16, Processor=3];\n" +
                "\t0 -> 2\t [Weight=11];\n" +
                "\t3\t [Weight=6, Start=16, Processor=4];\n" +
                "\t0 -> 3\t [Weight=11];\n" +
                "\t4\t [Weight=4, Start=11, Processor=1];\n" +
                "\t1 -> 4\t [Weight=19];\n" +
                "\t5\t [Weight=7, Start=15, Processor=2];\n" +
                "\t1 -> 5\t [Weight=4];\n" +
                "\t6\t [Weight=7, Start=15, Processor=1];\n" +
                "\t1 -> 6\t [Weight=21];\n" +
                "}\n";

        // Act / Assert
        Assert.assertFalse(Validator.isValid(_complexGraph, inputString));

    }

}
