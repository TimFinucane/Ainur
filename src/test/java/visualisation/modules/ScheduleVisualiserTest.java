package visualisation.modules;

import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.SimpleSchedule;
import common.schedule.Task;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Disabled;

/**
 * Class used to check rendering of ScheduleVisualiser components.
 */
@Disabled
public class ScheduleVisualiserTest extends Application {

    /**
     * Displays a visualisation of a dummy schedule
     */
    public void start(Stage stage) {
        ScheduleVisualiser sv = new ScheduleVisualiser(5);
        Scene scene = new Scene(sv, 1080, 720);
        stage.setScene(scene);
        stage.show();
        sv.update(generateSchedule());

        System.out.println("Width " + sv.getWidth() + ", Height " + sv.getHeight());
    }

    /**
     * Generates a dummy schedule to be visualised
     * @return : dummy schedule
     */
    private Schedule generateSchedule(){
        Schedule schedule = new SimpleSchedule(5);

        Task task = new Task(0, 0, new Node(4, "a", 1));
        Task task1 = new Task(1, 4, new Node(5, "b", 2));
        Task task2 = new Task(2, 5, new Node(4, "c", 3));
        Task task3 = new Task(4, 7, new Node(4, "d", 4));
        Task task4 = new Task(3, 0, new Node(10, "e", 5));
        Task task5 = new Task(1, 9, new Node(8, "f", 6));

        schedule.addTask(task);
        schedule.addTask(task1);
        schedule.addTask(task2);
        schedule.addTask(task3);
        schedule.addTask(task4);
        schedule.addTask(task5);

        return schedule;
    }
}
