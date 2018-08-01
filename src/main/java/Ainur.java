import org.apache.commons.cli.ParseException;

public class Ainur {
    public static void main(String[] args) {
      Cli cli = new Cli(args);
        try {
            cli.parse();
        } catch (ParseException e) {
            e.printStackTrace();
            cli.displayUsage();
        }
    }
}
