package visualisation;

import common.schedule.Schedule;
import common.schedule.Task;
import javafx.scene.Group;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class ScheduleVisualiser extends Group {

    private static final int WINDOW_WIDTH = 750;
    private static final int WINDOW_HEIGHT = 200;
    private static final Color FILL_COLOR = Color.LAVENDER;
    private static final Color BORDER_COLOR = Color.BLACK;
    private double rowHeight;
    private double colWidth;

    public void update(Schedule schedule) {

        int endTime = schedule.getEndTime();
        int numProc = schedule.getNumProcessors();
        rowHeight = (WINDOW_HEIGHT/numProc)-12;
        colWidth = WINDOW_WIDTH/endTime;

        GridPane grid = setDimensions(endTime, numProc);

        for (int proc = 0; proc < numProc; proc++) {
            for (Task task : schedule.getTasks(proc)) {
                grid.add(generateRect(task), task.getStartTime(), proc);
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

    private StackPane generateRect(Task task) {
        Rectangle rect = new Rectangle();
        rect.setHeight(rowHeight);
        rect.setWidth(colWidth*task.getNode().getComputationCost());
        rect.setFill(FILL_COLOR);
        rect.setStroke(BORDER_COLOR);

        Text text = new Text(task.getNode().getLabel());
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(rect, text);

        return stackPane;
    }

}
