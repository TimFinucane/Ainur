package visualisation;

import algorithm.Algorithm;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import visualisation.modules.AlgorithmStatisticsVisualiser;
import visualisation.modules.GraphVisualiser;
import visualisation.modules.ScheduleVisualiser;
import visualisation.modules.Statistics;

import java.util.concurrent.TimeUnit;

public class AinurVisualiser extends Region {

    /* MACROS */

    public final static int POLLING_DELAY = 100 ;

    /* Fields */

    // Algorithm currently visualising
    private Algorithm _algorithm;

    // Visualiser modules
    private GraphVisualiser _gv;
    private ScheduleVisualiser _sv;
    private AlgorithmStatisticsVisualiser _asv;
    private Statistics _stats;

    // Used to indicate whether or not the
    private boolean _running;

    /* Constructors */

    /**
     * Constructs an AinurVisualiser
     * Nests in modules, GraphVisualiser, ScheduleVisualiser and AlgorithmStatisticsVisualiser
     *
     * @param algorithm the algorithm to visualise
     * @param graph The task graph to be displayed
     * @param lowerBound the lowerbound of the algorithm //TODO this should be removed when a method to get this is implemented
     * @param uppedBound the uppedbound of the algorithm //TODO this should be removed when a method to get this is implemented
     * @param coresUsed The number of cores to be used
     */
    public AinurVisualiser(Algorithm algorithm, Graph graph, int lowerBound, int uppedBound, long coresUsed) {
        // Assign Args
        _algorithm = algorithm;

        // Initialise visualisers
        _gv = new GraphVisualiser(graph);
        _sv = new ScheduleVisualiser();
        _asv = new AlgorithmStatisticsVisualiser(lowerBound, uppedBound, coresUsed);

        // Initialise stats object
        _stats = new Statistics();
        _stats.setMaxScheduleBound(uppedBound);
        _stats.setMinScheduleBound(lowerBound);

        // Set up layout
        this.setUpLayout();
    }

    /* Public Methods */

    /**
     * This method runs the visualiser alongside the algorithm.
     * This method periodically polls the algorithm for information about its current status.
     * This method will then update the visualiser modules accordingly.
     */
    public void run() throws InterruptedException {
        _running = true;

        while (_running) {
            this.updateGraph();
            // Currently can't update these for some reason
            //this.updateSchedule();
            // this.updateStatistics();
            TimeUnit.MILLISECONDS.sleep(POLLING_DELAY);
        }
    }

    /**
     * Called when the algorithm it is polling stops running.
     * This will stop the visualisation on its current value.
     * This should be called from another thread to interrupt the show method's while loop
     */
    public void stop() {
        _running = false;
    }

    /* Private Helper Methods */

    /**
     * Private helper method.
     * This method lays out the visualiser modules on the in the parent component (this)
     */
    private void setUpLayout() {
        // Put the graph and stats visualiser side by side
        HBox graphStatHBox = new HBox();
        graphStatHBox.getChildren().addAll(_gv, _asv);

        // Put the schedule visualiser underneath
        VBox outerVBox = new VBox();
        outerVBox.getChildren().addAll(graphStatHBox, _sv);

        // add to the AinurVisualiser
        this.getChildren().add(outerVBox);
    }

    /**
     * Private helper method.
     * Gets the current node / nodes from an algorithm.
     * Uses this node to update the GraphVisualiser.
     */
    private void updateGraph() {
        System.out.println("graph");
        Node currentNode = _algorithm.currentNode();
        _gv.update(currentNode);
        // TODO check type of algorithm, if tiered get list if not get single
    }

    /**
     * Private helper method.
     * Gets the current best schedule from an algorithm.
     * Uses this schedule to update the ScheduleVisualiser.
     */
    private void updateSchedule() {
        System.out.println("schedule");
        Schedule currentSchedule = _algorithm.getCurrentBest();
        _sv.update(currentSchedule);
    }

    /**
     * Private helper method.
     * Gets algorithm statistics from an algorithm.
     * Uses these statistics to update the AlgorithmStatisticsVisualiser.
     */
    private void updateStatistics() {
        System.out.println("stats");
        _stats.setSearchSpaceCulled(_algorithm.branchesCulled());
        _stats.setSearchSpaceLookedAt(_algorithm.branchesExplored());
        _asv.update(_stats);
    }
}
