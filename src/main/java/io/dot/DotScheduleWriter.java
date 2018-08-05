package io.dot;

import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.Task;
import io.ScheduleWriter;

import javax.sql.rowset.serial.SerialStruct;
import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class writes a schedule to a .dot file format.
 */
public class DotScheduleWriter extends ScheduleWriter {

    private final String COMMUNICATION_COST_FORMAT = "\t%s -> %s\t [Weight=%d];\n";
    private final String COMPUTATION_COST_FORMAT = "\t%s\t [Weight=%d, Start=%d, Processor=%d];\n";
    private final String DOT_GRAPH_OPENING = "digraph \"output%s\" {\n";
    private final String DOT_GRAPH_CLOSING = "}\n\n";

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

        String streamText = convertStreamToString(is);
        StringBuilder outputText = new StringBuilder(streamText);

        PrintWriter pw = new PrintWriter(_os);

        Pattern nodePattern = Pattern.compile("(?<=;|\\{)\\s*(\\w+)\\s*\\[[^;]*()\\][^;]*;");
        Matcher m = nodePattern.matcher(streamText);

        int characterIndexDifference = 0;

        while (m.find()) {
            String nodeName = m.group(1);
            Node node = graph.findByLabel(nodeName);

            Task task = schedule.findTask(node);
            String injectionString = String.format(", Start=%s, Processor=%s", task.getStartTime(), task.getProcessor());

            outputText.insert(m.start(2) + characterIndexDifference, injectionString);

            characterIndexDifference += injectionString.length();
        }

        Pattern headerPattern = Pattern.compile("digraph\\s*\\\"()[^\\\"]*\\\"");
        m = headerPattern.matcher(outputText);

        m.find();
        int charIndex = m.start(1);

        outputText.setCharAt(charIndex, Character.toUpperCase(outputText.charAt(charIndex)));
        outputText.insert(charIndex, "output");

        pw.write(outputText.toString());
        pw.close();
    }


    private String convertStreamToString(InputStream inputStream) {

        Scanner s = new Scanner(inputStream).useDelimiter("\\A");

        return s.hasNext() ? s.next() : "";

    }
}
