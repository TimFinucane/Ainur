package visualisation.modules;

import common.Config;
import common.schedule.Schedule;
import common.schedule.Task;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Class to deal with the visual rendering of a schedule.
 */
public class ScheduleVisualiser extends VBox {
    private static final String INNER_SCHEDULE_CLASS_CSS = "inner-schedule";
    private static final String CANVAS_PADDING_CLASS = "canvas-padding";

    private static final Color FILL_COLOUR = Color.web(Config.UI_PRIMARY_COLOUR);
    private static final Color TEXT_COLOUR = Color.web(Config.UI_TEXT_COLOUR);

    private Schedule    _schedule;
    private Canvas      _scheduleView;
    private NumberAxis  _axis;

    public ScheduleVisualiser(int numProcessors) {
        _scheduleView = new Canvas();
        Pane canvasHolder = new Pane(_scheduleView);
        // Bind schedule view to its holder so that it resizes when possible
        _scheduleView.widthProperty().bind(canvasHolder.widthProperty());
        _scheduleView.heightProperty().bind(canvasHolder.heightProperty());

        // Ensure that when we resize, it redraws the schedule
        widthProperty().addListener(e -> draw());
        heightProperty().addListener(e -> draw());

        // And the number axis
        _axis = new NumberAxis(0, 100, 10);
        _axis.setTickLength(10.0);
        _axis.setTickLabelFont(new Font(_axis.getTickLabelFont().getName(), 16.0));

        VBox scheduleWrapper = new VBox(canvasHolder, _axis);
        VBox.setVgrow(canvasHolder, Priority.SOMETIMES);
        scheduleWrapper.getStyleClass().addAll(CANVAS_PADDING_CLASS);

        ScrollPane scrollPane = new ScrollPane(scheduleWrapper);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Add the canvas holder, and make sure it takes available height
        getChildren().add(scrollPane);
        setVgrow(scrollPane, Priority.ALWAYS);
        // And have a nice bit of pad
        this.getStyleClass().add(INNER_SCHEDULE_CLASS_CSS);

        canvasHolder.setMinHeight(25 * numProcessors);
        canvasHolder.setPrefHeight(50 * numProcessors);
        _axis.setMinHeight(25);

        scrollPane.setMinHeight(100);
        scrollPane.setPrefHeight(150);
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

        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();

        // Clear canvas before drawing in it
        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        // Colours of the tasks
        gc.setFill(FILL_COLOUR);
        gc.setStroke(Color.web(Config.UI_SECONDARY_COLOR));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        // The width of a single unit time in the schedule. End time is padded by 10% so schedule doesn't go to end
        double unitWidth = canvasWidth / _schedule.getEndTime();
        // The height of each task in the processor
        double taskGap = 5;
        double taskHeight = (canvasHeight - 2 - taskGap * _schedule.getNumProcessors()) / _schedule.getNumProcessors();

        // Add each task to position
        for(int processor = 0; processor < _schedule.getNumProcessors(); ++processor) {
            for(Task task : _schedule.getTasks(processor)) {
                int left = (int)(unitWidth * task.getStartTime());
                int top = (int)(1 + (taskHeight + taskGap) * processor);
                int width = (int)(unitWidth * task.getNode().getComputationCost());

                gc.setFill(FILL_COLOUR);
                gc.strokeRect(left, top, width, taskHeight);
                gc.fillRect(left, top, width, taskHeight);

                int centre = left + width / 2;
                gc.setFill(TEXT_COLOUR);
                gc.setFont( new Font(gc.getFont().getName(), Math.min(taskHeight / 2.0, width / 2.0)) );
                gc.fillText(task.getNode().getLabel(), centre, top + taskHeight / 2);
            }
        }
        // Modify number axis
        double tickUnit = Math.pow(10, Math.round( Math.log10(_schedule.getEndTime() / 10.0) ));
        _axis.setTickUnit(tickUnit);
        _axis.setUpperBound(_schedule.getEndTime());
        _axis.layout();

        // Extend tick marks to full screen
        for(int i = 0; i < _axis.getTickMarks().size(); ++i) {
            int x = (int)Math.round(_axis.getTickMarks().get(i).getPosition());

            if(i == _axis.getTickMarks().size() - 1)
                x -= 1;

            gc.setStroke(Color.web(Config.UI_LIGHT_BLACK_COLOUR));
            gc.strokeLine(x, 0, x, canvasHeight);
        }
    }
}
