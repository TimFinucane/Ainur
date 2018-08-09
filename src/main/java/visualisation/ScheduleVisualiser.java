package visualisation;

import common.schedule.Schedule;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

public class ScheduleVisualiser extends Group {

    private static final int WINDOW_WIDTH = 750;
    private static final int WINDOW_HEIGHT = 200;
    private double rowHeight;
    private double colWidth;


    public ScheduleVisualiser(Schedule schedule){
        int endTime = schedule.getEndTime();
        int numProc = schedule.getNumProcessors();
    }

    public ScheduleVisualiser(int endTime, int numProc){
        rowHeight = WINDOW_HEIGHT/numProc;
        colWidth = WINDOW_WIDTH/endTime;
        update(endTime, numProc);
    }

    public void update(int endTime, int numProc) {

        GridPane grid = setDimensions(endTime, numProc);

        Rectangle rectangle = new Rectangle();
        rectangle.setHeight(rowHeight);
        rectangle.setWidth(colWidth * 3);

        rectangle.setFill(Color.BLACK);

        grid.add(rectangle, 0, 0);

        grid.add(new Button(), 3, 3, 2, 2);
        grid.setGridLinesVisible(true);


        this.getChildren().add(grid);

    }

    private GridPane setDimensions(int endTime, int numProc) {
        GridPane grid = new GridPane();

        int colWidth = WINDOW_WIDTH/endTime;
        for (int i = 0; i < endTime; i++) {
            ColumnConstraints cc = new ColumnConstraints(colWidth);
            grid.getColumnConstraints().add(cc);
        }

        int rowHeight = WINDOW_HEIGHT/numProc;
        for (int i = 0; i < numProc; i++) {
            RowConstraints rc = new RowConstraints(rowHeight);
            grid.getRowConstraints().add(rc);
        }

        return grid;
    }

}
