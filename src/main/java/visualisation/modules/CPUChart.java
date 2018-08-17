package visualisation.modules;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * A line chart labelled with CPU usage information.
 */
public class CPUChart extends HBox {
    private static final String STATS_CONTENT_CLASS_CSS = "stats-content";

    private final double _updateFrequency;
    private double _time = 0;

    private XYChart.Series<Number, Number> _chartData = new XYChart.Series<>();

    /**
     * Creates the CPU chart
     * @param updateFrequency The frequency at which updates will be called, in seconds
     */
    public CPUChart(double updateFrequency) {
        _updateFrequency = updateFrequency;

        // Create number axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time (s)");
        xAxis.forceZeroInRangeProperty().setValue(false); // Allow x axis to scroll
        yAxis.setLabel("CPU Usage (%)");

        // Create LineChart
        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setCreateSymbols(false);
        chart.setLegendVisible(false);
        chart.setTitle("CPU Usage");

        chart.setMinHeight(200);
        chart.setMinWidth(200);

        chart.getData().add(_chartData);

        getChildren().add(chart);
        HBox.setHgrow(chart, Priority.SOMETIMES);

        _chartData.getNode().getStyleClass().add(STATS_CONTENT_CLASS_CSS);
    }

    /**
     * Calculate the current CPU usage of the OS and plot it onto chart
     */
    public void update() {
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
        } catch (Exception e) { /* Nothing should happen here imo */ }

        // Add current cpu usage to chart data structure, chart will automatically update. Divide by 1000 for seconds
        _chartData.getData().add(new XYChart.Data<>(_time, cpuUsage));
        if (_chartData.getData().size() > 50) { // If data size is greater than 100 points begin to remove
            _chartData.getData().remove(0, 1);
        }
        _time += _updateFrequency;
    }
}
