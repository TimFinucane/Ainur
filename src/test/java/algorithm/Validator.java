package algorithm;

import common.graph.Node;
import common.schedule.Processor;
import common.schedule.Schedule;
import common.schedule.Task;

import java.util.ArrayList;
import java.util.List;

public class Validator {

    public static boolean isValid(Schedule schedule) {

        List<Task> tasks = new ArrayList<>();

        for (Processor processor : schedule.getProcessors()) {
            for (Task task : processor.getTasks()) {
                tasks.add(task);
            }
        }



    }

}
