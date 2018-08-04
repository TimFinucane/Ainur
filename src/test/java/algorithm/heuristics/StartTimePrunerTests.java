package algorithm.heuristics;

import common.categories.HobbitonTests;
import common.graph.Node;
import common.schedule.Processor;
import common.schedule.Schedule;
import common.schedule.Task;
import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(HobbitonTests.class)
public class StartTimePrunerTests {

    /**
     * Tests for base case of no current latest finishing time task in any processors. Should return false because
     * there are no other tasks to compare against.
     */
    @Test
    public void testEmptyGraph() {

        // Arrange
        Arborist pruner = new StartTimePruner();

        Schedule schedule = new Schedule(1);
        Task task = new Task(1, new Node(1, "Stub", 0));
        Pair<Processor, Task> processorTaskPair = new Pair<>(new Processor(), task);

        //Act / Assert
        Assert.assertFalse(pruner.prune(null, schedule, processorTaskPair));

    }

    /**
     * Tests for simple case of single processor, one current task and one task wanting to be added directly
     * after. Should return false.
     */
    @Test
    public void testSingleProcessorReturnFalse() {

        // Arrange
        Arborist pruner = new StartTimePruner();

        Schedule schedule = new Schedule(1);
        schedule.getProcessors().get(0).addTask(
                new Task(0, new Node(5, "stub", 0)));

        Task task =  new Task(5, new Node(1, "stub", 0));
        Pair<Processor, Task> processorTaskPair = new Pair<>(new Processor(), task);

        //Act / Assert
        Assert.assertFalse(pruner.prune(null, schedule, processorTaskPair));

    }

    /**
     * Tests for simple case of two processors, one current task and one task wanting to be added directly
     * after. Should return false.
     */
    @Test
    public void testMultipleProcessorsReturnFalse() {

        // Arrange
        Arborist pruner = new StartTimePruner();

        Schedule schedule = new Schedule(2);
        schedule.getProcessors().get(0).addTask(
                new Task(0, new Node(5, "stub", 0)));

        Task task =  new Task(5, new Node(1, "stub", 0));
        Pair<Processor, Task> processorTaskPair = new Pair<>(new Processor(), task);

        //Act / Assert
        Assert.assertFalse(pruner.prune(null, schedule, processorTaskPair));

    }

    /**
     * Tests for the case of two processors with one existing task and an further task being added whose starting time
     * (4) is before that of the current existing tasks start time (5). Should return true.
     */
    @Test
    public void testMultipleProcessorsReturnTrue() {

        // Arrange
        Arborist pruner = new StartTimePruner();

        Schedule schedule = new Schedule(2);
        schedule.getProcessors().get(0).addTask(
                new Task(5, new Node(5, "stub", 0)));

        Task task =  new Task(4, new Node(1, "stub", 0));
        Pair<Processor, Task> processorTaskPair = new Pair<>(new Processor(), task);

        //Act / Assert
        Assert.assertTrue(pruner.prune(null, schedule, processorTaskPair));

    }

    /**
     * Test for the case where multiple existing processors have the same start time (5), and add a further task
     * with start time less that all of them (4). Should return true.
     */
    @Test
    public void testMultipleProcessorsSameLastStartTimeReturnTrue() {

        // Arrange
        Arborist pruner = new StartTimePruner();

        Schedule schedule = new Schedule(10);
        for (int i = 0; i < 9; i++) {
            schedule.getProcessors().get(i).addTask(
                    new Task(5, new Node(5, "stub", 0)));
        }


        Task task =  new Task(4, new Node(1, "stub", 0));
        Pair<Processor, Task> processorTaskPair = new Pair<>(new Processor(), task);

        //Act / Assert
        Assert.assertTrue(pruner.prune(null, schedule, processorTaskPair));

    }

    /**
     * Tests for the case where there exists a task with the same start time as the task being added (both 5).
     * Should return false.
     */
    @Test
    public void testStartTimesEqualReturnFalse() {

        // Arrange
        Arborist pruner = new StartTimePruner();

        Schedule schedule = new Schedule(2);
        schedule.getProcessors().get(1).addTask(
                new Task(5, new Node(5, "stub", 0)));


        Task task =  new Task(5, new Node(1, "stub", 0));
        Pair<Processor, Task> processorTaskPair = new Pair<>(new Processor(), task);

        //Act / Assert
        Assert.assertFalse(pruner.prune(null, schedule, processorTaskPair));

    }

}
