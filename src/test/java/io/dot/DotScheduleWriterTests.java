package io.dot;

import common.categories.HobbitonUnitTestsCategory;
import common.graph.Node;
import common.schedule.Processor;
import common.schedule.Schedule;
import common.schedule.Task;
import io.ScheduleWriter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayOutputStream;

/**
 * Class tests DotScheduleWriter by making schedules and getting the writer to write them out to file
 * or in this case, a string to compare with
 */
@Category(HobbitonUnitTestsCategory.class)
public class DotScheduleWriterTests {

    @Test
    /**
     * This test tests is one schedule can be written to file correctly
     */
    public void testBasicLinearOneProcessorSchedule(){
        // Set up
        Schedule schedule = new Schedule(1);

        Processor processor = schedule.getProcessors().get(0);

        processor.addTask(new Task(0,
                new Node(1, "1", 1)
        ));

        processor.addTask(new Task(2,
                new Node(2, "2", 2)
        ));

        processor.addTask(new Task(6,
                new Node(1, "3", 3)
        ));

        // Test
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ScheduleWriter dsw = new DotScheduleWriter(bs);
        dsw.write(schedule);

        // Assert
        String expected =
                "digraph \"Processor_0\" {\n" +
                        "\t1\t [Weight=1];\n" +
                        "\t2\t [Weight=2];\n" +
                        "\t1 -> 2\t [Weight=1];\n" +
                        "\t3\t [Weight=1];\n" +
                        "\t2 -> 3\t [Weight=2];\n" +
                        "}\n\n";
        Assert.assertEquals(expected, bs.toString());
    }

    @Test
    /*
     * This test tests if more than one schedule ie. more than one processor can be written out to file
     */
    public void testBasicLinearTwoProcessorSchedule(){

        Schedule schedule = new Schedule(2);

        Processor processor1 = schedule.getProcessors().get(0);
        //Set up
        processor1.addTask(new Task(0,
                new Node(2, "1", 1)
        ));
        processor1.addTask(new Task(4,
                new Node(3, "2", 2)
        ));
        processor1.addTask(new Task(9,
                new Node(1, "3", 3)
        ));

        Processor processor2 = schedule.getProcessors().get(1);

        processor2.addTask(new Task(1,
                new Node(2, "4", 4)
        ));
        processor2.addTask(new Task(4,
                new Node(2, "5", 5)
        ));
        processor2.addTask(new Task(8,
                new Node(1, "6", 6)
        ));

        // Test
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ScheduleWriter dsw = new DotScheduleWriter(bs);
        dsw.write(schedule);

        // Assert
        String expected =
                "digraph \"Processor_0\" {\n" +
                        "\t1\t [Weight=2];\n" +
                        "\t2\t [Weight=3];\n" +
                        "\t1 -> 2\t [Weight=2];\n" +
                        "\t3\t [Weight=1];\n" +
                        "\t2 -> 3\t [Weight=2];\n" +
                        "}\n" +
                        "\n" +
                        "digraph \"Processor_1\" {\n" +
                        "\t4\t [Weight=2];\n" +
                        "\t5\t [Weight=2];\n" +
                        "\t4 -> 5\t [Weight=1];\n" +
                        "\t6\t [Weight=1];\n" +
                        "\t5 -> 6\t [Weight=2];\n" +
                        "}\n\n";
        Assert.assertEquals(expected, bs.toString());
    }
}
