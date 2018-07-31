import common.Config;
import org.apache.commons.cli.*;

public class Cli {
    private String[] _args;
    boolean _visualise;
    int _cores;
    String _outputFile;
    Options _options;

    public Cli(String[] args) {
        _args = args;
        _visualise = false;
        _cores = 1; // TODO default may change when this is implemented
        _options = establishOptions();
    }

    public void parse() throws ParseException {
        // Apache Commons CLI: Definition Stage

        // Apache Commons CLI: Parsing Stage
        DefaultParser clParse = new DefaultParser();
        CommandLine cmdLine = clParse.parse(_options, _args);

        // Apache Commons CLI: Interrogation Stage
        if (cmdLine.hasOption("h") || cmdLine.getArgList().size() < 2) {
            displayUsage();
        }
        if (cmdLine.hasOption("p")) {
            _cores = Integer.parseInt(cmdLine.getOptionValue("p"));
            System.out.println("You instructed Ainur to be executed using " + _cores + " cores");
            System.out.println("Unfortunately this feature is yet to be implemented... stay tuned");
        }
        if (cmdLine.hasOption("v")) {
            System.out.println("You instructed Ainur to visualise the scheduling process!");
            System.out.println("Unfortunately this feature is yet to be implemented... stay tuned");
        }
        if (cmdLine.hasOption("o")) {
            _outputFile = cmdLine.getOptionValue("o");
            System.out.println("You instructed Ainur to output the schedule to a file called " + _outputFile);
        }

        System.out.println(cmdLine.getArgList());
    }

    public void displayUsage() {
        HelpFormatter helpFormatter = new HelpFormatter();

        String customMessage = new StringBuilder()
                .append("java -jar " + Config.APP_NAME + ".jar INPUT.dot P [Option]")
                .append(String.format("\n\nINPUT.dot%9s%s", "", "a task graph with integer weights in dot format"))
                .append(String.format("\nP%17s%s", "", "number of processors to schedule the INPUT graph on"))
                .append("\n\nOptional:")
                .toString();

        helpFormatter.printHelp(customMessage, _options);
        System.exit(0);
    }

    public Options establishOptions() {
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
