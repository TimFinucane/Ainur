package algorithm.Heuristics;

import common.graph.Graph;
import common.schedule.Schedule;

/**
 * An arborist, tree surgeon, or arboriculturist, is a professional in the practice of arboriculture, which is the
 * cultivation, management, and study of individual trees, shrubs, vines, and other perennial
 * woody plants in dendrology and horticulture - Wikipedia
 *
 * This interface is to be implemented by  any pruning algorithms and techniques to be used in the main algorithm to
 * prune the state space
 */
public interface Arborist {

    boolean prune(Graph greaph, Schedule schedule);

}
