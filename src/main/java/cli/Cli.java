package cli;

import common.Config;
import common.graph.Graph;
import common.schedule.Schedule;
import io.GraphReader;
import io.ScheduleWriter;
import io.dot.DotGraphReader;
import io.dot.DotScheduleWriter;
import org.apache.commons.cli.*;

import java.io.*;
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

    // MACROS
    private final String HELPER_HEADER;

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
            // Apache Commons CLI: Interrogation Stage
            this.interrogate(cmdLine);

            // Start the program
            Graph graph = this.readGraphFile(); // read the graph
            Schedule schedule = this.startScheduling(graph); // start scheduling
            this.writeSchedule(schedule); // write the schedule
        } catch (IOException i) {
            System.out.println("Sorry, we can't find the file you've supplied. Process terminated.");
            this.displayUsage();
        } catch (UnrecognizedOptionException u) {
            System.out.println("Please make sure your input arguments are in the appropriate format. Process terminated.");
        } catch (ParseException p) {
            p.printStackTrace();
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
     * An abstract method which determines what to do with the arguments once they have been processed.
     * This method should run a scheduling algorithm of the inheritors choice on the inputted graph.
     * Inheritors will also get the option to use their custom options how they wish.
     *
     * @param graph a graph representing the search space the scheduler will be called on.
     * @return A schedule obtained from the search space.
     */
    protected abstract Schedule startScheduling(Graph graph);

    /**
     * Reads a graph from the inputted dot file.
     *
     * @return A graph object created from the inputted dot file.
     * @throws FileNotFoundException if the .dot file could not be found
     */
    private Graph readGraphFile() throws FileNotFoundException {
        InputStream is = new FileInputStream(_inputFile);
        GraphReader graphReader = new DotGraphReader(is);
        return graphReader.read();
    }

    /**
     * Writes the schedule obtained from the scheduling algorithm to a dot file.
     *
     * @param schedule the schedule to write to the .dot file.
     * @throws FileNotFoundException
     */
    private void writeSchedule(Schedule schedule) {
        // Create a new file if file does not already exist
        try {
            File file = new File(_outputFile);
            file.createNewFile();
            OutputStream os = new FileOutputStream(file);

            // Write schedule to output file
            ScheduleWriter scheduleWriter = new DotScheduleWriter(os);
            scheduleWriter.write(schedule);

        } catch (IOException io) {
            System.out.println("Invalid filename entered, try run it again with a valid filename."
             + " Process terminated prematurely.");
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

        Option[] optionRef = cmdLine.getOptions();

        List<String> argList = cmdLine.getArgList();
        _inputFile = argList.get(0);

        try {
            _processors = Integer.parseInt(argList.get(1));
        } catch (NumberFormatException e) {
            System.out.println("Please make sure the number of processors is a positive, whole number.");
        }

        if (_processors == 0) {
            System.out.println("Sorry, we cannot allocate to zero processors. " +
                    "Please enter a positive integer value of processors");
            //TODO consider adding a custom exception to handle this type of error.
            System.exit(0);
        }

        if (cmdLine.hasOption("o")) {
            _outputFile = cmdLine.getOptionValue("o");
            if (!_outputFile.endsWith(".dot")){
                _outputFile += ".dot";
            }
            System.out.println("You instructed Ainur to output the schedule to a file called " + _outputFile);
        } else {
            int fileNameIndex = _inputFile.lastIndexOf("\\");
            _outputFile = _inputFile.substring(fileNameIndex+1, _inputFile.lastIndexOf('.')) + "-output.dot";
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

        options.addOption("o", true, "output file is named OUTPUT "
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
