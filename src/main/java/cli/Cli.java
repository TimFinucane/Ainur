package cli;

import common.Config;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.List;
import java.util.regex.Pattern;

/**
 * An abstract class used for processing command line arguments.
 * Inheritors can easily add arguments. And control how to program works.
 */
public class Cli {


    // The array of strings received from the command line invocation
    private String[] _args;

    // The parameters extracted from the args array.
    protected String _outputFile;
    protected String _inputFile;
    protected int _processors;

    // Collection of command line options
    protected Options _options;

    // Fields required for milestone two.
    protected boolean _visualise;
    protected int _cores;

    // MACROS
    private final String HELPER_HEADER;

    public String getOutputFile() {
        return _outputFile;
    }

    public String getInputFile() {
        return _inputFile;
    }

    public int getProcessors() {
        return _processors;
    }


    /**
     * Constructor responsible for assigning the args to a private field and assigning defaults.
     *
     * @param args a string array of arguments obtained from the command line.
     */
    public Cli(String[] args) {
        // Initialise values
        _args = args;
        _processors = Config.PROCESSORS_DEFAULT;
        _visualise = Config.VISUALISE_DEFAULT;
        _cores = Config.CORES_DEFAULT;

        // Apache Commons CLI: Definition Stage
        _options = establishOptions();

        HELPER_HEADER = new StringBuilder()
                .append("java -jar " + Config.APP_NAME + ".jar INPUT.dot P [Option]")
                .append(String.format("\n\nINPUT.dot%9s%s", "", "a task graph with integer weights in dot format"))
                .append(String.format("\nP%17s%s", "", "number of processors to schedule the INPUT graph on"))
                .append("\n\nOptional:")
                .toString();
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
            interrogate(cmdLine); // Apache Commons CLI: Interrogation Stage

        } catch (UnrecognizedOptionException u) {

            System.out.println("Please make sure your input arguments are in the appropriate format. Process terminated.");

        } catch (ParseException p) {

            p.printStackTrace();
        }
    }


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

        List<String> argList = cmdLine.getArgList();
        _inputFile = argList.get(0);

        try {
            _processors = Integer.parseInt(argList.get(1));
        } catch (NumberFormatException e) { // Can't parse the number of processors
            System.out.println("Please make sure the number of processors is a positive, whole number.");
        }


        if (_processors == 0) {
            System.out.println("Sorry, we cannot allocate to zero processors. Please enter a positive integer value of processors");
            //TODO consider adding a custom exception to handle this type of error.
            System.exit(0);
        }


        String[] outputPathComponents = _inputFile.split(Pattern.quote(File.separator)); // split on file separator, Pattern.quote() because regex grr
        StringBuilder outputPath = new StringBuilder();

        for (int i = 0; i < outputPathComponents.length - 1; i++) { // Take all path components except for last (i.e. the input file name)
            outputPath.append(outputPathComponents[i]).append(File.separator);
        }

        if (cmdLine.hasOption("o")) {
            _outputFile = outputPath.toString() + cmdLine.getOptionValue("o");

            _outputFile = _outputFile.endsWith(".dot") ? _outputFile : (_outputFile += ".dot"); // Append .dot on end if user didn't supply

            System.out.println("You instructed Ainur to output the schedule to the file: " + _outputFile);
        } else {
            String outputFileName = outputPathComponents[outputPathComponents.length - 1];
            String[] splitOutputFileName = outputFileName.split(Pattern.quote("."));

            _outputFile = outputPath.toString() + splitOutputFileName[0] + "-output.dot";

            System.out.println("Ainur output schedule file defaulted to: " + _outputFile);
        }


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
    }


    /**
     * A method for displaying the way the CLI should be used.
     * This is called whenever the user uses the CLI incorrectly or uses the help flag ("-h" or "--help")
     */
    public void displayUsage() {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(HELPER_HEADER, _options);
        System.exit(0);
    }


    /**
     * Sets up the optional arguments the CLI takes.
     * The optional arguments available can be extended by overriding the appendOptions method.
     *
     * @return the Options object containing the set of options the CLI takes.
     */
    private Options establishOptions() {
        Options options = new Options();

        options.addOption("o", true, "output file is named OUTPUT "
                + "(default is INPUT-output.dot)");
        options.addOption("h", "help", false, "show help");

        options.addOption(new Option("p", true, "use <arg> cores for execution in parallel "
                + "(default is sequential"));

        options.addOption(new Option("v", "visualise", false, "visualise the search"));

        return options;
    }
}