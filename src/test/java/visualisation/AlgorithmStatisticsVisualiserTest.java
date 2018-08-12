package visualisation;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Ignore;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

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

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            final Runnable runnable = () -> sv.update(generateStatistics(66, 44));
            Platform.runLater(runnable);
        }, 0, 1, TimeUnit.SECONDS);

        Runnable runnable = () -> sv.update(generateStatistics(66, 44));

        Platform.runLater(runnable);
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
