package integration.tests.repeatable.test;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class RepeatedTestRule implements TestRule
{
    @Override
    public Statement apply(Statement statement, Description description)
    {
        Statement result = statement;
        RepeatTest repeat = description.getAnnotation(RepeatTest.class);
        if(repeat != null)
        {
            int times = repeat.times();
            result = new RepeatableTestStatement(times, statement);
        }
        return result;
    }
}