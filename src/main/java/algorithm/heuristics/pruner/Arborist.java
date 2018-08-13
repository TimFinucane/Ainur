package algorithm.heuristics.pruner;

import common.graph.Graph;
import common.schedule.Schedule;
import common.schedule.Task;

/**
 * ""An arborist, tree surgeon, or arboriculturist, is a professional in the practice of arboriculture, which is the
 * cultivation, management, and study of individual trees, shrubs, vines, and other perennial
 * woody plants in dendrology and horticulture "" - Wikipedia
 *
 * This interface is to be implemented by  any pruning algorithms and techniques to be used in the main algorithm to
 * prune the state space
 */
public interface Arborist {

    /**
     * Method returns a boolean to tell algorithm if it should prune a branch, given the next task
     * to be added to the schedule for that branch
     * @param graph : Graph
     *              A graph, with root node the current latest node/task added to the schedule
     * @param schedule : Schedule
     *              A partial schedule which has been built by algorithm so far
     * @param toBeAdded : Task
     *              The task that is about to be added to the schedule, should pruning not occur.
     * @return boolean : boolean
     *              To tell algorithm whether to prune or not
     */
    boolean prune(Graph graph, Schedule schedule, Task toBeAdded);

    /**
     * Combines multiple arborists by deciding to prune if ANY arborist says to prune.
     * Note: Be VERY careful combining arborists together, make sure they are compatible with eachother.
     */
    static Arborist combine(Arborist... arborists) {
        return (graph, schedule, tobeAdded) -> {
            for(Arborist arborist : arborists)
                if(arborist.prune(graph, schedule, tobeAdded))
                    return false;

            return true;
        };
    }
}
