package cli;

import common.Config;
import org.apache.commons.cli.*;

import java.util.List;

/**
 * An abstract class used for processing command line arguments.
 * Inheritors can easily add arguments. And control how to program works.
 */
public abstract class Cli {
    // The array of strings received from the command line invocation
    private String[] _args;

    // The parameters extracted from the args array.
    protected String _outputFile;
    protected String _inputFile;
    protected int _processors;

    // Collection of command line options
    protected Options _options;

    /**
     * Constructor responsible for assigning the args to a private field and assigning defaults.
     *
     * @param args a string array of arguments obtained from the command line.
     */
    public Cli(String[] args) {
        // Initialise values
        _args = args;
        _processors = Config.PROCESSORS_DEFAULT;

        // Apache Commons CLI: Definition Stage
        _options = establishOptions();
    }

    /**
     * Interprets the command line arguments and uses them accordingly.
     */
    public void parse() {
        // Apache Commons CLI: Parsing Stage
        DefaultParser clParse = new DefaultParser();
        CommandLine cmdLine = null;
        try {
            cmdLine = clParse.parse(_options, _args);
            // Apache Commons CLI: Interrogation Stage
            this.interrogate(cmdLine);

            // Start Scheduling
            this.startScheduling();
        } catch (ParseException e) {
            e.printStackTrace();
            this.displayUsage();
        }
    }

    /**
     * A method for displaying the way the CLI should be used.
     * This is called whenever the user uses the CLI incorrectly or uses the help flag ("-h" or "--help")
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
     * An abstract method which determines what to do with the arguments once they have been processed.
     */
    protected abstract void startScheduling();

    /**
     * Converts the arguments to fields so they can be used as intended.
     *
     * @param cmdLine the CommandLine object used to gather the args and options.
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
     * Sets up the optional arguments the CLI takes.
     * The optional arguments available can be extended by overriding the appendOptions method.
     *
     * @return the Options object containing the set of options the CLI takes.
     */
    private Options establishOptions() {
        Options options = new Options();

        options.addOption("o", true, "name of the outputted file. "
                + "(default is INPUT-output.dot)");
        options.addOption("h", "help", false, "show help");

        // Add any options added by inheritors
        options = this.appendOptions(options);

        return options;
    }

    /**
     * Inheritors can override this method to provide extra options to the cli.
     * If extra options are added they should be handled in the interrogateArgs() method.
     * If you don't wish to add options will just return the options param.
     *
     * @param  options the list of options the CLI takes.
     * @return The list of options the CLI takes with any extra options appended.
     */
    protected Options appendOptions(Options options) {
        return options;
    }

    /**
     * Inheritors can override this method to handle any extra options added to the cli.
     * Extra options are added by overriding the appendOptions method.
     * Inheritors may also extend functionality of existing options.
     *
     * @param cmdLine the CommandLine object used to gather the args and options.
     */
    protected void interrogateArgs(CommandLine cmdLine) {}
}
