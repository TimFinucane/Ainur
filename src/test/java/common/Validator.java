package common;

import common.categories.HobbitonUnitTestsCategory;
import common.graph.Edge;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.SimpleSchedule;
import common.schedule.Task;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Validator {

    /**
     * Returns validity based on an output schedule and it's corresponding graph
     * @param graph
     * @param schedule
     * @return isValid : boolean
     */
    public static boolean isValid(Graph graph, Schedule schedule) {

        return validOrder(graph, schedule) && validPlacement(schedule);

    }

    // Check for dependent nodes of tasks and ensure their tasks finish after their parent task is
    // finished plus any necessary transmission time.
    private static boolean validOrder(Graph graph, Schedule schedule) {

        for (int processor = 0; processor < schedule.getNumProcessors(); ++processor) {
            for (Task task : schedule.getTasks(processor)) {
                // Loop through all outgoing edges of the graph
                for (Edge edge : graph.getOutgoingEdges(task.getNode())) {

                    Node node = edge.getDestinationNode();
                    Task dependentTask = schedule.findTask(node);

                    // Check if tasks are on the same processor
                    if (dependentTask.getProcessor() == processor) {
                        // Check if dependentTask's start time comes before the task's end time
                        if (dependentTask.getStartTime() < task.getEndTime()) {
                            return false; // Invalid
                        }
                    } else {
                        /// Check if dependentTask's stert time comes before the task's end time plus transmission cost
                        if (dependentTask.getStartTime() < task.getEndTime() + edge.getCost()) {
                            return false; // Invalid
                        }
                    }
                }
            }
        }

        return true;
    }

    // Check that the processors do not feature tasks that overlap.
    private static boolean validPlacement(Schedule schedule) {

        //Check that Tasks do not overlap on a processor
        for (int processor = 0; processor < schedule.getNumProcessors(); ++processor) {
            List<Task> tasks = schedule.getTasks(processor);

            for (Task task : tasks) {
                for (Task otherTask : tasks) {

                    // If either the start time of the other task lies in between the start and end time of the set task
                    // or the end time of the other task lies in between the start and finish of the set task.
                    if ((otherTask.getStartTime() < task.getEndTime() && otherTask.getStartTime() > task.getStartTime()) ||
                            (otherTask.getEndTime() < task.getEndTime() && otherTask.getEndTime() > task.getStartTime())) {

                        return false;
                    }

                }
            }
        }

        return true;
    }


    /**
     * Returns validity based on a string representing an output schedule in .dot format and it's corresponding graph.
     * String must be in the form of <nodeLabel> [Weight=<weight>, Start=<start>, Processor=<processor>];
     * @param graph
     * @param scheduleAsText
     * @return isValid : boolean
     */
    public static boolean isValid(Graph graph, String scheduleAsText) {

        List<Task> tasks = new ArrayList<>();

        Pattern taskPattern = Pattern.compile("(?<=;|^|\\{)\\s*(\\w+)\\s*\\[\\s*Weight\\s*=\\s*(\\d+)\\s*,\\s*Start\\s*=\\s*(\\d+)\\s*,\\s*Processor\\s*=\\s*(\\d+)\\s*]\\s*;");
        Matcher m = taskPattern.matcher(scheduleAsText);

        int maxProcessorNo = 0;

        while (m.find()) {
            Task task = new Task(Integer.parseInt(m.group(4)) - 1, // Processor index start at 1 in text
                    Integer.parseInt(m.group(3)), //Start time
                    graph.findByLabel(m.group(1))); // Corresponding node object

            tasks.add(task);

            // If larger processor index found increment number of processors
            maxProcessorNo = Integer.parseInt(m.group(4)) > maxProcessorNo ? Integer.parseInt(m.group(4)) : maxProcessorNo;
        }

        Schedule schedule = new SimpleSchedule(maxProcessorNo); // Create schedule with the max. number of found processors

        Collections.sort(tasks, Comparator.comparingInt(Task::getStartTime)); // This line is pretty sick: Order tasks by start time so that scheduler doesn't complain

        for (Task task : tasks) {
            schedule.addTask(task); // Populate schedule with tasks from the string
        }

        // Call above method with schedule parameter
        return isValid(graph, schedule);

    }

}
