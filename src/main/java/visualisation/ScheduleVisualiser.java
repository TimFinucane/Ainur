package visualisation;

import common.schedule.Schedule;
import common.schedule.Task;
import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.Axis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import javax.swing.event.ChangeListener;

/**
 * Class to deal with the visual rendering of a schedule.
 */
public class ScheduleVisualiser extends VBox {
    private static final Color FILL_COLOUR = Color.LAVENDER;
    private static final Color BORDER_COLOUR = Color.BLACK;
    private static final Color TEXT_COLOUR = Color.BLACK;

    private Schedule    _schedule;
    private Canvas      _scheduleView;
    private NumberAxis  _axis;

    public ScheduleVisualiser() {
        _scheduleView = new Canvas();
        Pane canvasHolder = new Pane(_scheduleView);
        // Bind schedule view to its holder so that it resizes when possible
        _scheduleView.widthProperty().bind(canvasHolder.widthProperty());
        _scheduleView.heightProperty().bind(canvasHolder.heightProperty());

        // Ensure that when we resize, it redraws the schedule
        widthProperty().addListener(e -> draw());
        heightProperty().addListener(e -> draw());

        // Add the canvas holder, and make sure it takes available height
        getChildren().add(canvasHolder);
        setVgrow(canvasHolder, Priority.ALWAYS);

        // And the number axis
        _axis = new NumberAxis(0, 100, 10);
        _axis.setTickLength(10.0);
        _axis.setTickLabelFont(new Font(_axis.getTickLabelFont().getName(), 16.0));
        getChildren().add(_axis);

        // And have a nice bit of pad
        setPadding(new Insets(10));
    }

    /**
     * Called to update the schedule being rendered to screen
     */
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
        gc.setFill(FILL_COLOUR);
        gc.setStroke(BORDER_COLOUR);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();

        // The width of a single unit time in the schedule. End time is padded by 10% so schedule doesn't go to end
        double unitWidth = canvasWidth / _schedule.getEndTime();
        // The height of each task in the processor
        double taskHeight = canvasHeight / _schedule.getNumProcessors();

        // Add each task to position
        for(int processor = 0; processor < _schedule.getNumProcessors(); ++processor) {
            for(Task task : _schedule.getTasks(processor)) {
                double left = unitWidth * task.getStartTime();
                double top = taskHeight * processor;
                double width = unitWidth * task.getNode().getComputationCost();

                gc.setFill(FILL_COLOUR);
                gc.fillRect(left, top,width, taskHeight);

                double centre = left + width / 2;
                gc.setFill(TEXT_COLOUR);
                gc.setFont( new Font(gc.getFont().getName(), Math.min(taskHeight / 2, width / 2)) );
                gc.fillText(task.getNode().getLabel(), centre, top + taskHeight / 2);
            }
        }
        // Modify number axis
        _axis.setUpperBound(_schedule.getEndTime());
        _axis.layout();

        // Extend tick marks to full screen
        for(int i = 0; i < _axis.getTickMarks().size(); ++i) {
            double x = _axis.getTickMarks().get(i).getPosition();

            if(i == _axis.getTickMarks().size() - 1)
                x -= 1;

            gc.strokeLine(x, 0, x, canvasHeight);
        }
    }
}
