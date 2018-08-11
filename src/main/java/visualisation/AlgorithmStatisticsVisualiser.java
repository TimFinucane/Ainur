package visualisation;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;

public class AlgorithmStatisticsVisualiser extends Region {

    private static final double SCHEDULE_TIME_BOUNDING_WIDTH = 200;

    private static final double WINDOW_HEIGHT = 200;
    private static final double WINDOW_WIDTH = 500;

    /**
     * This method is responsible for updating the state of a schedule time bounding visualisation, as well as updating
     * the values of several on screen statistics in text-format.
     * @param statistics
     */
    public void update(Statistics statistics) {


        GridPane grid = setEstimatorDimensions(statistics.getMinScheduleLength(), statistics.getMaxScheduleLength());

        getChildren().addAll(grid);

    }

    private GridPane setEstimatorDimensions(int minScheduuleLength, int maxScheduleLength) {

        GridPane grid = new GridPane();

        RowConstraints topRC = new RowConstraints(50);
        RowConstraints midRC = new RowConstraints(70);
        RowConstraints bottomRC = new RowConstraints(120);

        ColumnConstraints cc = new ColumnConstraints(SCHEDULE_TIME_BOUNDING_WIDTH);

        grid.getRowConstraints().addAll(topRC, midRC, bottomRC);
        grid.getColumnConstraints().addAll(cc);

        grid.setGridLinesVisible(true);

        return grid;
    }

}
