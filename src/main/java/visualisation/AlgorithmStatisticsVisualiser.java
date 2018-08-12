package visualisation;

import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AlgorithmStatisticsVisualiser extends Region {


    // Constant element / window element dimensions
    private static final int SCHEDULE_TIME_BOUNDING_HEIGHT = 200;
    private static final int SCHEDULE_TIME_BOUNDING_WIDTH = 800;

    private static final Font DEFAULT_FONT = new Font("Courier New", 12);

    // Should stay constant once assigned, correspond to initial upper / lower bounds on schedule time once algorithm starts
    private final double _initialLowerBound;
    private final double _initialUpperBound;
    private final double _initialBoundRange;

    // For keeping track of number of updates, certain visualizations may use this to prevent over updating
    private long _updateIteration;

    // Grid
    private final GridPane _boundGrid;

    // Cpu chart
    private final LineChart<Number, Number> _cpuChart;
    private final XYChart.Series _cpuChartData;

    // Labels
    private final Label _timeLabel;

    // Timer
    private final Timer _timer;
    private long _millisecondsRunning;



    public AlgorithmStatisticsVisualiser(int initialLowerBound, int initialUpperBound) {
        // Set bounding variables
        _initialLowerBound = initialLowerBound;
        _initialUpperBound = initialUpperBound;
        _initialBoundRange = _initialUpperBound - _initialLowerBound; // Range of upper and lower bound

        // Initialize elements
        _boundGrid = createGrid();

        _timeLabel = new Label("0");
        _timeLabel.setFont(DEFAULT_FONT);

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

        // Add elements to children of group
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(15));
        vBox.getChildren().addAll(_boundGrid, _timeLabel, _cpuChart);

        getChildren().addAll(vBox);
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
        leftRectangle.setFill(Color.MEDIUMVIOLETRED);
        rightRectangle.setFill(Color.MEDIUMVIOLETRED);

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
            if (_cpuChartData.getData().size() > 200) { // If data size is greater than 100 points begin to remove
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
        cpuChart.setTitle("CPU Usage");

        return cpuChart;
    }



    /**
     * Create a grid pane with constant height and no columns
     * @return
     */
    private GridPane createGrid() {

        GridPane grid = new GridPane();

        // Only need to create row with constant height as no real bounding information is yet available
        RowConstraints rc = new RowConstraints(SCHEDULE_TIME_BOUNDING_HEIGHT);
        grid.getRowConstraints().addAll(rc);

        return grid;
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

}
