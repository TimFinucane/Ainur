import algorithm.Algorithm;
import common.graph.Graph;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import visualisation.AinurVisualiser;

public class VisualiserWindow {
    /* Macros */
    private static final String STYLE_SHEET = "/style/Ainur.css";
    private static final String APP_ICON = "/img/icon.png";

    // Window state
    private double _x;
    private double _y;
    private boolean _leftDraggedState = false;
    private boolean _rightDraggedState = false;
    private boolean _bottomDraggedState = false;

    // JavaFX state
    private Stage _stage;
    private Scene _scene;
    private BorderPane _window;

    private AinurVisualiser _av;

    // Input arguments
    private final Algorithm _algorithm;
    private final Graph _graph;
    private final int _numProcessors;

    boolean complete = false;

    public VisualiserWindow(Algorithm algorithm, Graph graph, int numProcessors) {
        _algorithm = algorithm;
        _graph = graph;
        _numProcessors = numProcessors;
    }

    /**
     * Starts the javafx visualisation.
     * Takes over control from main.
     */
    public void visualise(Stage primaryStage) {
        _stage = primaryStage;

        // Replace gross default taskbar with custom one
        _stage.initStyle(StageStyle.UNDECORATED);

        // Load an ainur visualiser
        _av = new AinurVisualiser(_algorithm, _graph, _numProcessors);

        BorderPane border = createResizableBorderBox();
        border.setCenter(_av);

        _window = new BorderPane();
        _window.setTop(createMovableToolbar());
        _window.setCenter(border);

        // Set _scene and show
        _scene = new Scene(_window);
        primaryStage.setScene(_scene);

        _av.getStyleClass().add("ainur-vis");

        _scene.getStylesheets().add(getClass().getResource(STYLE_SHEET).toExternalForm());
        _stage.getIcons().add(new Image(getClass().getResourceAsStream(APP_ICON)));
        primaryStage.show();

        _av.run();

        // Early completion check
        if(complete)
            _av.stop();
    }

    public void stop() {
        complete = true;
        if(_av != null)
            _av.stop();
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
                _scene.setCursor(Cursor.W_RESIZE);
            else if(left && bottom)
                _scene.setCursor(Cursor.SW_RESIZE);
            else if(bottom && !left && !right)
                _scene.setCursor(Cursor.S_RESIZE);
            else if(bottom && right)
                _scene.setCursor(Cursor.SE_RESIZE);
            else if(right)
                _scene.setCursor(Cursor.E_RESIZE);
            else
                _scene.setCursor(Cursor.DEFAULT);
        });
        border.setOnMousePressed(fuckMe -> {
            this._x = fuckMe.getScreenX();
            this._y = fuckMe.getScreenY();

            double borderSize = border.getCenter().getLayoutX();

            _leftDraggedState = fuckMe.getX() < borderSize;
            _rightDraggedState = fuckMe.getX() > border.getWidth() - borderSize;
            _bottomDraggedState = fuckMe.getY() > border.getHeight() - borderSize;
        });
        border.setOnMouseDragged(fuckMe -> {
            double deltaX = fuckMe.getScreenX() - this._x;
            double deltaY = fuckMe.getScreenY() - this._y;
            this._x = fuckMe.getScreenX();
            this._y = fuckMe.getScreenY();

            if(_leftDraggedState) {
                double nextWidth = _stage.getWidth() - deltaX;

                if(nextWidth > _window.minWidth(_window.getHeight()) && nextWidth < _window.maxWidth(_window.getHeight())) {
                    _stage.setX(_stage.getX() + deltaX);
                    _stage.setWidth(nextWidth);
                }
            } else if(_rightDraggedState) {
                double nextWidth = _stage.getWidth() + deltaX;

                if(nextWidth > _window.minWidth(_window.getHeight()) && nextWidth < _window.maxWidth(_window.getHeight()))
                    _stage.setWidth(nextWidth);
            }
            if (_bottomDraggedState) {
                double nextHeight = _stage.getHeight() + deltaY;

                if(nextHeight > _window.minHeight(_window.getWidth()) && nextHeight < _window.maxHeight(_window.getWidth()))
                    _stage.setHeight(nextHeight);
            }
        });

        return border;
    }
    private ToolBar     createMovableToolbar() {
        ToolBar toolBar = new ToolBar();
        toolBar.getItems().add(new WindowButtons());
        toolBar.getStyleClass().add("toolbar");

        // Add ability to move _window from taskbar
        toolBar.setOnMousePressed(me -> {
            this._x = toolBar.getScene().getWindow().getX() - me.getScreenX();
            this._y = toolBar.getScene().getWindow().getY() - me.getScreenY();
        });
        toolBar.setOnMouseDragged(me -> {
            _stage.setX(me.getScreenX() + this._x);
            _stage.setY(me.getScreenY() + this._y);
        });

        return toolBar;
    }
    /**
     * Custom class for custom _window taskbar
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
