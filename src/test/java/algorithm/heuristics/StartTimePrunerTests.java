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
        Pair<Processor, Task> taskPair = new Pair<>(null, new Task(1, new Node(1, "Stub")));

        //Act / Assert
        Assert.assertFalse(pruner.prune(null, schedule, taskPair));

    }

}
