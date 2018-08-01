import cli.Cli;
import cli.MilestoneTwoCli;

public class Ainur {
    public static void main(String[] args) {
      Cli cli = new MilestoneTwoCli(args);
      cli.parse();
    }
}
