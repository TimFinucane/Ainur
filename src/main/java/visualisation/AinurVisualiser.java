package visualisation;

import algorithm.Algorithm;
import algorithm.TieredAlgorithm;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import visualisation.modules.AlgorithmStatisticsVisualiser;
import visualisation.modules.GraphVisualiser;
import visualisation.modules.ScheduleVisualiser;
import visualisation.modules.Statistics;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AinurVisualiser extends VBox {

    /* MACROS */

    // Delays
    private final static Duration FAST_POLLING_DELAY = Duration.millis(16);
    private final static Duration SLOW_POLLING_DELAY = Duration.millis(2000);
    private final static Duration MEDIUM_POLLING_DELAY = Duration.millis(333);

    private final static int INTERPOL_MOD = 3;

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
    private Timeline _mediumPoller;

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
     * @param upperBound the upperbound of the algorithm //TODO this should be removed when a method to get this is implemented
     * @param coresUsed The number of cores to be used
     */
    public AinurVisualiser(Algorithm algorithm, Graph graph, int numProcessors, int lowerBound, int upperBound, long coresUsed) {
        // Assign Args
        _algorithm = algorithm;

        // Initialise visualisers
        _gv = new GraphVisualiser(graph);
        _sv = new ScheduleVisualiser(numProcessors);
        _asv = new AlgorithmStatisticsVisualiser(lowerBound, upperBound, coresUsed);

        // Initialise stats object
        _stats = new Statistics();
        _stats.setMaxScheduleBound(upperBound);
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
            this.updateStatistics();
            this.updateGraphNodes();
        }));

        AtomicInteger count = new AtomicInteger(0);
        _mediumPoller = new Timeline(new KeyFrame(MEDIUM_POLLING_DELAY, event -> {
            count.incrementAndGet();
            _gv.update(count.get() / (double) INTERPOL_MOD);

            if (count.get() == INTERPOL_MOD) {
                _gv.flush();
                count.set(1);
            }
        }));

        _slowPoller = new Timeline(new KeyFrame(SLOW_POLLING_DELAY, event -> this.updateSchedule()));

        _slowPoller.setCycleCount(Animation.INDEFINITE);
        _mediumPoller.setCycleCount(Animation.INDEFINITE);
        _fastPoller.setCycleCount(Animation.INDEFINITE);

        _fastPoller.play();
        _mediumPoller.play();
        _slowPoller.play();
    }

    /**
     * Called when the algorithm it is polling stops running.
     * This will stop the visualisation on its current value.
     * This should be called from another thread to interrupt the show method's while loop
     */
    public void stop() {
        updateSchedule();
        updateStatistics();
        _gv.stop();
        _asv.stop();
        _fastPoller.stop();
        _mediumPoller.stop();
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
        //VBox outerVBox = new VBox();
        this.getChildren().addAll(graphStatHBox, _sv);

        //setVgrow(graphStatHBox, Priority.SOMETIMES); TODO: Set this when the gv and asv are resizable
        setVgrow(_sv, Priority.SOMETIMES);
        setPadding(new Insets(15));
        // add to the AinurVisualiser
        //this.getChildren().add(outerVBox);
    }

    /**
     * Private helper method.
     * Gets the current node / nodes from an algorithm.
     * Uses this node to update the GraphVisualiser.
     */
    private void updateGraphNodes() {
        if (_isTiered) {
            TieredAlgorithm tAlgorithm = (TieredAlgorithm) _algorithm;
            List<Node> nodeList = tAlgorithm.currentNodes();
            for (Node node : nodeList)
                _gv.nodeVisited(node);
        } else {
            Node currentNode = _algorithm.currentNode();
            _gv.nodeVisited(currentNode);
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
     * @return True if is tiered, false otherwise.
     */
    private boolean isTiered(Algorithm algorithm) {
        return algorithm instanceof TieredAlgorithm;
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
