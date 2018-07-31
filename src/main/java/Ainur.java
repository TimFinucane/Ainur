import common.Config;
import org.apache.commons.cli.*;

import java.io.PrintWriter;

public class Ainur {
    public static void main(String[] args) throws ParseException {
        boolean visualise = false; // TODO var will be used when visualisations are implemented
        int cores = 1; // TODO update this var and how it is used for multiprocessing
        String outputFile = null;

        // Apache Commons CLI: Definition Stage
        Options options = establishOptions();

        // Apache Commons CLI: Parsing Stage
        DefaultParser clParse = new DefaultParser();
        CommandLine cmdLine = clParse.parse(options, args);

        // Apache Commons CLI: Interrogation Stage
        if (cmdLine.hasOption("h") || cmdLine.getArgList().size() < 2) {
            HelpFormatter helpFormatter =   new HelpFormatter();

            helpFormatter.printHelp(Config.APP_NAME, options);
            System.exit(0);
        }
        if (cmdLine.hasOption("p")) {
            cores = Integer.parseInt(cmdLine.getOptionValue("p"));

            System.out.println("You instructed Ainur to be executed using " + cores + " cores");
            System.out.println("Unfortunately this feature is yet to be implemented... stay tuned");
        }
        if (cmdLine.hasOption("v")) {
            System.out.println("You instructed Ainur to visualise the scheduling process!");
            System.out.println("Unfortunately this feature is yet to be implemented... stay tuned");
        }
        if (cmdLine.hasOption("o")) {
            outputFile = cmdLine.getOptionValue("o");
            System.out.println("You instructed Ainur to output the schedule to a file called " + outputFile);
        }

        System.out.println(cmdLine.getArgList());
    }

    public static Options establishOptions() {
        Options options = new Options();

        options.addOption("p", true, "use <arg> cores for execution in parallel "
                + "(default is sequential");
        options.addOption("v", "visualise", false, "visualise the search");
        options.addOption("o", true, "name of the outputted file. "
                + "(default is INPUT-output.dot)");
        options.addOption("h", "help", false, "show help");

        return options;
    }
}
