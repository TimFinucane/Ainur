import org.apache.commons.cli.ParseException;

public class Ainur {
    public static void main(String[] args) {
        try {
            new Cli(args).parse();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
