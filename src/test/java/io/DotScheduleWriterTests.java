package io;

import common.graph.Edge;
import common.graph.Node;
import common.schedule.Processor;
import common.schedule.Schedule;
import common.schedule.Task;
import io.dot.DotScheduleWriter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DotScheduleWriterTests {

    @Test
    public void testBasicLinearOneProcessorSchedule(){
        // Set up
        Processor processor = new Processor();
        processor.addTask(new Task(0,
                new Node(1, "1")
        ));

        processor.addTask(new Task(2,
                new Node(2, "2")
        ));

        processor.addTask(new Task(6,
                new Node(1, "3")
        ));

        List<Processor> processorList = new ArrayList<>();
        processorList.add(processor);

        Schedule schedule = new Schedule(processorList);

        // Test
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ScheduleWriter dsw = new DotScheduleWriter(bs);
        dsw.write(schedule);

        // Assert
        String expected =
                "digraph \"Processor#0\" {\n" +
                        "\t1\t [Weight=1];\n" +
                        "\t2\t [Weight=2];\n" +
                        "\t1 -> 2\t [Weight=1];\n" +
                        "\t3\t [Weight=1];\n" +
                        "\t2 -> 3\t [Weight=2];\n" +
                        "}\n\n";
        Assert.assertEquals(expected, bs.toString());
    }

    @Test
    public void testBasicLinearTwoProcessorSchedule(){
        List<Processor> processorList = new ArrayList<>();
        Processor processor1 = new Processor();
        processor1.addTask(new Task(0,
                new Node(2, "1")
        ));
        processor1.addTask(new Task(4,
                new Node(3, "2")
        ));
        processor1.addTask(new Task(9,
                new Node(1, "3")
        ));
        processorList.add(processor1);

        Processor processor2 = new Processor();
        processor2.addTask(new Task(1,
                new Node(2, "4")
        ));
        processor2.addTask(new Task(4,
                new Node(2, "5")
        ));
        processor2.addTask(new Task(8,
                new Node(1, "6")
        ));
        List<Processor> processorList2 = new ArrayList<>();
        processorList.add(processor2);

        Schedule schedule = new Schedule(processorList);

        // Test
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ScheduleWriter dsw = new DotScheduleWriter(bs);
        dsw.write(schedule);

        // Assert
        String expected =
                "digraph \"Processor#0\" {\n" +
                        "\t1\t [Weight=2];\n" +
                        "\t2\t [Weight=3];\n" +
                        "\t1 -> 2\t [Weight=2];\n" +
                        "\t3\t [Weight=1];\n" +
                        "\t2 -> 3\t [Weight=2];\n" +
                        "}\n" +
                        "\n" +
                        "digraph \"Processor#1\" {\n" +
                        "\t4\t [Weight=2];\n" +
                        "\t5\t [Weight=2];\n" +
                        "\t4 -> 5\t [Weight=1];\n" +
                        "\t6\t [Weight=1];\n" +
                        "\t5 -> 6\t [Weight=2];\n" +
                        "}\n\n";
        Assert.assertEquals(expected, bs.toString());
    }
}
