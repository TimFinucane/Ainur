package visualisation.modules;

import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class StatisticsVisualiser extends GridPane {
    private static final double LABEL_GRID_COLUMN_WIDTH = 150;
    private static final double LABEL_GRID_ROW_HEIGHT = 20;

    private static final String TIME_LABEL_CLASS_CSS = "time-label";
    private static final String STATS_CONTENT_CLASS_CSS = "stats-content";
    private static final String TIME_LABEL_FINISH_CLASS_CSS = "time-label-finished";
    private static final String FINISHED_LABEL_CLASS = "finished-label";

    // Label Values that need to be periodically updated through life of visualizer
    private final Label _timeLabel;

    private Label _branchesCoveredValue; // Metrics labels
    private Label _branchesCulledValue;
    private Label _cullingRateValue;
    private Label _memoryFreeValue;
    private Label _memoryAllocatedValue;
    private Label _memoryMaxValue;

    private Label _finishedLabel;

    public StatisticsVisualiser(int coresUsed) {
        getColumnConstraints().addAll(
            new ColumnConstraints(LABEL_GRID_COLUMN_WIDTH),
            new ColumnConstraints(80)
        );

        RowConstraints rc = new RowConstraints(LABEL_GRID_ROW_HEIGHT);
        rc.setMinHeight(100);
        getRowConstraints().add(rc);

        _timeLabel = new Label("0");
        _timeLabel.getStyleClass().add(TIME_LABEL_CLASS_CSS);

        Label processorsUsedLabel = new Label("Cores running:");
        Label branchesCoveredLabel = new Label("Branches explored:");
        Label branchesCulledLabel = new Label("Branches culled:");
        Label cullingRateLabel = new Label("Culling rate:");
        Label memoryFreeLabel = new Label("Memory free:");
        Label memoryAllocatedLabel = new Label("Memory Allocated:");
        Label memoryMaxLabel = new Label("JVM Memory Limit:");

        Label processorsUsedValue = new Label(String.format("%d", coresUsed));
        _branchesCoveredValue = new Label("0");
        _branchesCulledValue = new Label("0");
        _cullingRateValue = new Label("0%");
        _memoryFreeValue = new Label("");
        _memoryAllocatedValue = new Label("");
        _memoryMaxValue = new Label("");

        // Add label elements to label grid so are aligned
        // Holds the following labels
        add(_timeLabel,             0, 0, 2, 1);
        add(processorsUsedLabel,    0, 1);
        add(processorsUsedValue,    1, 1);
        add(branchesCoveredLabel,   0, 2);
        add(_branchesCoveredValue,  1, 2);
        add(branchesCulledLabel,    0, 3);
        add(_branchesCulledValue,   1, 3);
        add(cullingRateLabel,       0, 4);
        add(_cullingRateValue,      1, 4);
        add(memoryFreeLabel,        0, 5);
        add(_memoryFreeValue,       1, 5);
        add(memoryAllocatedLabel,   0, 6);
        add(_memoryAllocatedValue,  1, 6);
        add(memoryMaxLabel,         0, 7);
        add(_memoryMaxValue,        1, 7);

        _finishedLabel = new Label("SCHEDULING COMPLETE");
        _finishedLabel.getStyleClass().add(FINISHED_LABEL_CLASS);
        _finishedLabel.setVisible(false);
    }

    /**
     * Updates the labels of miscellaneous algorithm metrics branches culled, search space, etc.
     * @param spaceLookedAt Number of search space nodes explored
     * @param spaceCulled Number of search space nodes culled
     */
    public void update(BigInteger spaceLookedAt, BigInteger spaceCulled) {
        NumberFormat numberFormat = new DecimalFormat("0.00E0");

        _branchesCoveredValue.setText(numberFormat.format(spaceLookedAt));
        _branchesCulledValue.setText(numberFormat.format(spaceCulled));

        // Big integer division requires that you convert into decimals so as to not lose precision (as integer division does).
        BigDecimal searchSpaceCulledAsBigDec = new BigDecimal(spaceLookedAt);
        BigDecimal searchSpaceLookedAtAsBigDec = new BigDecimal(spaceLookedAt);

        if (!searchSpaceLookedAtAsBigDec.equals(BigDecimal.ZERO)) { // We only want to divide if we know for sure the algorithm has some metrics for us
            double proportionCulled = searchSpaceCulledAsBigDec.divide(searchSpaceCulledAsBigDec.add(searchSpaceLookedAtAsBigDec), MathContext.DECIMAL32).floatValue();
            _cullingRateValue.setText(NumberFormat.getPercentInstance().format(100.0 * proportionCulled));
        }

        NumberFormat format = NumberFormat.getIntegerInstance();
        Runtime runtime = Runtime.getRuntime();

        // Convert to Megabytes
        int mega = 1024 * 1024;
        _memoryFreeValue.setText(       format.format(runtime.freeMemory()  / mega) + "Mb");
        _memoryAllocatedValue.setText(  format.format(runtime.totalMemory() / mega) + "Mb");
        _memoryMaxValue.setText(        format.format(runtime.maxMemory()   / mega) + "Mb");

        // Timer
    }
}
