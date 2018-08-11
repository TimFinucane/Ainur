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
        AlgorithmStatisticsVisualiser sv = new AlgorithmStatisticsVisualiser();
        sv.update(generateStatistics());
        Scene scene = new Scene(sv);
        stage.setScene(scene);
        stage.show();
    }


    /**
     * Generates a dummy schedule to be visualised
     * @return : dummy schedule
     */
    private Statistics generateStatistics(){

        Statistics stats = new Statistics();
        stats.setMaxScheduleBound(90);
        stats.setMinScheduleBound(87);

        return stats;

    }

}
