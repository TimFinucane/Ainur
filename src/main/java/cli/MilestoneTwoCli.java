package cli;

import common.Config;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class MilestoneTwoCli extends Cli {
    // Fields required for milestone two.
    protected boolean _visualise;
    protected int _cores;

    /**
     * Constructor responsible for assigning the args to a private field and assigning defaults.
     * Adds default args to the visualise and cores options.
     *
     * @param args a string array of arguments obtained from the command line.
     */
    public MilestoneTwoCli(String[] args) {
        super(args);
        _visualise = Config.VISUALISE_DEFAULT;
        _cores = Config.CORES_DEFAULT;
    }

    @Override
    protected void startScheduling() {
        // TODO
    }

    @Override
    protected Options appendOptions(Options options) {
        options.addOption(new Option("p", true, "use <arg> cores for execution in parallel "
                + "(default is sequential"));
        options.addOption(new Option("v", "visualise", false, "visualise the search"));
        return options;
    }

    @Override
    protected void interrogateArgs(CommandLine cmdLine) {
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
}
