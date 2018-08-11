package visualisation;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;

public class AlgorithmStatisticsVisualiser extends Region {

    // Constant element / window element dimensions
    private static final double SCHEDULE_TIME_BOUNDING_HEIGHT = 300;
    private static final double SCHEDULE_TIME_BOUNDING_WIDTH = 200;
    private static final double WINDOW_HEIGHT = 200;
    private static final double WINDOW_WIDTH = 500;

    // Should stay constant once assigned, correspond to initial upper / lower bounds on schedule time once algorithm starts
    private final double _initialLowerBound;
    private final double _initialUpperBound;

    public AlgorithmStatisticsVisualiser(int initialLowerBound, int initialUpperBound) {
        _initialLowerBound = initialLowerBound;
        _initialUpperBound = initialUpperBound;
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


        double initialBoundWidth = _initialUpperBound - _initialLowerBound;

        double lowerRectangleHeight = ((minScheduleBound - _initialLowerBound) / initialBoundWidth) * SCHEDULE_TIME_BOUNDING_HEIGHT;
        double upperRectangleHeight = ((_initialUpperBound - maxScheduleBound) / initialBoundWidth) * SCHEDULE_TIME_BOUNDING_HEIGHT;
        double midRectangleHeight = SCHEDULE_TIME_BOUNDING_HEIGHT - upperRectangleHeight - lowerRectangleHeight;


        RowConstraints topRC = new RowConstraints(upperRectangleHeight);
        RowConstraints midRC = new RowConstraints(midRectangleHeight);
        RowConstraints bottomRC = new RowConstraints(lowerRectangleHeight);

        ColumnConstraints cc = new ColumnConstraints(SCHEDULE_TIME_BOUNDING_WIDTH);


        grid.getRowConstraints().addAll(topRC, midRC, bottomRC);
        grid.getColumnConstraints().addAll(cc);

        grid.setGridLinesVisible(true);


        return grid;
    }

}
