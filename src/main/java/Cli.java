import org.apache.commons.cli.*;

import java.util.List;

/**
 *
 */
public class Cli {
    // The array of strings received from the command line invocation
    private String[] _args;

    // The parameters extracted from the args array.
    protected boolean _visualise;
    protected int _cores;
    protected String _outputFile;
    protected Options _options;
    protected String _inputFile;
    protected int _processors;

    /**
     *
     * @param args
     */
    public Cli(String[] args) {
        // Initialise values
        _args = args;
        _visualise = Config.VISUALISE_DEFAULT;
        _cores = Config.CORES_DEFAULT;
        _processors = Config.PROCESSORS_DEFAULT;

        // Apache Commons CLI: Definition Stage
        _options = establishOptions();
    }

    /**
     *
     * @throws ParseException
     */
    public void parse() throws ParseException {
        // Apache Commons CLI: Parsing Stage
        DefaultParser clParse = new DefaultParser();
        CommandLine cmdLine = clParse.parse(_options, _args);

        // Apache Commons CLI: Interrogation Stage
        this.interrogate(cmdLine);
    }

    /**
     *
     */
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

    /**
     *
     * @param cmdLine
     */
    private void interrogate(CommandLine cmdLine) {
        // Apache Commons CLI: Interrogation Stage
        if (cmdLine.hasOption("h") || cmdLine.getArgList().size() < 2) {
            displayUsage();
        }

        Option[] optionRef = cmdLine.getOptions();

        List<String> argList = cmdLine.getArgList();
        _inputFile = argList.get(0);
        _processors = Integer.parseInt(argList.get(1));

        if (cmdLine.hasOption("p")) {
            _cores = Integer.parseInt(cmdLine.getOptionValue("p"));
            System.out.println("You instructed Ainur to be executed using " + _cores + " cores");
            System.out.println("Unfortunately this feature is yet to be implemented... stay tuned");
        }
        if (cmdLine.hasOption("v")) {
            _visualise = true;
            System.out.println("You instructed Ainur to visualise the scheduling process!");
            System.out.println("Unfortunately this feature is yet to be implemented... stay tuned");
        }
        if (cmdLine.hasOption("o")) {
            _outputFile = cmdLine.getOptionValue("o");
            System.out.println("You instructed Ainur to output the schedule to a file called " + _outputFile);
        } else {
            _outputFile = _inputFile.substring(0, _inputFile.lastIndexOf('.')) + "_processed.dot";
            System.out.println("Ainur output schedule file name defaulted to: " + _outputFile);
        }

        this.interrogateArgs(cmdLine);
    }

    /**
     *
     * @return
     */
    private Options establishOptions() {
        Options options = new Options();

        options.addOption("p", true, "use <arg> cores for execution in parallel "
                + "(default is sequential");
        options.addOption("v", "visualise", false, "visualise the search");
        options.addOption("o", true, "name of the outputted file. "
                + "(default is INPUT-output.dot)");
        options.addOption("h", "help", false, "show help");

        options.addOptionGroup(this.appendOptions());

        return options;
    }

    /**
     * Inheritors can override this method to provide extra options to the cli.
     * If extra options are added they should be handled in the interrogateArgs() method.
     * @return
     */
    protected OptionGroup appendOptions() {
        return new OptionGroup();
    }

    /**
     * Inheritors can override this method to handle any extra options added to the cli.
     * Extra options are added by overriding the appendOptions method.'
     * Inheritors may also extend functionality of existing options.
     * @param cmdLine
     */
    protected void interrogateArgs(CommandLine cmdLine) {

    }
}
