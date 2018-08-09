package visualisation;

import common.schedule.Schedule;
import common.schedule.Task;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class ScheduleVisualiser extends Group {

    private static final int WINDOW_WIDTH = 750;
    private static final int WINDOW_HEIGHT = 200;
    private static final Color COLOR = Color.LAVENDER;
    private double rowHeight;
    private double colWidth;

    public void update(Schedule schedule) {
        int endTime = schedule.getEndTime();
        int numProc = schedule.getNumProcessors();
        rowHeight = WINDOW_HEIGHT/numProc;
        colWidth = WINDOW_WIDTH/endTime;

        GridPane grid = setDimensions(endTime, numProc);
        grid.setGridLinesVisible(true);

        for (int i = 0; i < numProc; i++) {
            for (Task task : schedule.getTasks(i)) {
                Rectangle rect = new Rectangle();
                rect.setHeight(rowHeight);
                rect.setWidth(colWidth*task.getNode().getComputationCost());
                rect.setFill(COLOR);

                Text text = new Text(task.getNode().getLabel());
                StackPane stackPane = new StackPane();
                stackPane.getChildren().addAll(rect, text);
                grid.add(stackPane, task.getStartTime(), i);
            }
        }

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
