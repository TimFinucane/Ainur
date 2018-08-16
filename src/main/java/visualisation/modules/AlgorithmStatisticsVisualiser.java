package visualisation.modules;

import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import scala.util.parsing.combinator.testing.Str;

import javax.management.*;
import java.lang.management.ManagementFactory;
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
public class AlgorithmStatisticsVisualiser extends Region {

    // CONSTANTS
    private static final int SCHEDULE_TIME_BOUNDING_HEIGHT = 100;
    private static final int SCHEDULE_TIME_BOUNDING_WIDTH = 750;

    private static final double LABEL_GRID_COLUMN_WIDTH = 150;
    private static final double LABEL_GRID_ROW_HEIGHT = 20;

    private static final Font DEFAULT_FONT = new Font("Consolas", 12);
    private static final Font DEFAULT_TIME_FONT = new Font("Consolas", 20);

    private static final Paint BOUNDING_RECTANGLE_FILL = Paint.valueOf("#b475d6");
    private static final Color BOUNDING_RECTANGLE_STROKE_FILL = Color.BLACK;
    private static final Color FINISHING_TICK_MARK_FILL = Color.RED;
    private static final Color FINISHING_TIMER_FONT_FILL = Color.RED;


    // BOUND FIELDS
    private double _initialLowerBound;
    private double _initialUpperBound;
    private double _initialBoundRange;

    private double _bestLower;
    private double _bestUpper;

    // For keeping track of number of updates, certain visualizations may use this to prevent over updating
    private long _updateIteration;


    // ELEMENTS
    // Grid
    private final GridPane _boundGrid;
    private final NumberAxis _boundingAxis;

    // Cpu chart
    private final LineChart<Number, Number> _cpuChart;
    private final XYChart.Series _cpuChartData;

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



    /**
     * Creates all visual elements and arranges on the screen. Many labels will initially be set to zero.
     * @param coresUsed
     */
    public AlgorithmStatisticsVisualiser(long coresUsed) {

        // Initialize elements
        _boundGrid = createBoundingVisualization();
        _boundingAxis = createBoundingVisualizationAxis();

        _timeLabel = new Label("0");
        _timeLabel.setFont(DEFAULT_TIME_FONT);

        Label processorsUsedLabel = new Label("Cores running:");
        processorsUsedLabel.setFont(DEFAULT_FONT);
        Label processorsUsedValue = new Label(String.format("%d", coresUsed));
        processorsUsedValue.setFont(DEFAULT_FONT);

        Label branchesCoveredLabel = new Label("Branches explored:");
        branchesCoveredLabel.setFont(DEFAULT_FONT);
        _branchesCoveredValue = new Label(String.format("%d", 0));
        _branchesCoveredValue.setFont(DEFAULT_FONT);

        Label branchesCulledLabel = new Label("Branches culled:");
        branchesCulledLabel.setFont(DEFAULT_FONT);
        _branchesCulledValue = new Label(String.format("%d", 0));
        _branchesCulledValue.setFont(DEFAULT_FONT);

        Label cullingRateLabel = new Label("Culling rate:");
        cullingRateLabel.setFont(DEFAULT_FONT);
        _cullingRateValue = new Label(String.format("%.1f%%", 0.0));
        _cullingRateValue.setFont(DEFAULT_FONT);

        Label memoryFreeLabel = new Label("Memory free:");
        memoryFreeLabel.setFont(DEFAULT_FONT);
        _memoryFreeValue = new Label(String.format(""));
        _memoryFreeValue.setFont(DEFAULT_FONT);

        Label memoryAllocatedLabel = new Label("Memory Allocated:");
        memoryAllocatedLabel.setFont(DEFAULT_FONT);
        _memoryAllocatedValue = new Label("");
        _memoryAllocatedValue.setFont(DEFAULT_FONT);

        Label memoryMaxLabel = new Label("JVM Memory Limit:");
        memoryMaxLabel.setFont(DEFAULT_FONT);
        _memoryMaxValue = new Label("");
        _memoryMaxValue.setFont(DEFAULT_FONT);

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
        _cpuChart = createCpuChart();
        _cpuChartData = new XYChart.Series();
        _cpuChart.getData().add(_cpuChartData);
        _updateIteration = 0;


        // Start timer
        _millisecondsRunning = 0;
        _timer = new Timer();
        _timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                _millisecondsRunning += 10;
            }
        }, new Date(), 10);

        // Create horizontal box with misc. labels to the left of cpu chart
        HBox labelAndCpuVBox = new HBox();
        labelAndCpuVBox.setPadding(new Insets(15));
        labelAndCpuVBox.getChildren().addAll(_labelGrid, _cpuChart);

        // Create vertical box with the bounding visualization and it's axis in vertical alignment
        VBox boundVBox = new VBox();
        boundVBox.setPadding(new Insets(5));
        boundVBox.getChildren().addAll(_boundGrid, _boundingAxis);

        // Add previously created boxes into VBox; one on top of another
        VBox outerVBox = new VBox();
        outerVBox.setPadding(new Insets(15));
        outerVBox.getChildren().addAll(labelAndCpuVBox, boundVBox);

        // Add final VBox to this regions children
        getChildren().addAll(outerVBox);
    }


    /**
     * This method is responsible for updating the state of a schedule time bounding visualisation, as well as updating
     * the values of several on screen statistics in text-format.
     * @param statistics
     */
    public void update(Statistics statistics) {
        _updateIteration++; // Increment iteration of update

        // If on first iteration then update all of these parameters
        if (_updateIteration == 1) {
            _initialUpperBound = statistics.getMaxScheduleBound();
            _initialLowerBound = 0;
            _boundingAxis.setUpperBound(_initialUpperBound);
            _initialBoundRange = _initialUpperBound - _initialLowerBound;

            _boundingAxis.setTickUnit((_initialUpperBound - _initialLowerBound) / 20);

            _bestLower = _initialLowerBound;
            _bestUpper = _initialUpperBound;
        }

        updateBoundingChart(statistics); // Update bounding chart

        updateCpuUsageChart(); // Update CPU usage chart

        updateTimeLabel(); // Update Time label

        updateLabels(statistics); // Update misc. statistics labels

    }


    /**
     * To be called when Visualizer is intended to stop
     */
    public void stop() {

        _timeLabel.setTextFill(FINISHING_TIMER_FONT_FILL);
        _timer.cancel();

        // Render finishing rectangle marker in middle column :)
        Rectangle finishIndicator = new Rectangle();
        finishIndicator.setStroke(FINISHING_TICK_MARK_FILL);
        finishIndicator.setStrokeWidth(5);
        finishIndicator.setHeight(SCHEDULE_TIME_BOUNDING_HEIGHT);
        _boundGrid.add(finishIndicator, 1, 0);

    }


    /**
     * Sets the time elapsed from the start of the counter of _millisecondsRunning and converts into the form
     * hh:mm:ss:ms
     * @return
     */
    private void updateTimeLabel() {
        _timeLabel.setText(String.format(String.format("%02d:%02d:%02d.%01d",
                _millisecondsRunning/(3600*1000),
                _millisecondsRunning/(60*1000) % 60,
                _millisecondsRunning/1000 % 60,
                _millisecondsRunning % 1000 / 100)));
    }


    /**
     * Updates the labels of miscellaneous algorithm metrics branches culled, search space, etc.
     * @param statistics
     */
    private void updateLabels(Statistics statistics) {

        _branchesCoveredValue.setText(String.format("%6.2e", (float)statistics.getSearchSpaceLookedAt()));
        _branchesCulledValue.setText(String.format("%6.2e", (float)statistics.getSearchSpaceCulled()));
        _cullingRateValue.setText(String.format("%.1f%%",  100 * (float)statistics.getSearchSpaceCulled() / (statistics.getSearchSpaceLookedAt() + statistics.getSearchSpaceCulled())));

        NumberFormat format = NumberFormat.getInstance();
        Runtime runtime = Runtime.getRuntime(); // For the commas in 100,000

        _memoryFreeValue.setText(String.format("%sM", format.format(runtime.freeMemory() / (1024 * 1024)))); // Convert to Megabytes
        _memoryAllocatedValue.setText(String.format("%sM", format.format(runtime.totalMemory() / (1024 * 1024))));
        _memoryMaxValue.setText(String.format("%sM", format.format(runtime.maxMemory() / (1024 * 1024))));

    }


    /**
     * Update the bounding chart to appropriate state given the current min and max boundary times. This chart re-renders both rectangles if one changes.
     * @param statistics
     */
    private void updateBoundingChart(Statistics statistics) {

        // If neither bound changes, no point in continuing to render
        if (statistics.getMinScheduleBound() == _bestLower && statistics.getMaxScheduleBound() == _bestUpper)
            return;

        // If current statistics are better than locally stored bests, overwrite.
        _bestLower = statistics.getMinScheduleBound() > _bestLower ? statistics.getMinScheduleBound() : _bestLower;
        _bestUpper = statistics.getMaxScheduleBound() < _bestUpper ? statistics.getMaxScheduleBound() : _bestUpper;

        // calculate left and right rectangle widths with regards to bound progression and visualisation width
        int leftRectangleWidth = (int)(((_bestLower - _initialLowerBound) / _initialBoundRange) * SCHEDULE_TIME_BOUNDING_WIDTH);
        int rightRectangleWidth = (int)(((_initialUpperBound - _bestUpper) / _initialBoundRange) * SCHEDULE_TIME_BOUNDING_WIDTH);

        // Create rectangles with width and color features
        Rectangle leftRectangle = new Rectangle(leftRectangleWidth, SCHEDULE_TIME_BOUNDING_HEIGHT);
        Rectangle rightRectangle = new Rectangle(rightRectangleWidth, SCHEDULE_TIME_BOUNDING_HEIGHT);
        leftRectangle.setFill(BOUNDING_RECTANGLE_FILL);
        leftRectangle.setStroke(BOUNDING_RECTANGLE_STROKE_FILL);
        rightRectangle.setFill(BOUNDING_RECTANGLE_FILL);
        rightRectangle.setStroke(BOUNDING_RECTANGLE_STROKE_FILL);

        // Clear previous column information of chart and update with current columns
        _boundGrid.getColumnConstraints().clear();
        _boundGrid.getColumnConstraints().addAll(
                new ColumnConstraints(leftRectangleWidth),
                new ColumnConstraints(SCHEDULE_TIME_BOUNDING_WIDTH - leftRectangleWidth - rightRectangleWidth),
                new ColumnConstraints(rightRectangleWidth));

        // Add newly made rectangles into columns
        _boundGrid.add(leftRectangle, 0, 0);
        _boundGrid.add(rightRectangle, 2, 0);
    }


    /**
     * Calculate the current CPU usage of the OS and plot it onto chart
     */
    private void updateCpuUsageChart() {
        if (_updateIteration % 20 == 0) { // Only update chart every 25 iterations of updates.
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
            } catch (Exception e) {
            }

            // Add current cpu usage to chart data structure, chart will automatically update. Divide by 1000 for seconds
            _cpuChartData.getData().add(new XYChart.Data(_millisecondsRunning / 1000, cpuUsage));
            if (_cpuChartData.getData().size() > 50) { // If data size is greater than 100 points begin to remove
                _cpuChartData.getData().remove(0, 1);
            }
        }
    }


    /**
     * Creates an empty grid to house miscellaneous metrics labels, branches culled, searched, etc.
     * @return
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
     * @return
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
        cpuChart.setMaxHeight(300);
        cpuChart.setTitle("CPU Usage");

        return cpuChart;
    }


    /**
     * Create a grid pane with constant height and no columns
     * @return
     */
    private GridPane createBoundingVisualization() {

        GridPane grid = new GridPane();

        // Only need to create row with constant height as no real bounding information is yet available
        RowConstraints rc = new RowConstraints(SCHEDULE_TIME_BOUNDING_HEIGHT);
        grid.getRowConstraints().addAll(rc);

        return grid;
    }


    /**
     * Creates an axis intended for bounding visualization
     * @return
     */
    private NumberAxis createBoundingVisualizationAxis() {

        // Set uper and lower bounds to that of initial schedule estimates, set tick marks to be 1/20th of the way across
        NumberAxis numberAxis = new NumberAxis("Schedule Time Units", _initialLowerBound, _initialUpperBound, (_initialUpperBound - _initialLowerBound) / 20);

        return numberAxis;
    }

}
