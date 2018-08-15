package visualisation.modules;

import common.schedule.Schedule;
import common.schedule.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * Class to deal with the visual rendering of a schedule.
 */
public class ScheduleVisualiser extends Region {

    private static final double WINDOW_WIDTH = 1000;
    private static final double WINDOW_HEIGHT = 200;
    private static final Color FILL_COLOR = Color.LAVENDER;
    private static final Color BORDER_COLOR = Color.BLACK;
    private double rowHeight;
    private double colWidth;

    private VBox _vBox;

    public ScheduleVisualiser() {
        _vBox = new VBox();
        _vBox.setMinHeight(WINDOW_HEIGHT);
        _vBox.setMinWidth(WINDOW_WIDTH);
        _vBox.setPadding(new Insets(15));
        this.getChildren().add(_vBox);
    }

    /**
     * Updates the visualisation to display the input schedule.
     * @param schedule : schedule to visualise
     */
    public void update(Schedule schedule) {
        if (schedule == null)
            return;

        // Makes sure previous visualisation is cleared from the display
        this.getChildren().clear();

        double endTime = schedule.getEndTime();
        double numProc = schedule.getNumProcessors();
        rowHeight = (WINDOW_HEIGHT/numProc)*0.85;
        colWidth = WINDOW_WIDTH/endTime;

        // Generates a grid structure to display all the tasks with
        GridPane grid = setDimensions(endTime, numProc);

        // Generates an axis to display the schedule timing
        NumberAxis axis = new NumberAxis("Schedule Time",0, endTime, 1);
        axis.setMinWidth(WINDOW_WIDTH);

        // Adds tasks to visualisation for each processor
        for (int proc = 0; proc < numProc; proc++) {
            for (Task task : schedule.getTasks(proc)) {
                grid.add(generateRect(task), task.getStartTime(), proc);
            }
        }

        // Uses a VBox to get desired vertical alignment
        _vBox.getChildren().clear();
        _vBox.getChildren().addAll(grid, axis);
        this.getChildren().add(_vBox);
    }


    /**
     * Generates a Grid pane with number of cols to be the end time of the schedule and number of cols to be the
     * number of processors.
     * @param endTime : end time of the schedule to display
     * @param numProc : number of processors in this schedule
     * @return : a GridPane to display the schedule.
     */
    private GridPane setDimensions(double endTime, double numProc) {
        GridPane grid = new GridPane();

        // Generates endTime cols and sizes them to fit window size
        double colWidth = WINDOW_WIDTH/endTime;
        for (int i = 0; i < endTime; i++) {
            ColumnConstraints cc = new ColumnConstraints(colWidth);
            grid.getColumnConstraints().add(cc);
        }

        // Generates numProc rows and sizes them to fit window size
        double rowHeight = WINDOW_HEIGHT/numProc;
        for (int i = 0; i < numProc; i++) {
            RowConstraints rc = new RowConstraints(rowHeight);
            grid.getRowConstraints().add(rc);
        }

        return grid;
    }

    /**
     * Generates a Rectangle to display the input task
     * @param task : task to display
     */
    private StackPane generateRect(Task task) {
        Rectangle rect = new Rectangle();

        // Set height to fit in appropriate number of grid squares
        rect.setHeight(rowHeight);
        rect.setWidth(colWidth*task.getNode().getComputationCost());

        // Adds aesthetics
        rect.setFill(FILL_COLOR);
        rect.setStroke(BORDER_COLOR);

        // Displays the rectangle with the task's label
        Text text = new Text(task.getNode().getLabel());
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(rect, text);

        return stackPane;
    }
}
