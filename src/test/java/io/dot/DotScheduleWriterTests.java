package io.dot;

import common.categories.HobbitonUnitTestsCategory;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.SimpleSchedule;
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

    /*@Test
    /**
     * This test tests is one schedule can be written to file correctly
     */
    /*public void testBasicLinearOneProcessorSchedule(){
        // Set up
        SimpleSchedule schedule = new SimpleSchedule(1);

        schedule.addTask( new Task(0, 0, new Node(1, "1", 1)) );
        schedule.addTask( new Task(0, 2, new Node(2, "2", 2)) );
        schedule.addTask( new Task(0, 6, new Node(1, "3", 3)) );

        Graph graph = new Graph.Builder().name("test").build();

        // Test
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ScheduleWriter dsw = new DotScheduleWriter(bs);
        dsw.write(schedule, graph);

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
    }*/

    //@Test
    /*
     * This test tests if more than one schedule ie. more than one processor can be written out to file
     */
    /*public void testBasicLinearTwoProcessorSchedule(){

        SimpleSchedule schedule = new SimpleSchedule(2);
        //Set up
        schedule.addTask( new Task(0, 0, new Node(2, "1", 1)) );
        schedule.addTask( new Task(0, 4, new Node(3, "2", 2)) );
        schedule.addTask( new Task(0, 9, new Node(1, "3", 3)) );

        schedule.addTask( new Task(1, 1, new Node(2, "4", 4)) );
        schedule.addTask( new Task(1, 4, new Node(2, "5", 5)) );
        schedule.addTask( new Task(1, 8, new Node(1, "6", 6)) );

        Graph graph = new Graph.Builder().name("test").build();

        // Test
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ScheduleWriter dsw = new DotScheduleWriter(bs);
        dsw.write(schedule, graph);

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
    }*/
}
