import cli.Cli;
import cli.MilestoneOneCli;

public class Ainur {
    public static void main(String[] args) {
      Cli cli = new MilestoneOneCli(args);
      cli.parse();
    }
}
