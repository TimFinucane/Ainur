package algorithm.heuristics;

import algorithm.heuristics.lowerbound.CriticalPath;
import algorithm.heuristics.lowerbound.FillTimeBound;
import algorithm.heuristics.lowerbound.LowerBound;
import algorithm.heuristics.pruner.*;

/**
 * A default set of heuristics to use
 */
public class DefaultHeuristics {
    public static LowerBound lowerBound() {
        return LowerBound.combine(new CriticalPath(), new FillTimeBound());
    }

    public static Arborist arborist() {
        return Arborist.combine(
            new StartTimePruner(),
            new ProcessorOrderPruner(),
            new BetterStartPruner(),
            new BetterSwapPruner()
        );
    }
}
