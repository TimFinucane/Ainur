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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DotScheduleWriterTests {

    private Schedule _schedule;

    @Test
    public void testBasicSchedule(){
        // Set up
        Processor processor = new Processor();
        processor.addTask(new Task(1,
                new Node(1, "1")
        ));

        processor.addTask(new Task(2,
                new Node(1, "2")
        ));

        processor.addTask(new Task(3,
                new Node(1, "3")
        ));

        List<Processor> processorList = new ArrayList<>();
        processorList.add(processor);

        Schedule schedule = new Schedule(processorList);

        // Test
        ScheduleWriter dsw = new DotScheduleWriter(new OutputStream() {
            @Override
            public void write(int b) throws IOException {

            }
        });
        dsw.write(schedule);
    }
}
