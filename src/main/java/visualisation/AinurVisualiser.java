package visualisation;

import algorithm.Algorithm;
import common.graph.Graph;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import visualisation.modules.AlgorithmStatisticsVisualiser;
import visualisation.modules.GraphVisualiser;
import visualisation.modules.ScheduleVisualiser;

public class AinurVisualiser extends Region {

    /* MACROS */

    /* Fields */

    // Algorithm currently visualising
    private Algorithm _algorithm;

    // Visualiser modules
    private GraphVisualiser _gv;
    private ScheduleVisualiser _sv;
    private AlgorithmStatisticsVisualiser _asv;

    /* Constructors */

    /**
     * Constructs an AinurVisualiser
     * Nests in modules, GraphVisualiser, ScheduleVisualiser and AlgorithmStatisticsVisualiser
     *
     * @param algorithm the algorithm to visualise
     * @param graph The task graph to be displayed
     * @param lowerBound the lowerbound of the algorithm //TODO this should be removed when a method to get this is implemented
     * @param coresUsed The number of cores to be used
     */
    public AinurVisualiser(Algorithm algorithm, Graph graph, int lowerBound, long coresUsed) {
        // Assign Args
        _algorithm = algorithm;

        // Initialise visualisers
        _gv = new GraphVisualiser(graph);
        _sv = new ScheduleVisualiser();
        _asv = new AlgorithmStatisticsVisualiser(lowerBound, algorithm.getCurrentBest().getEndTime(), coresUsed);

        this.setUpLayout();
    }

    /* Public Methods */

    /**
     * Creates a stage and displays the visualiser in it.
     */
    public void show(){
        Stage stage = new Stage();
        Scene scene = new Scene(this);
        stage.setScene(scene);
        stage.show();

        this.runVisualiser();
    }

    /* Private Helper Methods */

    /**
     * Private helper method.
     * This method lays out the visualiser modules on the in the parent component (this)
     */
    private void setUpLayout() {
        //TODO implement me!
    }

    /**
     * Private helper method.
     * This method runs the visualiser alongside the algorithm.
     * This method periodically polls the algorithm for information about its current status.
     * This method will then update the visualiser modules accordingly.
     */
    private void runVisualiser() {
        //TODO implement me!
    }

    /**
     * Private helper method.
     * This method is to be called whenever the visualiser wants to update all of its modules.
     */
    private void updateDisplays() {
        this.updateGraph();
        this.updateSchedule();
        this.updateStatistics();
    }

    /**
     * Private helper method.
     * Gets the current node / nodes from an algorithm.
     * Uses this node to update the GraphVisualiser.
     */
    private void updateGraph() {
        //TODO Implement
        // TODO check type of algorithm, if tiered get list if not get single
    }

    /**
     * Private helper method.
     * Gets the current best schedule from an algorithm.
     * Uses this schedule to update the ScheduleVisualiser.
     */
    private void updateSchedule() {
        //TODO implement
    }

    /**
     * Private helper method.
     * Gets algorithm statistics from an algorithm.
     * Uses these statistics to update the AlgorithmStatisticsVisualiser.
     */
    private void updateStatistics() {
        //TODO implement
    }

}
