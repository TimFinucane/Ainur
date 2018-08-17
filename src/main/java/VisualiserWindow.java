import algorithm.Algorithm;
import common.graph.Graph;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import visualisation.AinurVisualiser;

public class VisualiserWindow {
    /* Macros */
    private static final String STYLE_SHEET = "/style/Ainur.css";


    private AinurVisualiser av;
    private double  x;
    private double  y;
    private boolean leftDraggedState = false;
    private boolean rightDraggedState = false;
    private boolean bottomDraggedState = false;

    private Stage       stage;
    private Scene       scene;
    private BorderPane  window;

    public VisualiserWindow(Algorithm algorithm, Graph graph, int numProcessors) {
        // Load an ainur visualiser
        av = new AinurVisualiser(algorithm, graph, numProcessors);
    }

    /**
     * Starts the javafx visualisation.
     * Takes over control from main.
     */
    public void visualise(Stage primaryStage) {
        stage = primaryStage;

        // Replace gross default taskbar with custom one
        stage.initStyle(StageStyle.UNDECORATED);

        BorderPane border = createResizableBorderBox();
        border.setCenter(av);

        window = new BorderPane();
        window.setTop(createMovableToolbar());
        window.setCenter(border);

        // Set scene and show
        scene = new Scene(window);
        primaryStage.setScene(scene);

        av.getStyleClass().add("ainur-vis");

        scene.getStylesheets().add(getClass().getResource(STYLE_SHEET).toExternalForm());
        primaryStage.show();

        av.run();
    }

    private BorderPane  createResizableBorderBox() {
        BorderPane border = new BorderPane();
        border.getStyleClass().add("window-border");

        // AAAAAA (also known as resizability)
        border.setOnMouseMoved(fuckMe -> {
            double borderSize = border.getCenter().getLayoutX();

            boolean left = fuckMe.getX() < borderSize;
            boolean right = fuckMe.getX() > border.getWidth() - borderSize;
            boolean bottom = fuckMe.getY() > border.getHeight() - borderSize;

            if(left && !bottom)
                scene.setCursor(Cursor.W_RESIZE);
            else if(left && bottom)
                scene.setCursor(Cursor.SW_RESIZE);
            else if(bottom && !left && !right)
                scene.setCursor(Cursor.S_RESIZE);
            else if(bottom && right)
                scene.setCursor(Cursor.SE_RESIZE);
            else if(right)
                scene.setCursor(Cursor.E_RESIZE);
            else
                scene.setCursor(Cursor.DEFAULT);
        });
        border.setOnMousePressed(fuckMe -> {
            this.x = fuckMe.getScreenX();
            this.y = fuckMe.getScreenY();

            double borderSize = border.getCenter().getLayoutX();

            leftDraggedState = fuckMe.getX() < borderSize;
            rightDraggedState = fuckMe.getX() > border.getWidth() - borderSize;
            bottomDraggedState = fuckMe.getY() > border.getHeight() - borderSize;
        });
        border.setOnMouseDragged(fuckMe -> {
            double deltaX = fuckMe.getScreenX() - this.x;
            double deltaY = fuckMe.getScreenY() - this.y;
            this.x = fuckMe.getScreenX();
            this.y = fuckMe.getScreenY();

            if(leftDraggedState) {
                double nextWidth = stage.getWidth() - deltaX;

                if(nextWidth > window.minWidth(window.getHeight()) && nextWidth < window.maxWidth(window.getHeight())) {
                    stage.setX(stage.getX() + deltaX);
                    stage.setWidth(nextWidth);
                }
            } else if(rightDraggedState) {
                double nextWidth = stage.getWidth() + deltaX;

                if(nextWidth > window.minWidth(window.getHeight()) && nextWidth < window.maxWidth(window.getHeight()))
                    stage.setWidth(nextWidth);
            }
            if (bottomDraggedState) {
                double nextHeight = stage.getHeight() + deltaY;

                if(nextHeight > window.minHeight(window.getWidth()) && nextHeight < window.maxHeight(window.getWidth()))
                    stage.setHeight(nextHeight);
            }
        });

        return border;
    }
    private ToolBar     createMovableToolbar() {
        ToolBar toolBar = new ToolBar();
        toolBar.getItems().add(new WindowButtons());
        toolBar.getStyleClass().add("toolbar");

        // Add ability to move window from taskbar
        toolBar.setOnMousePressed(me -> {
            this.x = toolBar.getScene().getWindow().getX() - me.getScreenX();
            this.y = toolBar.getScene().getWindow().getY() - me.getScreenY();
        });
        toolBar.setOnMouseDragged(me -> {
            stage.setX(me.getScreenX() + this.x);
            stage.setY(me.getScreenY() + this.y);
        });

        return toolBar;
    }
    /**
     * Custom class for custom window taskbar
     */
    private class WindowButtons extends HBox {
        public WindowButtons() {
            Button closeBtn = new Button("X");
            closeBtn.getStyleClass().add("close-button");

            closeBtn.setOnAction(actionEvent -> Platform.exit());

            this.getChildren().add(closeBtn);
        }
    }
}
