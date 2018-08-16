package visualisation;

import algorithm.Algorithm;
import algorithm.TieredAlgorithm;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import visualisation.modules.AlgorithmStatisticsVisualiser;
import visualisation.modules.GraphVisualiser;
import visualisation.modules.ScheduleVisualiser;
import visualisation.modules.Statistics;

import java.util.List;

public class AinurVisualiser extends Region {

    /* MACROS */

    public final static Duration FAST_POLLING_DELAY = Duration.millis(100);
    public final static Duration SLOW_POLLING_DELAY = Duration.millis(2000);

    /* Fields */

    // Algorithm currently visualising
    private Algorithm _algorithm;

    // Visualiser modules
    private GraphVisualiser _gv;
    private ScheduleVisualiser _sv;
    private AlgorithmStatisticsVisualiser _asv;
    private Statistics _stats;

    // Used to indicate whether or not the algorithm is running
    private Timeline _fastPoller;
    private Timeline _slowPoller;

    // Used to indicate whether or not the algorithm is tiered
    private boolean _isTiered;

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
        _asv = new AlgorithmStatisticsVisualiser(lowerBound, 100000000, coresUsed);

        // Initialise stats object
        _stats = new Statistics();
        _stats.setMaxScheduleBound(uppedBound);
        _stats.setMinScheduleBound(lowerBound);

        // See if algorithm is tiered.
        _isTiered = isTiered(_algorithm);

        // Set up layout
        this.setUpLayout();
    }

    /* Public Methods */

    /**
     * This method runs the visualiser alongside the algorithm.
     * This method periodically polls the algorithm for information about its current status.
     * This method will then update the visualiser modules accordingly.
     */
    public void run() {
        _fastPoller = new Timeline(new KeyFrame(FAST_POLLING_DELAY, event -> {
            this.updateGraph();
            this.updateStatistics();
        }));

        _slowPoller = new Timeline(new KeyFrame(SLOW_POLLING_DELAY, event -> this.updateSchedule()));

        _slowPoller.setCycleCount(Animation.INDEFINITE);
        _fastPoller.setCycleCount(Animation.INDEFINITE);

        _fastPoller.play();
        _slowPoller.play();
    }

    /**
     * Called when the algorithm it is polling stops running.
     * This will stop the visualisation on its current value.
     * This should be called from another thread to interrupt the show method's while loop
     */
    public void stop() {
        updateGraph();
        updateSchedule();
        updateStatistics();
        _asv.stop();
        _fastPoller.stop();
        _slowPoller.stop();
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
        if (_isTiered) {
            TieredAlgorithm tAlgorithm = (TieredAlgorithm) _algorithm;
            List<Node> nodeList = tAlgorithm.currentNodes();
            _gv.update(nodeList);
        } else {
            Node currentNode = _algorithm.currentNode();
            _gv.update(currentNode);
        }
    }

    /**
     * Private helper method.
     * Gets the current best schedule from an algorithm.
     * Uses this schedule to update the ScheduleVisualiser.
     */
    private void updateSchedule() {
        Schedule currentSchedule = _algorithm.getCurrentBest();
        _sv.update(currentSchedule);
    }

    /**
     * Private helper method.
     * Checks to see if an algorithm is an instance of a tiered algorithm.
     * @returns True if is tiered, false otherwise.
     */
    private boolean isTiered(Algorithm algorithm) {
        if (algorithm instanceof TieredAlgorithm) {
            return true;
        }
        return false;
    }

    /**
     * Private helper method.
     * Gets algorithm statistics from an algorithm.
     * Uses these statistics to update the AlgorithmStatisticsVisualiser.
     */
    private void updateStatistics() {
        _stats.setSearchSpaceCulled(_algorithm.branchesCulled());
        _stats.setSearchSpaceLookedAt(_algorithm.branchesExplored());

        _stats.setMaxScheduleBound(_algorithm.getCurrentBest().getEndTime());
        _stats.setMinScheduleBound(_algorithm.lowerBound());

        _asv.update(_stats);
    }
}
