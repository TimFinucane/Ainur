package visualisation;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Ignore;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Ignore
public class AlgorithmStatisticsVisualiserTest extends Application {

    private int minTime;
    private int maxTime;

    /**
     * Displays a visualisation of a dummy schedule
     */
    @Override
    public void start(Stage stage) {

        minTime = 0;
        maxTime = 100;

        AlgorithmStatisticsVisualiser sv = new AlgorithmStatisticsVisualiser(0, 100);
        sv.update(generateStatistics());
        Scene scene = new Scene(sv);
        stage.setScene(scene);
        stage.show();

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            final Runnable runnable = () -> sv.update(generateStatistics());
            Platform.runLater(runnable);
        }, 0, 100, TimeUnit.MILLISECONDS);

    }


    /**
     * Generates a dummy schedule to be visualised
     * @return : dummy schedule
     */
    private Statistics generateStatistics(){

        minTime += 1;
        maxTime -= 1;

        System.out.println(maxTime);
        System.out.println(minTime);

        Statistics stats = new Statistics();
        stats.setMaxScheduleBound(maxTime);
        stats.setMinScheduleBound(minTime);

        return stats;

    }

}
