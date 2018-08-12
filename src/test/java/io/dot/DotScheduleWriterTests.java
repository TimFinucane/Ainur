package io.dot;

import common.categories.HobbitonUnitTestsCategory;
import common.graph.Graph;
import common.schedule.SimpleSchedule;
import common.schedule.Task;
import io.ScheduleWriter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Class tests DotScheduleWriter by making schedules and getting the writer to write them out to file
 * or in this case, a string to compare with
 */
@Category(HobbitonUnitTestsCategory.class)
public class DotScheduleWriterTests {

    @Test
    /**
     * This tests one schedule can be written to file correctly
     */
    public void testBasicLinearOneProcessorSchedule(){

        // Set up
        Graph graph = new Graph.Builder()
                .node("1", 1)
                .node("2", 2)
                .node("3", 1)
                .build();

        SimpleSchedule schedule = new SimpleSchedule(1);
        schedule.addTask( new Task(0, 0, graph.getNodes().get(0)) );
        schedule.addTask( new Task(0, 2, graph.getNodes().get(1)) );
        schedule.addTask( new Task(0, 6, graph.getNodes().get(2)) );

        String str = "digraph \"graph\" {\n" +
                "\t1\t [Weight=1];\n" +
                "\t2\t [Weight=2];\n" +
                "\t1 -> 2\t [Weight=1];\n" +
                "\t3\t [Weight=1];\n" +
                "\t2 -> 3\t [Weight=2];\n" +
                "}\n\n";
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Test
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ScheduleWriter dsw = new DotScheduleWriter(bs);
        dsw.write(schedule, graph, is);

        // Assert
        String expected = "digraph \"outputGraph\" {\n" +
                "\t1\t [Weight=1, Start=0, Processor=1];\n" + // We expect processors to start at 1
                "\t2\t [Weight=2, Start=2, Processor=1];\n" +
                "\t1 -> 2\t [Weight=1];\n" +
                "\t3\t [Weight=1, Start=6, Processor=1];\n" +
                "\t2 -> 3\t [Weight=2];\n" +
                "}\n\n";

        Assert.assertEquals(expected, bs.toString());
    }

    @Test
    /**
     * This tests one schedule can be written to file correctly with node labels as multi character strings
     */
    public void testOneProcessotScheduleCharacterLabels(){

        // Set up
        Graph graph = new Graph.Builder()
                .node("heythenamespauline", 1)
                .node("giddaypaulineimsteph", 2)
                .node("guysguysjustchillout", 1)
                .build();

        SimpleSchedule schedule = new SimpleSchedule(1);
        schedule.addTask( new Task(0, 0, graph.getNodes().get(0)) );
        schedule.addTask( new Task(0, 2, graph.getNodes().get(1)) );
        schedule.addTask( new Task(0, 6, graph.getNodes().get(2)) );

        String str = "digraph \"graph\" {\n" +
                "\theythenamespauline\t [Weight=1];\n" +
                "\tgiddaypaulineimsteph\t [Weight=2];\n" +
                "\theythenamespauline -> giddaypaulineimsteph\t [Weight=1];\n" +
                "\tguysguysjustchillout\t [Weight=1];\n" +
                "\tgiddaypaulineimsteph -> guysguysjustchillout\t [Weight=2];\n" +
                "}\n\n";
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Test
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ScheduleWriter dsw = new DotScheduleWriter(bs);
        dsw.write(schedule, graph, is);

        // Assert
        String expected = "digraph \"outputGraph\" {\n" +
                "\theythenamespauline\t [Weight=1, Start=0, Processor=1];\n" + // We expect processors to start at 1
                "\tgiddaypaulineimsteph\t [Weight=2, Start=2, Processor=1];\n" +
                "\theythenamespauline -> giddaypaulineimsteph\t [Weight=1];\n" +
                "\tguysguysjustchillout\t [Weight=1, Start=6, Processor=1];\n" +
                "\tgiddaypaulineimsteph -> guysguysjustchillout\t [Weight=2];\n" +
                "}\n\n";

        Assert.assertEquals(expected, bs.toString());
    }


    @Test
    /**
     * This test tests multiple schedules can be written to file correctly
     */
    public void testMultiProcessorSchedule(){

        // Set up
        Graph graph = new Graph.Builder()
                .node("1", 1)
                .node("2", 2)
                .node("3", 1)
                .build();

        SimpleSchedule schedule = new SimpleSchedule(3);
        schedule.addTask( new Task(0, 0, graph.getNodes().get(0)) );
        schedule.addTask( new Task(1, 2, graph.getNodes().get(1)) );
        schedule.addTask( new Task(2, 6, graph.getNodes().get(2)) );

        String str = "digraph \"graph\" {\n" +
                "\t1\t [Weight=1];\n" +
                "\t2\t [Weight=2];\n" +
                "\t1 -> 2\t [Weight=1];\n" +
                "\t3\t [Weight=1];\n" +
                "\t2 -> 3\t [Weight=2];\n" +
                "}\n\n";
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Test
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ScheduleWriter dsw = new DotScheduleWriter(bs);
        dsw.write(schedule, graph, is);

        // Assert
        String expected = "digraph \"outputGraph\" {\n" +
                "\t1\t [Weight=1, Start=0, Processor=1];\n" + // We expect processors to start at 1
                "\t2\t [Weight=2, Start=2, Processor=2];\n" +
                "\t1 -> 2\t [Weight=1];\n" +
                "\t3\t [Weight=1, Start=6, Processor=3];\n" +
                "\t2 -> 3\t [Weight=2];\n" +
                "}\n\n";

        Assert.assertEquals(expected, bs.toString());
    }
}
