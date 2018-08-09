package visualisation;

import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.SimpleSchedule;
import common.schedule.Task;
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
        ScheduleVisualiser sv = new ScheduleVisualiser();
        sv.update(generateSchedule());
        Scene scene = new Scene(sv);
        stage.setScene(scene);
        stage.show();
    }

    public Schedule generateSchedule(){
        Schedule schedule = new SimpleSchedule(5);
        Task task = new Task(2, 2, new Node(14, "a", 10));

        schedule.addTask(task);
        return schedule;
    }
}
