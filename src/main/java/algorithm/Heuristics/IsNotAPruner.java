package algorithm.Heuristics;

import common.graph.Graph;
import common.schedule.Schedule;

public class IsNotAPruner implements Arborist {

    public boolean prune(Graph graph, Schedule schedule) {
        return false;
    }
}
