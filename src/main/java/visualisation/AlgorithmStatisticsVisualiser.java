package visualisation;

import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
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

public class AlgorithmStatisticsVisualiser extends Region {


    // Constant dimensions
    private static final int SCHEDULE_TIME_BOUNDING_HEIGHT = 100;
    private static final int SCHEDULE_TIME_BOUNDING_WIDTH = 750;

    private static final double LABEL_GRID_COLUMN_WIDTH = 150;
    private static final double LABEL_GRID_ROW_HEIGHT = 20;

    private static final Font DEFAULT_FONT = new Font("Consolas", 12);
    private static final Font DEFAULT_TIME_FONT = new Font("Consolas", 20);

    // Should stay constant once assigned, correspond to initial upper / lower bounds on schedule time once algorithm starts
    private final double _initialLowerBound;
    private final double _initialUpperBound;
    private final double _initialBoundRange;

    // For keeping track of number of updates, certain visualizations may use this to prevent over updating
    private long _updateIteration;

    // Grid
    private final GridPane _boundGrid;
    private final NumberAxis _boundingAxis;

    // Cpu chart
    private final LineChart<Number, Number> _cpuChart;
    private final XYChart.Series _cpuChartData;

    // Labels
    private final Label _timeLabel;
    private final GridPane _labelGrid; // Holds the following labels
    private final Label _processorsUsedLabel;
    private final Label _branchesCoveredLabel;
    private final Label _branchesCulledLabel;
    private final Label _cullingRateLabel;
    private final Label _memoryFreeLabel;
    private final Label _memoryAllocatedLabel;
    private final Label _memoryMaxLabel;
    private Label _processorsUsedValue;
    private Label _branchesCoveredValue;
    private Label _branchesCulledValue;
    private Label _cullingRateValue;
    private Label _memoryFreeValue;
    private Label _memoryAllocatedValue;
    private Label _memoryMaxValue;

    // Timer
    private final Timer _timer;
    private long _millisecondsRunning;


    /**
     * Creates all visual elements and arranges on the screen. Many labels will initially be set to zero.
     * @param initialLowerBound
     * @param initialUpperBound
     * @param coresUsed
     */
    public AlgorithmStatisticsVisualiser(int initialLowerBound, int initialUpperBound, long coresUsed) {
        // Set bounding variables
        _initialLowerBound = initialLowerBound;
        _initialUpperBound = initialUpperBound;
        _initialBoundRange = _initialUpperBound - _initialLowerBound; // Range of upper and lower bound

        // Initialize elements
        _boundGrid = createBoundingVisualization();
        _boundingAxis = createBoundingVisualizationAxis();

        _timeLabel = new Label("0");
        _timeLabel.setFont(DEFAULT_TIME_FONT);

        _processorsUsedLabel = new Label("Cores running:");
        _processorsUsedLabel.setFont(DEFAULT_FONT);
        _processorsUsedValue = new Label(String.format("%d", coresUsed));
        _processorsUsedValue.setFont(DEFAULT_FONT);

        _branchesCoveredLabel = new Label("Branches covered:");
        _branchesCoveredLabel.setFont(DEFAULT_FONT);
        _branchesCoveredValue = new Label(String.format("%d", 0));
        _branchesCoveredValue.setFont(DEFAULT_FONT);

        _branchesCulledLabel = new Label("Branches culled:");
        _branchesCulledLabel.setFont(DEFAULT_FONT);
        _branchesCulledValue = new Label(String.format("%d", 0));
        _branchesCulledValue.setFont(DEFAULT_FONT);

        _cullingRateLabel = new Label("Culling rate:");
        _cullingRateLabel.setFont(DEFAULT_FONT);
        _cullingRateValue = new Label(String.format("%.1f%%", 0.0));
        _cullingRateValue.setFont(DEFAULT_FONT);

        _memoryFreeLabel = new Label("Memory free:");
        _memoryFreeLabel.setFont(DEFAULT_FONT);
        _memoryFreeValue = new Label(String.format(""));
        _memoryFreeValue.setFont(DEFAULT_FONT);

        _memoryAllocatedLabel = new Label("Memory Allocated:");
        _memoryAllocatedLabel.setFont(DEFAULT_FONT);
        _memoryAllocatedValue = new Label("");
        _memoryAllocatedValue.setFont(DEFAULT_FONT);

        _memoryMaxLabel = new Label("JVM Memory Limit:");
        _memoryMaxLabel.setFont(DEFAULT_FONT);
        _memoryMaxValue = new Label("");
        _memoryMaxValue.setFont(DEFAULT_FONT);

        // Add label elements to label grid so are aligned
        _labelGrid = createLabelGrid();
        _labelGrid.add(_timeLabel, 0, 0);
        _labelGrid.add(_processorsUsedLabel, 0, 1);
        _labelGrid.add(_processorsUsedValue, 1, 1);
        _labelGrid.add(_branchesCoveredLabel, 0, 2);
        _labelGrid.add(_branchesCoveredValue, 1, 2);
        _labelGrid.add(_branchesCulledLabel, 0, 3);
        _labelGrid.add(_branchesCulledValue, 1, 3);
        _labelGrid.add(_cullingRateLabel, 0, 4);
        _labelGrid.add(_cullingRateValue, 1, 4);
        _labelGrid.add(_memoryFreeLabel, 0, 5);
        _labelGrid.add(_memoryFreeValue, 1, 5);
        _labelGrid.add(_memoryAllocatedLabel, 0, 6);
        _labelGrid.add(_memoryAllocatedValue, 1, 6);
        _labelGrid.add(_memoryMaxLabel, 0, 7);
        _labelGrid.add(_memoryMaxValue, 1, 7);

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
     * This method is responsible for updating the state of a schedule time bounding visualisation, as well as updating
     * the values of several on screen statistics in text-format.
     * @param statistics
     */
    public void update(Statistics statistics) {
        _updateIteration++; // Increment iteration of update

        updateBoundingChart(statistics); // Update bounding chart

        updateCpuUsageChart(); // Update CPU usage chart

        updateTimeLabel(); // Update Time label

        updateLabels(statistics); // Update misc. statistics labels

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


    private void updateLabels(Statistics statistics) {

        _branchesCoveredValue.setText(String.format("%6.2e", (float)statistics.getSearchSpaceLookedAt()));
        _branchesCulledValue.setText(String.format("%6.2e", (float)statistics.getSearchSpaceCulled()));
        _cullingRateValue.setText(String.format("%.1f%%",  100 * (float)statistics.getSearchSpaceCulled() / statistics.getSearchSpaceLookedAt()));

        NumberFormat format = NumberFormat.getInstance();
        Runtime runtime = Runtime.getRuntime(); // For the commas in 100,000

        _memoryFreeValue.setText(String.format("%sM", format.format(runtime.freeMemory() / (1024 * 1024)))); // Convert to Megabytes
        _memoryAllocatedValue.setText(String.format("%sM", format.format(runtime.totalMemory() / (1024 * 1024))));
        _memoryMaxValue.setText(String.format("%sM", format.format(runtime.maxMemory() / (1024 * 1024))));

    }


    /**
     * Update the bounding chart to appropriate state given the current min and max boundary times.
     * @param statistics
     */
    private void updateBoundingChart(Statistics statistics) {
        // calculate left and right rectangle widths with regards to bound progression and visualisation width
        int leftRectangleWidth = (int)(((statistics.getMinScheduleBound() - _initialLowerBound) / _initialBoundRange) * SCHEDULE_TIME_BOUNDING_WIDTH);
        int rightRectangleWidth = (int)(((_initialUpperBound - statistics.getMaxScheduleBound()) / _initialBoundRange) * SCHEDULE_TIME_BOUNDING_WIDTH);

        // Create rectangles with width and color features
        Rectangle leftRectangle = new Rectangle(leftRectangleWidth, SCHEDULE_TIME_BOUNDING_HEIGHT);
        Rectangle rightRectangle = new Rectangle(rightRectangleWidth, SCHEDULE_TIME_BOUNDING_HEIGHT);
        leftRectangle.setFill(Paint.valueOf("#b475d6"));
        rightRectangle.setFill(Paint.valueOf("#b475d6"));

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
