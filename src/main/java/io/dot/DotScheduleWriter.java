package io.dot;

import common.graph.Graph;
import common.schedule.Schedule;
import common.schedule.Task;
import io.ScheduleWriter;

import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class writes a schedule to a .dot file format.
 */
public class DotScheduleWriter extends ScheduleWriter {

    /**
     * Constructor for ScheduleWriter
     * @param os The output stream to write to.
     */
    public DotScheduleWriter(OutputStream os) {
        super(os);
    }

    @Override
    /**
     * Method to write schedule out to .dot file. There is only one output file for a schedule,
     * but there can be multiple digraphs in the dot file ie. as many digraphs as there are
     * processors as each processors tasks goes in a separate digraph
     * @params schedule the schedule to be written out to file
     */
    public void write(Schedule schedule, Graph graph, InputStream is) {

        // Create StringBuilder from InputStream for injection
        String streamText = convertStreamToString(is);
        StringBuilder outputText = new StringBuilder(streamText);

        PrintWriter pw = new PrintWriter(_os); // Output writer

        // (?<=;|\{)            :       Positive lookbehind to check that either ';' or '{' appear before match
        // \s*(\w+)\s*          :       Check for any whitespace, a string of alpha numeric characters as group 1 the any whitespace
        // *\[Weight[^;]*()\]   :       Find '[Weight' followed by a non-semi-colon then followed by (empty) group 2 (for insertion)
        //                              followed by a '['. Check for no semi-colon ensures regex does not match multiple lines.
        // [^;]*;"              :       Check for any amount of non-semi-colons followed by a ';'. This is required to ensure match
        //                              does not occur across multiple lines.
        Pattern nodePattern = Pattern.compile("(?<=;|\\{)\\s*(\\w+)\\s*\\[Weight[^;]*()\\][^;]*;");
        Matcher m = nodePattern.matcher(streamText);

        int characterIndexDifference = 0; // Records total number of characters inserted into StringBuilder in line following while loop.

        while (m.find()) {
            String nodeName = m.group(1); // Node label
            Task task = schedule.findTask(graph.findByLabel(nodeName)); // Corresponding task to node label

            // Create injection string from start time of task and associated processor index
            String injectionString = String.format(", Start=%s, Processor=%s", task.getStartTime(), task.getProcessor() + 1);

            // Inject string into group 2. m.start(2) finds the beginning of group 2 and characterIndexDifference is added because of
            // increasing string length. As a result, spot we want to inject into gets continually 'bumped along' with each loop iteration.
            outputText.insert(m.start(2) + characterIndexDifference, injectionString);

            characterIndexDifference += injectionString.length(); // Increment variable by length of string we just injected.
        }

        // digraph\s*           :       Match 'digraph' followed by any amount of whitespace.
        // \"()[^\"]*\"         :       '"' followed by empty open group (for insertion) followed by any amount of not '"', followed
        //                              by a '"'. This is required to ensure a match does not occur across multiple lines.
        Pattern headerPattern = Pattern.compile("digraph\\s*\\\"()[^\\\"]*\\\"");
        m = headerPattern.matcher(outputText);

        m.find();
        int charIndex = m.start(1);

        // Set first character of graph name to upper case. 'digraph title {' -> 'digraph Title {'
        outputText.setCharAt(charIndex, Character.toUpperCase(outputText.charAt(charIndex)));
        outputText.insert(charIndex, "output"); // Inject 'output' into space just before graph title. 'Title' -> 'outputTitle'

        pw.write(outputText.toString());
        pw.close();
    }


    private String convertStreamToString(InputStream inputStream) {

        Scanner s = new Scanner(inputStream).useDelimiter("\\A");

        return s.hasNext() ? s.next() : "";

    }
}
