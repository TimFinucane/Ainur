package visualisation.modules.statistics;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class StatisticsVisualiser extends GridPane {
    // Label Values that need to be periodically updated through life of visualizer
    private Label _branchesCoveredValue; // Metrics labels
    private Label _branchesCulledValue;
    private Label _cullingRateValue;
    private Label _memoryFreeValue;
    private Label _memoryAllocatedValue;
    private Label _memoryMaxValue;

    public StatisticsVisualiser(int coresUsed) {
        this.setPadding(new Insets(20));
        getColumnConstraints().addAll(
            new ColumnConstraints(135, 200, -1, Priority.SOMETIMES, HPos.LEFT, true),
            new ColumnConstraints(75, 100, -1, Priority.SOMETIMES, HPos.RIGHT, true)
        );

        RowConstraints rc = new RowConstraints(20, 70, -1);
        rc.setMinHeight(100);
        getRowConstraints().add(rc);

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
        BigDecimal searchSpaceCulledAsBigDec = new BigDecimal(spaceCulled);
        BigDecimal searchSpaceLookedAtAsBigDec = new BigDecimal(spaceLookedAt);

        if (!searchSpaceLookedAtAsBigDec.equals(BigDecimal.ZERO)) { // We only want to divide if we know for sure the algorithm has some metrics for us
            BigDecimal proportionCulled = searchSpaceCulledAsBigDec.divide(searchSpaceCulledAsBigDec.add(searchSpaceLookedAtAsBigDec), 5, BigDecimal.ROUND_HALF_UP);
            _cullingRateValue.setText(NumberFormat.getPercentInstance().format(proportionCulled.floatValue() * 100));
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
