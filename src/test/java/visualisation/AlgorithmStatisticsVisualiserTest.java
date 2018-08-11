package visualisation;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Ignore;

@Ignore
public class AlgorithmStatisticsVisualiserTest extends Application {

    /**
     * Displays a visualisation of a dummy schedule
     */
    public void start(Stage stage) {


        AlgorithmStatisticsVisualiser sv = new AlgorithmStatisticsVisualiser(0, 100);
        sv.update(generateStatistics(70, 40));
        Scene scene = new Scene(sv);
        stage.setScene(scene);
        stage.show();

    }


    /**
     * Generates a dummy schedule to be visualised
     * @return : dummy schedule
     */
    private Statistics generateStatistics(int max, int min){

        Statistics stats = new Statistics();
        stats.setMaxScheduleBound(max);
        stats.setMinScheduleBound(min);

        return stats;

    }

}
