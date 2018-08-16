package algorithm.heuristics;

import algorithm.heuristics.pruner.Arborist;
import algorithm.heuristics.pruner.ProcessorOrderPruner;
import common.graph.Node;
import common.schedule.SimpleSchedule;
import common.schedule.Task;
import org.junit.Assert;
import org.junit.jupiter.api.Test;


public class ProcessorOrderPrunerTests {
    private Node nodeA = new Node(3, "a", 0);
    private Node nodeB = new Node(3, "b", 1);
    private Arborist arborist = new ProcessorOrderPruner();

    @Test
    public void correctOrderTest() {
        SimpleSchedule schedule = new SimpleSchedule(2);
        schedule.addTask(new Task(0, 0, nodeA));

        Assert.assertFalse(arborist.prune(null, schedule, new Task(1, 0, nodeB)));
    }

    @Test
    public void incorrectOrderTest() {
        SimpleSchedule schedule = new SimpleSchedule(2);
        schedule.addTask(new Task(0, 0, nodeB));

        Assert.assertTrue(arborist.prune(null, schedule, new Task(1, 0, nodeA)));
    }

    @Test
    public void correctWithMultipleTaskTest() {
        SimpleSchedule schedule = new SimpleSchedule(2);
        schedule.addTask(new Task(0, 0, nodeB));
        schedule.addTask(new Task(1, 0, new Node(1, "a", 3)));

        Assert.assertFalse(arborist.prune(null, schedule, new Task(1, 0, nodeA)));
    }

    @Test
    public void correctWithLaterSecondTaskTest() {
        SimpleSchedule schedule = new SimpleSchedule(2);
        schedule.addTask(new Task(0, 2, nodeA));

        Assert.assertFalse(arborist.prune(null, schedule, new Task(1, 4, nodeB)));
    }

    @Test
    public void incorrectWithLaterFirstTaskTest() {
        SimpleSchedule schedule = new SimpleSchedule(2);
        schedule.addTask(new Task(0, 2, nodeB));

        Assert.assertTrue(arborist.prune(null, schedule, new Task(1, 1, nodeA)));
    }
}
