package visualisation;

import common.schedule.Schedule;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.awt.*;

public class ScheduleVisualiser extends Group {

    public ScheduleVisualiser(Schedule schedule){
        int endTime = schedule.getEndTime();
        int numProc = schedule.getNumProcessors();
    }

    public ScheduleVisualiser(int endTime, int numProc){
        update(endTime, numProc);
    }

    public void update(int endTime, int numProc) {

        GridPane grid = setDimensions(endTime, numProc);

        System.out.println(grid.toString());

        System.out.println(grid.getColumnConstraints());
        System.out.println(grid.getRowConstraints());

        grid.add(new Button(), 3, 3);
        grid.setGridLinesVisible(true);


        this.getChildren().add(grid);

    }

    private GridPane setDimensions(int endTime, int numProc) {
        GridPane grid = new GridPane();

        int colWidth = 750/endTime;
        for (int i = 0; i < endTime; i++) {
            ColumnConstraints cc = new ColumnConstraints(colWidth);
            grid.getColumnConstraints().add(cc);
        }

        int rowHeight = 200/numProc;
        for (int i = 0; i < numProc; i++) {
            RowConstraints rc = new RowConstraints(rowHeight);
            grid.getRowConstraints().add(rc);
        }

        return grid;
    }

}
