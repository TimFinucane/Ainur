package algorithm.heuristics;

import algorithm.heuristics.pruner.Arborist;
import algorithm.heuristics.pruner.StartTimePruner;
import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.SimpleSchedule;
import common.schedule.Task;
import org.junit.Assert;
import org.junit.jupiter.api.Test;


public class StartTimePrunerTests {

    /**
     * Tests for base case of no current latest finishing time task in any processors. Should return false because
     * there are no other tasks to compare against.
     */
    @Test
    public void testEmptyGraph() {
        // Arrange
        Arborist pruner = new StartTimePruner();

        Schedule schedule = new SimpleSchedule(1);
        Task task = new Task(0, 1, new Node(1, "Stub", 0));

        //Act / Assert
        Assert.assertFalse(pruner.prune(null, schedule, task));
    }

    /**
     * Tests for simple case of single processor, one current task and one task wanting to be added directly
     * after. Should return false.
     */
    @Test
    public void testSingleProcessorReturnFalse() {
        // Arrange
        Arborist pruner = new StartTimePruner();

        Schedule schedule = new SimpleSchedule(1);
        schedule.addTask(
                new Task(0, 0, new Node(5, "stub", 0)));

        Task task =  new Task(0, 5, new Node(1, "stub", 0));

        //Act / Assert
        Assert.assertFalse(pruner.prune(null, schedule, task));
    }

    /**
     * Tests for simple case of two processors, one current task and one task wanting to be added directly
     * after. Should return false.
     */
    @Test
    public void testMultipleProcessorsReturnFalse() {
        // Arrange
        Arborist pruner = new StartTimePruner();

        Schedule schedule = new SimpleSchedule(2);
        schedule.addTask(new Task(0, 0, new Node(5, "stub", 0)));

        Task task =  new Task(0, 5, new Node(1, "stub", 0));

        //Act / Assert
        Assert.assertFalse(pruner.prune(null, schedule, task));
    }

    /**
     * Tests for the case of two processors with one existing task and an further task being added whose starting time
     * (4) is before that of the current existing tasks start time (5). Should return true.
     */
    @Test
    public void testMultipleProcessorsReturnTrue() {
        // Arrange
        Arborist pruner = new StartTimePruner();

        Schedule schedule = new SimpleSchedule(2);
        schedule.addTask(new Task(0, 5, new Node(5, "stub", 0)));

        Task task =  new Task(0, 4, new Node(1, "stub", 0));

        //Act / Assert
        Assert.assertTrue(pruner.prune(null, schedule, task));
    }

    /**
     * Test for the case where multiple existing processors have the same start time (5), and add a further task
     * with start time less that all of them (4). Should return true.
     */
    @Test
    public void testMultipleProcessorsSameLastStartTimeReturnTrue() {
        // Arrange
        Arborist pruner = new StartTimePruner();

        Schedule schedule = new SimpleSchedule(10);
        for (int i = 0; i < 9; i++) {
            schedule.addTask(new Task(i, 5, new Node(5, "stub", 0)));
        }


        Task task = new Task(0, 4, new Node(1, "stub", 0));

        //Act / Assert
        Assert.assertTrue(pruner.prune(null, schedule, task));
    }

    /**
     * Tests for the case where there exists a task with the same start time as the task being added (both 5).
     * Should return false.
     */
    @Test
    public void testStartTimesEqualReturnFalse() {
        // Arrange
        Arborist pruner = new StartTimePruner();

        Schedule schedule = new SimpleSchedule(2);
        schedule.addTask(new Task(1, 5, new Node(5, "stub", 0)));

        Task task =  new Task(0, 5, new Node(1, "stub", 0));

        //Act / Assert
        Assert.assertFalse(pruner.prune(null, schedule, task));

    }

}
