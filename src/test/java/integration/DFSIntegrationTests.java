package integration;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class DFSIntegrationTests extends IntegrationTest {


    public DFSIntegrationTests() {
        // Get all files in data/SampleData/Input, override graphs for this to be the value
        File folder = new File(String.valueOf(Paths.get("data", "SampleData", "Input")));
        graphs = Arrays.asList(folder.list());

        // get optimal schedules from data/SampleData/Output

    }

    @Override
    protected void runAgainstOptimal(String graph, int processors, int optimalScheduleLength) {



    }


}
