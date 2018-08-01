package algorithm.heuristics;

import common.graph.Node;
import common.schedule.Processor;
import common.schedule.Schedule;
import common.schedule.Task;
import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Test;

public class StartTimePrunerTests {

    @Test
    public void testEmptyGraph() {

        // Arrange
        Arborist pruner = new StartTimePruner();

        Schedule schedule = new Schedule(1);
        Task task =  new Task(1, new Node(1, "Stub"));

        //Act / Assert
        Assert.assertFalse(pruner.prune(null, schedule, task));

    }

    @Test
    public void testSingleProcessorReturnFalse() {

        // Arrange
        Arborist pruner = new StartTimePruner();

        Schedule schedule = new Schedule(1);
        schedule.getProcessors().get(0).addTask(
                new Task(0, new Node(5, "stub")));

        Task task =  new Task(5, new Node(1, "stub"));

        //Act / Assert
        Assert.assertFalse(pruner.prune(null, schedule, task));

    }

    @Test
    public void testMultipleProcessorsReturnFalse() {

        // Arrange
        Arborist pruner = new StartTimePruner();

        Schedule schedule = new Schedule(2);
        schedule.getProcessors().get(0).addTask(
                new Task(0, new Node(5, "stub")));

        Task task =  new Task(5, new Node(1, "stub"));

        //Act / Assert
        Assert.assertFalse(pruner.prune(null, schedule, task));

    }

}
