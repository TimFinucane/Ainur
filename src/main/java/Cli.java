import common.Config;
import org.apache.commons.cli.*;

public class Cli {
    private String[] _args;
    boolean _visualise;
    int _cores;
    String _outputFile;

    public Cli(String[] args) {
        _args = args;
        _visualise = false;
        _cores = 1; // TODO default may change when this is implemented
    }

    public void parse() throws ParseException {
        // Apache Commons CLI: Definition Stage
        Options options = establishOptions();

        // Apache Commons CLI: Parsing Stage
        DefaultParser clParse = new DefaultParser();
        CommandLine cmdLine = clParse.parse(options, _args);

        // Apache Commons CLI: Interrogation Stage
        if (cmdLine.hasOption("h") || cmdLine.getArgList().size() < 2) {
            HelpFormatter helpFormatter =   new HelpFormatter();

            helpFormatter.printHelp(Config.APP_NAME, options);
            System.exit(0);
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
