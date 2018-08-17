package visualisation.modules;

import common.Config;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class renders a set of statistical components including miscellaneous algorithm metrics, a best schedule
 * bounding visualization and a cpu usage graph. The elements can be updated constantly with use of the update()
 * method.
 *
 * The constructor initializes ALL elements of the view so if more is to be added it can be done here. Adding new
 * miscellaneous metric should be done in the labelGrid, as this will align them nicely. All components needing
 * to be periodically updated can be accessed as a field.
 */
public class AlgorithmStatisticsVisualiser extends VBox {

    // CONSTANTS
    private static final String TIME_LABEL_CLASS_CSS = "time-label";
    private static final String STATS_CONTENT_CLASS_CSS = "stats-content";
    private static final String TIME_LABEL_FINISH_CLASS_CSS = "time-label-finished";
    private static final String FINISHED_LABEL_CLASS = "finished-label";

    private static final int SCHEDULE_TIME_BOUNDING_HEIGHT = 100;

    private static final double LABEL_GRID_COLUMN_WIDTH = 150;
    private static final double LABEL_GRID_ROW_HEIGHT = 20;

    private static final Color FINISHING_TICK_MARK_FILL = Color.web(Config.UI_LIGHT_BLACK_COLOUR);

    // BOUND FIELDS
    private double _initialLowerBound;
    private double _initialUpperBound;

    // For keeping track of number of updates, certain visualizations may use this to prevent over updating
    private long _updateIteration;


    // ELEMENTS
    private BoundingChart _boundingChart;

    private final XYChart.Series<Number, Number> _cpuChartData;

    // Label Values that need to be periodically updated through life of visualizer
    private final Timer _timer; // Timer
    private long _millisecondsRunning;
    private final Label _timeLabel;

    private Label _branchesCoveredValue; // Metrics labels
    private Label _branchesCulledValue;
    private Label _cullingRateValue;
    private Label _memoryFreeValue;
    private Label _memoryAllocatedValue;
    private Label _memoryMaxValue;

    private Label _finishedLabel;

    /**
     * Creates all visual elements and arranges on the screen. Many labels will initially be set to zero.
     * @param coresUsed : Cores algorithm runs on
     */
    public AlgorithmStatisticsVisualiser(long coresUsed, int initialLowerBound, int initialUpperBound) {
        _timeLabel = new Label("0");
        _timeLabel.getStyleClass().add(TIME_LABEL_CLASS_CSS);

        Label processorsUsedLabel = new Label("Cores running:");
        Label processorsUsedValue = new Label(String.format("%d", coresUsed));

        Label branchesCoveredLabel = new Label("Branches explored:");
        _branchesCoveredValue = new Label(String.format("%d", 0));

        Label branchesCulledLabel = new Label("Branches culled:");
        _branchesCulledValue = new Label(String.format("%d", 0));

        Label cullingRateLabel = new Label("Culling rate:");
        _cullingRateValue = new Label(String.format("%.1f%%", 0.0));

        Label memoryFreeLabel = new Label("Memory free:");
        _memoryFreeValue = new Label("");

        Label memoryAllocatedLabel = new Label("Memory Allocated:");
        _memoryAllocatedValue = new Label("");

        Label memoryMaxLabel = new Label("JVM Memory Limit:");
        _memoryMaxValue = new Label("");

        _finishedLabel = new Label("SCHEDULING COMPLETE");
        _finishedLabel.getStyleClass().add(FINISHED_LABEL_CLASS);
        _finishedLabel.setVisible(false);

        // Add label elements to label grid so are aligned
        // Holds the following labels
        GridPane _labelGrid = createLabelGrid();
        _labelGrid.add(_timeLabel, 0, 0);
        _labelGrid.add(processorsUsedLabel, 0, 1);
        _labelGrid.add(processorsUsedValue, 1, 1);
        _labelGrid.add(branchesCoveredLabel, 0, 2);
        _labelGrid.add(_branchesCoveredValue, 1, 2);
        _labelGrid.add(branchesCulledLabel, 0, 3);
        _labelGrid.add(_branchesCulledValue, 1, 3);
        _labelGrid.add(cullingRateLabel, 0, 4);
        _labelGrid.add(_cullingRateValue, 1, 4);
        _labelGrid.add(memoryFreeLabel, 0, 5);
        _labelGrid.add(_memoryFreeValue, 1, 5);
        _labelGrid.add(memoryAllocatedLabel, 0, 6);
        _labelGrid.add(_memoryAllocatedValue, 1, 6);
        _labelGrid.add(memoryMaxLabel, 0, 7);
        _labelGrid.add(_memoryMaxValue, 1, 7);

        // Create a cpu usage chart
        // Cpu chart
        LineChart<Number, Number> _cpuChart = createCpuChart();
        _cpuChartData = new XYChart.Series<>();
        _cpuChart.getData().add(_cpuChartData);
        _cpuChartData.getNode().getStyleClass().add(STATS_CONTENT_CLASS_CSS);
        _updateIteration = 0;


        // Start timer
        _millisecondsRunning = 0;
        _timer = new Timer();
        _timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                _millisecondsRunning += 10;

                if (_millisecondsRunning % 2000 == 0) // Every 2 seconds update cpu usage chart
                    updateCpuUsageChart();
            }
        }, new Date(), 10);

        // Create horizontal box with misc. labels to the left of cpu chart
        VBox labelVbox = new VBox();
        labelVbox.getChildren().addAll(_finishedLabel, _labelGrid);

        HBox labelAndCpuVBox = new HBox();
        labelAndCpuVBox.setPadding(new Insets(15));
        labelAndCpuVBox.getChildren().addAll(labelVbox, _cpuChart);
        HBox.setHgrow(_cpuChart, Priority.SOMETIMES);

        // Create bounding chart
        _boundingChart = new BoundingChart(initialLowerBound, initialUpperBound);

        // Add previously created boxes into VBox; one on top of another
        setPadding(new Insets(15));
        getChildren().addAll(labelAndCpuVBox, _boundingChart);
        VBox.setVgrow(labelAndCpuVBox, Priority.SOMETIMES);
        //VBox.setVgrow(boundVBox, Priority.SOMETIMES); TODO:
    }


    /**
     * This method is responsible for updating the state of a schedule time bounding visualisation, as well as updating
     * the values of several on screen statistics in text-format.
     * @param statistics : updateStatistics
     */
    public void update(Statistics statistics) {
        _updateIteration++; // Increment iteration of update

        // If on first iteration then update all of these parameters
        /*if (_updateIteration == 1) {
            _initialUpperBound = statistics.getMaxScheduleBound();
            _initialLowerBound = 0;
        }

        if (_initialUpperBound == Integer.MAX_VALUE) {
            _initialUpperBound = statistics.getMaxScheduleBound();
        }*/

        updateTimeLabel(); // Update Time label

        updateLabels(statistics); // Update misc. statistics labels
        _boundingChart.update(statistics.getMinScheduleBound(), statistics.getMaxScheduleBound());
    }


    /**
     * To be called when Visualizer is intended to stop
     */
    public void stop() {
        _finishedLabel.setVisible(true);
        _timeLabel.getStyleClass().add(TIME_LABEL_FINISH_CLASS_CSS);
        _timer.cancel();
    }


    /**
     * Sets the time elapsed from the start of the counter of _millisecondsRunning and converts into the form
     * hh:mm:ss:ms
     */
    private void updateTimeLabel() {
        _timeLabel.setText(String.format("%02d:%02d:%02d.%01d",
                _millisecondsRunning/(3600*1000),
                _millisecondsRunning/(60*1000) % 60,
                _millisecondsRunning/1000 % 60,
                _millisecondsRunning % 1000 / 100));
    }


    /**
     * Updates the labels of miscellaneous algorithm metrics branches culled, search space, etc.
     * @param statistics : Update statistics
     */
    private void updateLabels(Statistics statistics) {

        NumberFormat numberFormat = new DecimalFormat("0.00E0");

        _branchesCoveredValue.setText(String.format("%s", numberFormat.format(new BigDecimal(statistics.getSearchSpaceLookedAt()))));
        _branchesCulledValue.setText(String.format("%s", numberFormat.format(new BigDecimal(statistics.getSearchSpaceCulled()))));

        // Big integer division requires that you convert into decimals so as to not lose precision (as integer division does).
        BigDecimal searchSpaceCulledAsBigDec = new BigDecimal(statistics.getSearchSpaceCulled());
        BigDecimal searchSpaceLookedAtAsBigDec = new BigDecimal(statistics.getSearchSpaceLookedAt());
        if (!searchSpaceCulledAsBigDec.equals(BigDecimal.ZERO) && !searchSpaceLookedAtAsBigDec.equals(BigDecimal.ZERO)) // We only want to divide if we know for sure the algorithm has some metrics for us
            _cullingRateValue.setText(String.format("%.1f%%", 100 * searchSpaceCulledAsBigDec.divide(searchSpaceCulledAsBigDec.add(searchSpaceLookedAtAsBigDec), MathContext.DECIMAL32).floatValue()));

        NumberFormat format = NumberFormat.getInstance();
        Runtime runtime = Runtime.getRuntime(); // For the commas in 100,000

        _memoryFreeValue.setText(String.format("%sM", format.format(runtime.freeMemory() / (1024 * 1024)))); // Convert to Megabytes
        _memoryAllocatedValue.setText(String.format("%sM", format.format(runtime.totalMemory() / (1024 * 1024))));
        _memoryMaxValue.setText(String.format("%sM", format.format(runtime.maxMemory() / (1024 * 1024))));

    }

    /**
     * Calculate the current CPU usage of the OS and plot it onto chart
     */
    private void updateCpuUsageChart() {
        double cpuUsage = 0;
        try {
            // Get necessary objects representing OS information
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
            AttributeList list = mbs.getAttributes(name, new String[]{ "ProcessCpuLoad" });

            if (list.isEmpty()) {
                return;
            }

            // Get cpu usage ratio
            Attribute att = (Attribute)list.get(0);
            Double value  = (Double)att.getValue();

            if (value == -1.0) {
                return;
            }

            cpuUsage = ((int)(value * 100));
        } catch (Exception e) { /* Nothing should happen here imo */}

        // Add current cpu usage to chart data structure, chart will automatically update. Divide by 1000 for seconds
        _cpuChartData.getData().add(new XYChart.Data<>(_millisecondsRunning / 1000, cpuUsage));
        if (_cpuChartData.getData().size() > 50) { // If data size is greater than 100 points begin to remove
            _cpuChartData.getData().remove(0, 1);
        }
    }


    /**
     * Creates an empty grid to house miscellaneous metrics labels, branches culled, searched, etc.
     * @return GridPane : grid pane to hold labels
     */
    private GridPane createLabelGrid() {

        GridPane gridPane = new GridPane();
        gridPane.getColumnConstraints().addAll(
                new ColumnConstraints(LABEL_GRID_COLUMN_WIDTH),
                new ColumnConstraints(80)
        );
        RowConstraints rc = new RowConstraints(LABEL_GRID_ROW_HEIGHT);
        rc.setMinHeight(100);
        gridPane.getRowConstraints().add(rc);

        return gridPane;
    }


    /**
     * Create a line chart labelled with CPU usage information.
     * @return LineChart<Number,Number> : line chart representing cpu usage
     */
    private LineChart<Number,Number> createCpuChart() {

        // Create number axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time (s)");
        xAxis.forceZeroInRangeProperty().setValue(false); // Allow x axis to scroll
        yAxis.setLabel("CPU Usage (%)");

        // Create LineChart
        LineChart<Number, Number>  cpuChart = new LineChart<>(xAxis, yAxis);
        cpuChart.setCreateSymbols(false);
        cpuChart.setLegendVisible(false);
        cpuChart.setTitle("CPU Usage");

        cpuChart.setMinHeight(200);
        cpuChart.setMinWidth(200);

        return cpuChart;
    }

}
