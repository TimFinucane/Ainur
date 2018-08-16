package visualisation;

import common.schedule.Schedule;
import common.schedule.Task;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * Class to deal with the visual rendering of a schedule.
 */
public class ScheduleVisualiser extends VBox {
    private static final Color FILL_COLOR = Color.LAVENDER;
    private static final Color BORDER_COLOR = Color.BLACK;

    private Schedule _schedule;
    private Canvas _scheduleView;

    public ScheduleVisualiser() {
        _scheduleView = new Canvas();
        Pane canvasHolder = new Pane(_scheduleView);
        // Bind schedule view to its holder so that it resizes when possible
        _scheduleView.widthProperty().bind(canvasHolder.widthProperty());
        _scheduleView.heightProperty().bind(canvasHolder.heightProperty());

        // Ensure that when the canvas holder resizes, it redraws the schedule
        canvasHolder.widthProperty().addListener(e -> draw());
        canvasHolder.heightProperty().addListener(e -> draw());

        // Add the canvas holder, and make sure it takes available height
        getChildren().add(canvasHolder);
        setVgrow(canvasHolder, Priority.ALWAYS);

        setPadding(new Insets(10));
    }

    public void update(Schedule schedule) {
        _schedule = schedule;
        draw();
    }

    /**
     * Draws the schedule
     */
    private void draw() {
        GraphicsContext gc = _scheduleView.getGraphicsContext2D();

        // If there is no schedule, don't remove whatever was on the screen before. TODO: Intended behaviour?
        if(_schedule == null)
            return;

        gc.clearRect(0, 0, getWidth(), getHeight());

        // Colours of the tasks
        gc.setFill(FILL_COLOR);
        gc.setStroke(BORDER_COLOR);

        double width = gc.getCanvas().getWidth();
        double height = gc.getCanvas().getHeight();

        // The height of each task in the processor
        double taskHeight = height / _schedule.getNumProcessors();
        // The width of a single unit time in the schedule. End time is padded by 10% so schedule doesn't go to end
        double unitWidth = width / (_schedule.getEndTime());

        // Add each task to position
        for(int processor = 0; processor < _schedule.getNumProcessors(); ++processor) {
            for(Task task : _schedule.getTasks(processor)) {
                gc.fillRect(
                    unitWidth * task.getStartTime(),
                    taskHeight * processor,
                    unitWidth * task.getNode().getComputationCost(),
                    taskHeight
                );
            }
        }
    }

}
