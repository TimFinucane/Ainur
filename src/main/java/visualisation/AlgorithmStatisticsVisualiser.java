package visualisation;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AlgorithmStatisticsVisualiser extends Region {

    // Constant element / window element dimensions
    private static final double SCHEDULE_TIME_BOUNDING_HEIGHT = 300;
    private static final double SCHEDULE_TIME_BOUNDING_WIDTH = 200;
    private static final double WINDOW_HEIGHT = 200;
    private static final double WINDOW_WIDTH = 500;

    // Should stay constant once assigned, correspond to initial upper / lower bounds on schedule time once algorithm starts
    private final double _initialLowerBound;
    private final double _initialUpperBound;
    private final double _initialBoundRange;

    // Timer
    private final Timer _timer;
    private int _secondsRunning;

    public AlgorithmStatisticsVisualiser(int initialLowerBound, int initialUpperBound) {
        // Set bounding variables
        _initialLowerBound = initialLowerBound;
        _initialUpperBound = initialUpperBound;
        _initialBoundRange = _initialUpperBound - _initialLowerBound; // Range of upper and lower bound

        // Start timer
        _secondsRunning = 0;
        _timer = new Timer();
        _timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                _secondsRunning++;
            }
        }, new Date(), 1000);
    }

    /**
     * This method is responsible for updating the state of a schedule time bounding visualisation, as well as updating
     * the values of several on screen statistics in text-format.
     * @param statistics
     */
    public void update(Statistics statistics) {

        getChildren().clear();

        GridPane grid = setTimeBoundingDimensions(statistics.getMinScheduleBound(), statistics.getMaxScheduleBound());

        getChildren().addAll(grid);

    }

    private GridPane setTimeBoundingDimensions(int minScheduleBound, int maxScheduleBound) {

        GridPane grid = new GridPane();


        // Calculate height of upper and lower rectangle in visualisation, mid rectangle is remaining height landing in between them.
        double lowerRectangleHeight = ((minScheduleBound - _initialLowerBound) / _initialBoundRange) * SCHEDULE_TIME_BOUNDING_HEIGHT;
        double upperRectangleHeight = ((_initialUpperBound - maxScheduleBound) / _initialBoundRange) * SCHEDULE_TIME_BOUNDING_HEIGHT;
        double midRectangleHeight = SCHEDULE_TIME_BOUNDING_HEIGHT - upperRectangleHeight - lowerRectangleHeight;


        // Assign appropriate grid row heights to corresponding rectangle heights
        RowConstraints topRC = new RowConstraints(upperRectangleHeight);
        RowConstraints midRC = new RowConstraints(midRectangleHeight);
        RowConstraints bottomRC = new RowConstraints(lowerRectangleHeight);

        // Column width will be constant
        ColumnConstraints cc = new ColumnConstraints(SCHEDULE_TIME_BOUNDING_WIDTH);


        grid.getRowConstraints().addAll(topRC, midRC, bottomRC);
        grid.getColumnConstraints().addAll(cc);

        grid.setGridLinesVisible(true);


        return grid;
    }

}
