package visualisation.modules;

import common.Config;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class BoundingChart extends VBox {
    private static final Color FINISHING_TICK_MARK_FILL = Color.web(Config.UI_LIGHT_BLACK_COLOUR);
    private static final Color TEXT_FILL = Color.web(Config.UI_TEXT_COLOUR);

    private final int _initialLowerBound;
    private final int _initialUpperBound;

    private int _curLowerBound;
    private int _curUpperBound;

    private Canvas _boundingBoxView;
    private NumberAxis _axis;

    public BoundingChart(int initialLowerBound, int initialUpperBound) {
        if(initialLowerBound == Integer.MAX_VALUE)
            throw new RuntimeException("Initial lower bound should never be int max");
        if(initialUpperBound == Integer.MAX_VALUE)
            throw new RuntimeException("We shouldnt have initial upper bound this high");

        if(initialLowerBound != initialUpperBound) {
            _initialLowerBound = initialLowerBound;
            _initialUpperBound = initialUpperBound;
        } else {
            _initialLowerBound = initialLowerBound - 1;
            _initialUpperBound = initialUpperBound + 1;
        }
        _curLowerBound = initialLowerBound;
        _curUpperBound = initialUpperBound;

        _boundingBoxView = new Canvas();
        Pane canvasHolder = new Pane(_boundingBoxView);

        // Bind schedule view to its holder so that it resizes when possible
        _boundingBoxView.widthProperty().bind(canvasHolder.widthProperty());
        _boundingBoxView.heightProperty().bind(canvasHolder.heightProperty());

        // Ensure that when we resize, it redraws the schedule
        widthProperty().addListener(e -> draw());
        heightProperty().addListener(e -> draw());

        // Set upper and lower bounds to that of initial schedule estimates, Have approx. 20 tick marks rounded to nearest 10 time units
        double tickUnit = Math.pow(10, Math.round( Math.log10((_initialUpperBound - _initialLowerBound) / 10.0) ));
        tickUnit *= Math.round((_initialUpperBound - _initialLowerBound) / (2 * tickUnit));

        _axis = new NumberAxis(_initialLowerBound, _initialUpperBound, tickUnit);
        _axis.setTickLength(10.0);
        _axis.setTickLabelFont(new Font(_axis.getTickLabelFont().getName(), 16.0));
        _axis.setTickLabelFill(TEXT_FILL);

        getChildren().add(canvasHolder);
        getChildren().add(_axis);
        setPadding(new Insets(0, 15, 0, 15));
        VBox.setVgrow(canvasHolder, Priority.SOMETIMES);

        _axis.setMinHeight(25);

        setPrefHeight(100);
    }

    /**
     * Update the bounding chart to appropriate state given the current min and max boundary times.
     * This chart re-renders both rectangles if one changes.
     */
    public void update(int lowerBound, int upperBound) {
        // If current statistics are better than locally stored bests, overwrite.
        _curLowerBound = Math.max(_curLowerBound, lowerBound); // TODO: Check for int max
        _curUpperBound = Math.min(_curUpperBound, upperBound); // TODO: Check for int max

        draw();
    }

    private void draw() {
        GraphicsContext gc = _boundingBoxView.getGraphicsContext2D();
        gc.clearRect(0, 0, _boundingBoxView.getWidth(), _boundingBoxView.getHeight());

        // Calculate rectangle dims
        double pixelsPerUnit = _boundingBoxView.getWidth() / (_initialUpperBound - _initialLowerBound);
        double height = _boundingBoxView.getHeight();

        double lowerEnd = Math.round((_curLowerBound - _initialLowerBound) * pixelsPerUnit);

        double upperStart = Math.round((_curUpperBound - _initialLowerBound) * pixelsPerUnit);
        double upperWidth = _boundingBoxView.getWidth() - upperStart;

        gc.setFill(Color.web(Config.UI_SECONDARY_COLOR));
        gc.setStroke(Color.web(Config.UI_LIGHT_BLACK_COLOUR));

        // Draw rectangles
        gc.fillRect(0, 0, lowerEnd, height);
        gc.fillRect(upperStart, 0, upperWidth, height);

        // Text of the stuff TODO: Font family/name
        gc.setFont(new Font(gc.getFont().getName(), Math.min(height / 2, Math.min(lowerEnd / 2, upperWidth / 2))));
        gc.setFill(Color.web(Config.UI_LIGHT_BLACK_COLOUR));
        gc.setTextBaseline(VPos.CENTER);

        gc.setTextAlign(TextAlignment.RIGHT);
        gc.fillText(String.valueOf(_curLowerBound), lowerEnd * 0.8, height / 2.0);

        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText(String.valueOf(_curUpperBound), upperStart + upperWidth * 0.2, height / 2.0);

        // If lower bound and upper bound are same, render a nice fat (4px) line
        if(lowerEnd == upperStart) {
            gc.setFill(FINISHING_TICK_MARK_FILL);
            gc.fillRect(lowerEnd - 2, 0, 4, height);
        } else {
            gc.strokeLine(lowerEnd, 0, lowerEnd, height);
            gc.strokeLine(upperStart, 0, upperStart, height);
        }
    }
}
