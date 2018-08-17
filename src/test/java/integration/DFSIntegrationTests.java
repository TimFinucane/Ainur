package integration;

import algorithm.Algorithm;
import algorithm.DFSAlgorithm;
import algorithm.heuristics.lowerbound.CriticalPath;
import algorithm.heuristics.pruner.Arborist;
import algorithm.heuristics.pruner.ProcessorOrderPruner;
import algorithm.heuristics.pruner.StartTimePruner;
import common.Validator;
import common.graph.Graph;
import common.schedule.Schedule;
import io.GraphReader;
import io.dot.DotGraphReader;
import javafx.util.Pair;
import org.junit.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DFSIntegrationTests extends IntegrationTest {


    public DFSIntegrationTests() {
        // Get all files in data/SampleData/Input, override graphs for this to be the value
        File inputFolder = new File(String.valueOf(Paths.get("data", "SampleData", "Input")));
        graphs = Arrays.asList(inputFolder.list());
        for (int i = 0; i < graphs.size(); i++)
            graphs.set(i, String.valueOf(Paths.get("data", "SampleData", "Input")) + File.separator + graphs.get(i));

        // Make a list of lists with one pair in each corresponding to the number of processors and
        // optimal solution for that graph.
        File outputFolder = new File(String.valueOf(Paths.get("data", "SampleData", "Output")));
        List<String> outputFiles = Arrays.asList(outputFolder.list());
        List<List<Pair<Integer, Integer>>> optimalSchedulesReplacement = new ArrayList<>();

        for (String outputFileNameString : outputFiles) {

            InputStream is = null;
            try {
                is = new FileInputStream(String.valueOf(Paths.get("data", "SampleData", "Output")) + File.separator + outputFileNameString);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            int graphProcessorNo = scheduleProcessors(is);
            int graphOptimalSolution = scheduleLength(is);

            List<Pair<Integer, Integer>> listToAdd = new ArrayList<>();
            Pair<Integer, Integer> pair = new Pair<>(graphProcessorNo, graphOptimalSolution);
            listToAdd.add(pair);
            optimalSchedulesReplacement.add(listToAdd);
        }

        optimalSchedules = optimalSchedulesReplacement;
    }

    @Override
    protected void runAgainstOptimal(String graph, int processors, int optimalScheduleLength) {

        // Single threaded DFS implementation
        Algorithm dfsAlgorithm = new DFSAlgorithm(
                Arborist.combine(new StartTimePruner(), new ProcessorOrderPruner()),
                new CriticalPath()
        );


        GraphReader reader = null;
        try {
            reader = new DotGraphReader(new FileInputStream(graph));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Graph inputGraph = reader.read();

        dfsAlgorithm.run(inputGraph, processors);
        Schedule schedule = dfsAlgorithm.getCurrentBest();

        Assert.assertEquals(optimalScheduleLength, schedule.getEndTime());
        Assert.assertTrue(Validator.isValid(inputGraph, schedule));
    }


}
