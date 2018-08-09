package visualisation;

import common.schedule.Schedule;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class ScheduleVisualiser extends Group {

    public ScheduleVisualiser(Schedule schedule){
        int endTime = schedule.getEndTime();
        int numProc = schedule.getNumProcessors();
    }

    public ScheduleVisualiser(){
        update();
    }

    public void update() {

        Node node = (Node)this.getParent();
        int numProc = 4;
        int endTime = 14;

        int colWidth = 750/endTime;
        int rowHeight = 200/numProc;
//
//        Button button = new Button();
//        button.setMinWidth(100);
//        button.setMinHeight(50);
//        button.setText("hello");


        GridPane grid = new GridPane();

        for (int i = 0; i < endTime; i++) {
            ColumnConstraints cc = new ColumnConstraints(colWidth);
            grid.getColumnConstraints().add(cc);
        }

        for (int i = 0; i < numProc; i++) {
            RowConstraints rc = new RowConstraints(rowHeight);
            grid.getRowConstraints().add(rc);
        }


        System.out.println(grid.toString());

        System.out.println(grid.getColumnConstraints());
        System.out.println(grid.getRowConstraints());

        grid.add(new Button(), 3, 3);


        this.getChildren().add(grid);

    }

}
