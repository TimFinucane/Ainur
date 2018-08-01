package cli;

/**
 * A class implementing the abstract Cli class.
 * This class is to be used as the Cli for milestone one.
 * Extra command line args will be added in the milestone two CLI.
 */
public class MilestoneOneCli extends Cli {

    /**
     * Constructor responsible for assigning the args to a private field and assigning defaults.
     *
     * @param args a string array of arguments obtained from the command line.
     */
    public MilestoneOneCli(String[] args) {
        super(args);
    }

    @Override
    protected void startScheduling() {

    }
}
