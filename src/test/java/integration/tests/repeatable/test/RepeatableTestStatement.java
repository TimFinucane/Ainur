package integration.tests.repeatable.test;


import org.junit.runners.model.Statement;

/**
 * Custome class for JUnit annotation to run the same test multiple times
 */
public class RepeatableTestStatement extends Statement {

    private final int times;
    private final Statement statement;

    public RepeatableTestStatement(int times, Statement statement)
    {
        this.times = times;
        this.statement = statement;
    }

    /**
     * Method runs the same test multiple times
     * @throws Throwable
     */
    @Override
    public void evaluate() throws Throwable
    {
        for(int i = 0; i < times; i++)
        {
            statement.evaluate();
            System.out.println("Test " + (i+1));
        }
    }

}
