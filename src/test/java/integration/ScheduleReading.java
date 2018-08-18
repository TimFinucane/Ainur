package integration;

import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScheduleReading {
    /**
     * Returns an integer value of the schedule length given an input stream of text in dot format. Attributes must be
     * specified in the form of [Processor=<>,Start=<>,Weight=<>]
     */
    public static int lengthFromFile(InputStream is) {

        Scanner s = new Scanner(is).useDelimiter("\\A");
        String inputTextAsString = s.hasNext() ? s.next() : "";
        s.close();

        Pattern taskPattern = Pattern.compile("Processor=(\\d+),Start=(\\d+),Weight=(\\d+)");
        Matcher m = taskPattern.matcher(inputTextAsString);

        int maxTaskEndTime = 0;
        // loop through all nodes looking for latest start time
        while (m.find()) {
            // Start time + Weight
            int taskEndTime = Integer.parseInt(m.group(2)) + Integer.parseInt(m.group(3));
            maxTaskEndTime = taskEndTime > maxTaskEndTime ? taskEndTime : maxTaskEndTime;

        }

        return maxTaskEndTime;
    }

    /**
     * Returns the number of processors used in a particular schedule.
     */
    public static int processorsFromFile(InputStream is) {

        Scanner s = new Scanner(is).useDelimiter("\\A");
        String inputTextAsString = s.hasNext() ? s.next() : "";
        s.close();

        Pattern taskPattern = Pattern.compile("Processor=(\\d+)");
        Matcher m = taskPattern.matcher(inputTextAsString);

        int maxProcessors = 0;
        while (m.find()) {
            int processorNo = Integer.parseInt(m.group(1));
            // As far as i can see processor count starts at 0, so add 1.
            maxProcessors = processorNo + 1 > maxProcessors ? processorNo + 1 : maxProcessors;

        }

        return maxProcessors;
    }
}
