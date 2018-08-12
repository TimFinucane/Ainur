package visualisation;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Ignore;

@Ignore
public class AlgorithmStatisticsVisualiserTest extends Application {

    /**
     * Displays a visualisation of a dummy schedule
     */
    @Override
    public void start(Stage stage) {


        AlgorithmStatisticsVisualiser sv = new AlgorithmStatisticsVisualiser(0, 100);
        sv.update(generateStatistics(70, 40));
        Scene scene = new Scene(sv);
        stage.setScene(scene);
        stage.show();

        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                while (true) {
                    sv.update(generateStatistics(70, 40));
                }
            }
        };
        new Thread(task).start();
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
