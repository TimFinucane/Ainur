package visualisation;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AlgorithmStatisticsVisualiser extends Region {

    // Constant element / window element dimensions
    private static final int SCHEDULE_TIME_BOUNDING_HEIGHT = 200;
    private static final int SCHEDULE_TIME_BOUNDING_WIDTH = 800;
    private static final double WINDOW_HEIGHT = 200;
    private static final double WINDOW_WIDTH = 500;

    // Should stay constant once assigned, correspond to initial upper / lower bounds on schedule time once algorithm starts
    private final double _initialLowerBound;
    private final double _initialUpperBound;
    private final double _initialBoundRange;

    // Grid
    private final GridPane _boundGrid;

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


        _boundGrid = createGrid();
        _timeLabel = new Label("0");


        // Start timer
        _millisecondsRunning = 0;
        _timer = new Timer();
        _timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                _millisecondsRunning += 10;
            }
        }, new Date(), 10);


        VBox vBox = new VBox();
        vBox.setPadding(new Insets(15));
        vBox.getChildren().addAll(_boundGrid, _timeLabel);

        getChildren().addAll(vBox);
    }

    /**
     * This method is responsible for updating the state of a schedule time bounding visualisation, as well as updating
     * the values of several on screen statistics in text-format.
     * @param statistics
     */
    public void update(Statistics statistics) {

        // Update bounding visualization
        int leftRectangleWidth = (int)(((statistics.getMinScheduleBound() - _initialLowerBound) / _initialBoundRange) * SCHEDULE_TIME_BOUNDING_WIDTH);
        int rightRectangleWidth = (int)(((_initialUpperBound - statistics.getMaxScheduleBound()) / _initialBoundRange) * SCHEDULE_TIME_BOUNDING_WIDTH);

        Rectangle leftRectangle = new Rectangle(leftRectangleWidth, SCHEDULE_TIME_BOUNDING_HEIGHT);
        Rectangle rightRectangle = new Rectangle(rightRectangleWidth, SCHEDULE_TIME_BOUNDING_HEIGHT);
        leftRectangle.setFill(Color.LAVENDER);
        rightRectangle.setFill(Color.LAVENDER);

        _boundGrid.getColumnConstraints().clear();
        _boundGrid.getColumnConstraints().addAll(
                new ColumnConstraints(leftRectangleWidth),
                new ColumnConstraints(SCHEDULE_TIME_BOUNDING_WIDTH - leftRectangleWidth - rightRectangleWidth),
                new ColumnConstraints(rightRectangleWidth));

        _boundGrid.add(leftRectangle, 0, 0);
        _boundGrid.add(rightRectangle, 2, 0);


        // Update timer
        _timeLabel.setText(String.format(String.format("%02d:%02d:%02d.%01d",
                _millisecondsRunning/(3600*1000),
                _millisecondsRunning/(60*1000) % 60,
                _millisecondsRunning/1000 % 60,
                _millisecondsRunning % 1000 / 100)));

    }


    private GridPane createGrid() {

        GridPane grid = new GridPane();

        RowConstraints rc = new RowConstraints(SCHEDULE_TIME_BOUNDING_HEIGHT);
        grid.getRowConstraints().addAll(rc);
        grid.setGridLinesVisible(true); // TODO remove when done

        return grid;
    }

}
