package integration;

import javafx.util.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.*;

public class GraphSet {
    /* Just in case you still want to use these */
    protected static final String NODES_7_FILENAME  = Paths.get("data", "graphs", "Nodes_7_OutTree.dot").toString();
    protected static final String NODES_8_FILENAME  = Paths.get("data", "graphs", "Nodes_8_Random.dot").toString();
    protected static final String NODES_9_FILENAME  = Paths.get("data", "graphs", "Nodes_9_SeriesParallel.dot").toString();
    protected static final String NODES_10_FILENAME = Paths.get("data", "graphs", "Nodes_10_Random.dot").toString();
    protected static final String NODES_11_FILENAME = Paths.get("data", "graphs", "Nodes_11_OutTree.dot").toString();

    protected static final List<Pair<Integer, Integer>> NODES_7_LENGTHS   = Arrays.asList(new Pair<>(2,  28), new Pair<>(4,  22));
    protected static final List<Pair<Integer, Integer>> NODES_8_LENGTHS   = Arrays.asList(new Pair<>(2, 581), new Pair<>(4, 581));
    protected static final List<Pair<Integer, Integer>> NODES_9_LENGTHS   = Arrays.asList(new Pair<>(2,  55), new Pair<>(4,  55));
    protected static final List<Pair<Integer, Integer>> NODES_10_LENGTHS  = Arrays.asList(new Pair<>(2,  50), new Pair<>(4,  50));
    protected static final List<Pair<Integer, Integer>> NODES_11_LENGTHS  = Arrays.asList(new Pair<>(2, 350), new Pair<>(4, 227));

    // Actual class

    // Public accessors for use of the data
    final List<String> graphs;
    final List<List<Pair<Integer, Integer>>> optimalScheduleLengths;

    // Do not try to create graph sets outside of this class. Use the static methods
    private GraphSet(List<String> graphs, List<List<Pair<Integer, Integer>>> optimalScheduleLengths) {
        this.graphs = graphs;
        this.optimalScheduleLengths = optimalScheduleLengths;
    }

    // Create a graphset from merging multiple
    public GraphSet(GraphSet... sets) {
        this.graphs = new ArrayList<>();
        this.optimalScheduleLengths = new ArrayList<>();

        for(GraphSet set : sets) {
            graphs.addAll(set.graphs);
            optimalScheduleLengths.addAll(set.optimalScheduleLengths);
        }
    }

    /**
     * Olivers graphs
     */
    public static GraphSet OLIVER() {
        return new GraphSet(
            Arrays.asList(NODES_7_FILENAME, NODES_8_FILENAME, NODES_9_FILENAME, NODES_10_FILENAME, NODES_11_FILENAME),
            Arrays.asList(NODES_7_LENGTHS, NODES_8_LENGTHS, NODES_9_LENGTHS, NODES_10_LENGTHS, NODES_11_LENGTHS)
        );
    }
    /**
     * Olivers graphs up to a given node size
     */
    public static GraphSet OLIVER(int nodeBound) {
        return new GraphSet(
            Arrays.asList(NODES_7_FILENAME, NODES_8_FILENAME, NODES_9_FILENAME, NODES_10_FILENAME, NODES_11_FILENAME).subList(0, nodeBound - 6),
            Arrays.asList(NODES_7_LENGTHS, NODES_8_LENGTHS, NODES_9_LENGTHS, NODES_10_LENGTHS, NODES_11_LENGTHS).subList(0, nodeBound - 6)
        );
    }

    /**
     * Graphs with 2 or 4 processors, less than 21 nodes
     */
    public static GraphSet SMALL_EXTRA() {
        return new GraphSet(
            readFromExtras(Arrays.asList("Nodes_10"), Arrays.asList("16p")),
            readFromExtras(Arrays.asList("16p"), Arrays.asList("Nodes_21", "Nodes_30")) // So 16 comes after 2, 4, and 8
        );
    }
    /**
     * Graphs with 4 or 16 processors, and not fork joins of 21 or 30 nodes
     */
    public static GraphSet MEDIUM_EXTRA() {
        return readFromExtras(
                Arrays.asList("2p"),
                Arrays.asList("Nodes_10", "Nodes_30", "Fork", "Independent", "Join", "MaxBf", "Random_Nodes"));
    }
    /**
     * All fork joins of 21 or 30 nodes
     */
    public static GraphSet HARD_EXTRAS() {
        return new GraphSet(
            readFromExtras(Arrays.asList("Nodes_21", "Nodes_30"), Arrays.asList("16p")),
            readFromExtras(Arrays.asList("16p"), Arrays.asList("Nodes_10")) // So 16 comes after 2, 4, and 8
        );
    }
    /**
     * All the above tests except for hard
     */
    public static GraphSet ALL_REASONABLE() {
        return new GraphSet(OLIVER(), SMALL_EXTRA(), MEDIUM_EXTRA());
    }

    /**
     * Creates a graphset from files in the extra graphs folder, with given exclusions and inclusions
     * @param inclusions If none of these strings are in the filename, item is excluded May be null.
     * @param exclusions If any of these strings are in the filename, item is excluded. May be null.
     * @return A graphset. To be used internally and exposed through explicit static creators.
     */
    private static GraphSet readFromExtras(List<String> inclusions, List<String> exclusions) {
        ArrayList<String> graphs = new ArrayList<>();
        ArrayList<List<Pair<Integer, Integer>>> optimalScheduleLengths = new ArrayList<>();

        // Get all files in data/SampleData/Input, override graphs for this to be the value
        File inputFolder = new File(String.valueOf(Paths.get("data", "SampleData", "Input")));

        // Loop through all files in input file folder
        if(inputFolder.list() == null)
            throw new RuntimeException("Error trying to load extras, folder contents not found!");

        FileNameLoop:
        for (String fileName : Objects.requireNonNull(inputFolder.list())) {
            String filePath = Paths.get("data", "SampleData", "Input", fileName).toString();

            // Inclusions and Exclusions constraints
            if(exclusions != null) {
                for (String exclusion : exclusions) {
                    if (fileName.contains(exclusion))
                        continue FileNameLoop;
                }
            }
            if(inclusions != null) {
                boolean hasInclusion = false;
                for (String inclusion : inclusions) {
                    if(fileName.contains(inclusion)) {
                        hasInclusion = true;
                        break;
                    }
                }
                if(!hasInclusion)
                    continue;
            }

            try {
                // Create new pair in optimalSchedules of the correct no. of processors and scheduleLength
                // derived from output directory.
                String outputFileName = filePath.replace("Input", "Output");

                optimalScheduleLengths.add(
                    Collections.singletonList(new Pair<>(
                        Integer.valueOf(fileName.substring(0, fileName.indexOf('p'))),
                        ScheduleReading.lengthFromFile(new FileInputStream(outputFileName))
                    ))
                );

            } catch (FileNotFoundException e) {
                System.out.println("Couldn't find: " + filePath);
            } finally {
                // Add graph name to list of testing graphs (in finally so only will do if could add to optimalScheduleLengths
                graphs.add(filePath);
            }
        }

        return new GraphSet(graphs, optimalScheduleLengths);
    }
}
