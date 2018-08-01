package cli;

import common.Config;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

public class MilestoneTwoCli extends Cli {
    protected boolean _visualise;
    protected int _cores;

    /**
     * @param args
     */
    public MilestoneTwoCli(String[] args) {
        super(args);
        _visualise = Config.VISUALISE_DEFAULT;
        _cores = Config.CORES_DEFAULT;
    }

    @Override
    protected void startScheduling() {

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
