package visualisation;

import algorithm.Algorithm;
import algorithm.TieredAlgorithm;
import common.graph.Graph;
import common.graph.Node;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import visualisation.modules.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AinurVisualiser extends VBox {

    /* MACROS */

    // css classes
    private final static String GRAPH_CLASS_CSS = "graph-vis";
    private final static String STATS_CLASS_CSS = "stats-vis";
    private final static String SCHEDULE_CLASS_CSS = "schedule-vis";
    private final static String VIS_CLASS_CSS = "vis";
    private static final String FINISHED_LABEL_CLASS = "finished-label";
    private static final String TIME_LABEL_CLASS_CSS = "time-label";
    private static final String TIME_LABEL_FINISH_CLASS_CSS = "time-label-finished";

    // Delays
    private final static Duration FAST_POLLING_DELAY = Duration.millis(16);
    private final static Duration SLOW_POLLING_DELAY = Duration.millis(2000);
    private final static Duration MEDIUM_POLLING_DELAY = Duration.millis(333);

    private final static int INTERPOL_MOD = 3;

    private final long _startTime;

    /* Fields */

    // Algorithm currently visualising
    private Algorithm _algorithm;

    // Visualiser modules
    private GraphVisualiser _graph;
    private ScheduleVisualiser _schedule;
    private BoundingChart _bounds;
    private CPUChart _cpuChart;
    private StatisticsVisualiser _statistics;
    private Label _timeLabel = new Label();
    private Label _finishedLabel;

    // Used to indicate whether or not the algorithm is running
    private Timeline _fastPoller;
    private Timeline _slowPoller;
    private Timeline _mediumPoller;

    /* Constructors */

    /**
     * Constructs an AinurVisualiser
     * Nests in modules, GraphVisualiser, ScheduleVisualiser and others
     *
     * @param algorithm the algorithm to visualise
     * @param graph The task graph to be displayed
     */
    public AinurVisualiser(Algorithm algorithm, Graph graph, int numProcessors) {
        // Assign Args
        _algorithm = algorithm;

        _startTime = System.currentTimeMillis();

        int coresUsed = (algorithm instanceof TieredAlgorithm) ? ((TieredAlgorithm) algorithm).numThreads() : 1;
        // TODO: When getCurrentBest is safe (i.e. using non optimal starting algorithm) remove the math min
        int upperBound = Math.min(algorithm.getCurrentBest().getEndTime(), 1000);

        // Initialise visualisers
        _graph = new GraphVisualiser(graph);
        _schedule = new ScheduleVisualiser(numProcessors);
        _bounds = new BoundingChart(algorithm.lowerBound(), upperBound);
        _cpuChart = new CPUChart(SLOW_POLLING_DELAY.toMillis() / 1000.0);
        _statistics = new StatisticsVisualiser(coresUsed);

        _timeLabel.getStyleClass().add(TIME_LABEL_CLASS_CSS);
        _finishedLabel = new Label("SCHEDULING COMPLETE");
        _finishedLabel.getStyleClass().add(FINISHED_LABEL_CLASS);
        _finishedLabel.setVisible(false);

        // Create the stats section
        // TODO: Comment and/or split into methods?
        VBox extraStats = new VBox(20, _timeLabel, _statistics);
        extraStats.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(_statistics, Priority.SOMETIMES);

        HBox statsUpper = new HBox(extraStats, _cpuChart);
        HBox.setHgrow(_statistics, Priority.SOMETIMES);
        HBox.setHgrow(_cpuChart, Priority.SOMETIMES);

        VBox statsBox = new VBox(_finishedLabel, statsUpper, _bounds);
        statsBox.setAlignment(Pos.CENTER);
        VBox.setVgrow(statsUpper, Priority.SOMETIMES);
        VBox.setVgrow(_bounds, Priority.SOMETIMES);

        HBox statsWrapper = new HBox(statsBox);
        HBox.setHgrow(statsBox, Priority.SOMETIMES);
        statsWrapper.getStyleClass().addAll(STATS_CLASS_CSS, VIS_CLASS_CSS);

        HBox graphWrapper = new HBox(_graph);
        graphWrapper.getStyleClass().addAll(GRAPH_CLASS_CSS, VIS_CLASS_CSS);

        // Put the graph and stats visualiser side by side
        HBox upper = new HBox(graphWrapper, statsWrapper);
        HBox.setHgrow(statsWrapper, Priority.SOMETIMES);

        // Put the schedule visualiser underneath
        HBox scheduleWrapper = new HBox(_schedule);
        scheduleWrapper.getStyleClass().addAll(SCHEDULE_CLASS_CSS, VIS_CLASS_CSS);
        HBox.setHgrow(_schedule, Priority.ALWAYS);

        this.getChildren().addAll(upper, scheduleWrapper);
        VBox.setVgrow(scheduleWrapper, Priority.ALWAYS);
    }

    /* Public Methods */

    /**
     * This method runs the visualiser alongside the algorithm.
     * This method periodically polls the algorithm for information about its current status.
     * This method will then update the visualiser modules accordingly.
     */
    public void run() {
        _fastPoller = new Timeline(new KeyFrame(FAST_POLLING_DELAY, event -> {
            _bounds.update(_algorithm.lowerBound(), _algorithm.getCurrentBest().getEndTime());
            _statistics.update(_algorithm.branchesExplored(), _algorithm.branchesCulled());
            updateTimeLabel(Duration.millis(System.currentTimeMillis() - _startTime));

            updateGraphNodes();
        }));

        AtomicInteger count = new AtomicInteger(0);
        _mediumPoller = new Timeline(new KeyFrame(MEDIUM_POLLING_DELAY, event -> {
            count.incrementAndGet();
            _graph.update(count.get() / (double) INTERPOL_MOD);

            if (count.get() == INTERPOL_MOD) {
                _graph.flush();
                count.set(1);
            }
        }));

        _slowPoller = new Timeline(new KeyFrame(SLOW_POLLING_DELAY, event -> {
            _schedule.update(_algorithm.getCurrentBest());
            _cpuChart.update();
        }));

        _slowPoller.setCycleCount(Animation.INDEFINITE);
        _mediumPoller.setCycleCount(Animation.INDEFINITE);
        _fastPoller.setCycleCount(Animation.INDEFINITE);

        _fastPoller.play();
        _mediumPoller.play();
        _slowPoller.play();
    }

    /**
     * Sets the time elapsed from the start of the counter of _millisecondsRunning and converts into the form
     * hh:mm:ss:ms
     */
    private void updateTimeLabel(Duration duration) {
        _timeLabel.setText(String.format("%d:%02d:%02d.%02d",
            (int)duration.toHours(),
            (int)duration.toMinutes(),
            (int)duration.toSeconds(),
            (int)duration.toMillis()
            )
        );
    }

    /**
     * Called when the algorithm it is polling stops running.
     * This will stop the visualisation on its current value.
     * This should be called from another thread to interrupt the show method's while loop
     */
    public void stop() {
        _schedule.update(_algorithm.getCurrentBest());
        _bounds.update(_algorithm.lowerBound(), _algorithm.getCurrentBest().getEndTime());
        _statistics.update(_algorithm.branchesExplored(), _algorithm.branchesCulled());

        _graph.stop();
        _finishedLabel.setVisible(true);
        _timeLabel.getStyleClass().add(TIME_LABEL_FINISH_CLASS_CSS);

        _fastPoller.stop();
        _mediumPoller.stop();
        _slowPoller.stop();
    }

    /* Private Helper Methods */

    /**
     * Private helper method.
     * Gets the current node / nodes from an algorithm.
     * Uses this node to update the GraphVisualiser.
     */
    private void updateGraphNodes() {
        if (_algorithm instanceof TieredAlgorithm) {
            TieredAlgorithm tAlgorithm = (TieredAlgorithm) _algorithm;
            List<Node> nodeList = tAlgorithm.currentNodes();
            for (Node node : nodeList)
                _graph.nodeVisited(node);
        } else {
            Node currentNode = _algorithm.currentNode();
            _graph.nodeVisited(currentNode);
        }
    }
}
