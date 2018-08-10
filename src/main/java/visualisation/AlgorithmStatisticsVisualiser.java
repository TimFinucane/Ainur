package visualisation;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public class AlgorithmStatisticsVisualiser extends Region {

    private static final double WINDOW_HEIGHT = 200;
    private static final double WINDOW_LENGTH = 500;

    public void update(Statistics statistics) {


        GridPane grid = setDimensions(statistics.getMinScheduleLength(), statistics.getMaxScheduleLength());



    }

    private GridPane setDimensions() {

    }

}
