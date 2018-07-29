package io;

import common.graph.Edge;
import common.graph.Node;
import common.schedule.Processor;
import common.schedule.Schedule;
import common.schedule.Task;
import io.dot.DotScheduleWriter;
import org.junit.Before;
import org.junit.Test;

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
        ScheduleWriter dsw = new DotScheduleWriter(new OutputStream() {
            @Override
            public void write(int b) throws IOException {}
        });
        dsw.write(schedule);

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
        ScheduleWriter dsw = new DotScheduleWriter(new OutputStream() {
            @Override
            public void write(int b) throws IOException {   }
        });
        dsw.write(schedule);
    }
}
