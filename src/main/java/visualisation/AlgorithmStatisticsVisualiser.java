package visualisation;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AlgorithmStatisticsVisualiser extends Region {

    // Constant element / window element dimensions
    private static final double SCHEDULE_TIME_BOUNDING_HEIGHT = 200;
    private static final double SCHEDULE_TIME_BOUNDING_WIDTH = 800;
    private static final double WINDOW_HEIGHT = 200;
    private static final double WINDOW_WIDTH = 500;

    // Should stay constant once assigned, correspond to initial upper / lower bounds on schedule time once algorithm starts
    private final double _initialLowerBound;
    private final double _initialUpperBound;
    private final double _initialBoundRange;

    // Timer
    private final Timer _timer;
    private int _millisecondsRunning;

    public AlgorithmStatisticsVisualiser(int initialLowerBound, int initialUpperBound) {
        // Set bounding variables
        _initialLowerBound = initialLowerBound;
        _initialUpperBound = initialUpperBound;
        _initialBoundRange = _initialUpperBound - _initialLowerBound; // Range of upper and lower bound

        // Start timer
        _millisecondsRunning = 0;
        _timer = new Timer();
        _timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                _millisecondsRunning += 100; // set label time
            }
        }, new Date(), 100);
    }

    /**
     * This method is responsible for updating the state of a schedule time bounding visualisation, as well as updating
     * the values of several on screen statistics in text-format.
     * @param statistics
     */
    public void update(Statistics statistics) {

        getChildren().clear();

        GridPane grid = setTimeBoundingDimensions(statistics.getMinScheduleBound(), statistics.getMaxScheduleBound());
        Label timerLabel = setTimerLabel();


        VBox vBox = new VBox();
        vBox.setPadding(new Insets(15));
        vBox.getChildren().addAll(grid, timerLabel);

        getChildren().addAll(vBox);

    }


    private GridPane setTimeBoundingDimensions(int minScheduleBound, int maxScheduleBound) {

        GridPane grid = new GridPane();


        // Calculate height of upper and lower rectangle in visualisation, mid rectangle is remaining height landing in between them.
        double leftRectangleWidth = ((minScheduleBound - _initialLowerBound) / _initialBoundRange) * SCHEDULE_TIME_BOUNDING_WIDTH;
        double rightRectangleWidth = ((_initialUpperBound - maxScheduleBound) / _initialBoundRange) * SCHEDULE_TIME_BOUNDING_WIDTH;
        double midRectangleWidth = SCHEDULE_TIME_BOUNDING_WIDTH - rightRectangleWidth - leftRectangleWidth;


        // Assign appropriate grid row heights to corresponding rectangle heights
        ColumnConstraints rightCC = new ColumnConstraints(rightRectangleWidth);
        ColumnConstraints midCC = new ColumnConstraints(midRectangleWidth);
        ColumnConstraints leftCC = new ColumnConstraints(leftRectangleWidth);

        // Column width will be constant
        RowConstraints rc = new RowConstraints(SCHEDULE_TIME_BOUNDING_HEIGHT);


        grid.getRowConstraints().addAll(rc);
        grid.getColumnConstraints().addAll(rightCC, midCC, leftCC);

        grid.setGridLinesVisible(true);


        return grid;
    }


    private Label setTimerLabel() {
        return new Label(Integer.toString(_millisecondsRunning));
    }
}
