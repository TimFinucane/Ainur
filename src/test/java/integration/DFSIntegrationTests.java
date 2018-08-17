package integration;

import algorithm.Algorithm;
import javafx.util.Pair;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DFSIntegrationTests extends IntegrationTest {


    public DFSIntegrationTests() {
        // Get all files in data/SampleData/Input, override graphs for this to be the value
        File folder = new File(String.valueOf(Paths.get("data", "SampleData", "Input")));
        graphs = Arrays.asList(folder.list());

        // Make a list of lists with one pair in each corresponding to the number of processors and
        // optimal solution for that graph.
        List<List<Pair<Integer, Integer>>> optimalSchedulesReplacement = new ArrayList<>();
        for (String graphString : graphs) {
            int graphProcessorNo = scheduleProcessors(graphString);
            int graphOptimalSolution = scheduleLength(graphString);

            List<Pair<Integer, Integer>> listToAdd = new ArrayList<>();
            listToAdd.add(new Pair<>(graphProcessorNo, graphOptimalSolution));
            optimalSchedulesReplacement.add(listToAdd);
        }

        optimalSchedules = optimalSchedulesReplacement;
    }

    @Override
    protected void runAgainstOptimal(String graph, int processors, int optimalScheduleLength) {


//        Algorithm dfsAlgorithm = new
        

    }


}
