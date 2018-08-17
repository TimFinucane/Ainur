package visualisation.modules;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Disabled;

import java.math.BigInteger;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Disabled
public class AlgorithmStatisticsVisualiserTest extends Application {

    private int minTime;
    private int maxTime;

    /**
     * Displays a visualisation of a dummy schedule
     */
    @Override
    public void start(Stage stage) {

        minTime = 0;
        maxTime = 1000;

        AlgorithmStatisticsVisualiser sv = new AlgorithmStatisticsVisualiser(4, 0, 1000);
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

        Statistics stats = new Statistics();
        stats.setMaxScheduleBound(maxTime / 10);
        stats.setMinScheduleBound(minTime / 10);
        stats.setSearchSpaceLookedAt(new BigInteger(Integer.toString((int)(minTime * 4.4))));
        stats.setSearchSpaceCulled(new BigInteger(Integer.toString((int)(minTime / 2.8))));

        return stats;

    }

}
