package algorithm.Heuristics;

import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;

import java.util.List;

public interface LowerBound {

    int estimate(Graph graph, Schedule schedule, List<Node> nodesToVisit);
    int estimate(Graph graph, Schedule schedule);

}
