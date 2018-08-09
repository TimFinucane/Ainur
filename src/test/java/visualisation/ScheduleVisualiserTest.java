package visualisation;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Class used to check rendering of ScheduleVisualiser components.
 */
@Ignore
public class ScheduleVisualiserTest extends Application{

    @Override
    public void start(Stage stage) {
        Group schedule = new ScheduleVisualiser();
//        schedule.getChildren().add(new Button());

        Scene scene = new Scene(schedule);
        stage.setScene(scene);
        stage.show();
    }
}
