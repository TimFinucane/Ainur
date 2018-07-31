package algorithm.heuristics;

import common.graph.Graph;
import common.schedule.Schedule;

/**
 * ""An arborist, tree surgeon, or arboriculturist, is a professional in the practice of arboriculture, which is the
 * cultivation, management, and study of individual trees, shrubs, vines, and other perennial
 * woody plants in dendrology and horticulture "" - Wikipedia
 *
 * This interface is to be implemented by  any pruning algorithms and techniques to be used in the main algorithm to
 * prune the state space
 */
public interface Arborist {

    /** Method returns a boolean to tell algorithm if it should prune or not
     * @param graph : Graph
     *              A graph, with root node the current latest node/task added to the schedule
     * @param schedule : Schedule
     *              A partial schedule which has been built by algorithm so far
     * @return boolean : boolean
     *              To tell algorithm whether to prune or not
     */
    boolean prune(Graph graph, Schedule schedule);

}
