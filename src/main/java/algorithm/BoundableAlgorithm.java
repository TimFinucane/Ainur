package algorithm;

import algorithm.heuristics.Arborist;
import algorithm.heuristics.LowerBound;
import common.graph.Edge;
import common.graph.Graph;
import common.graph.Node;
import common.schedule.Schedule;
import common.schedule.SimpleSchedule;
import common.schedule.Task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public abstract class BoundableAlgorithm extends Algorithm{

    private final MultiAlgorithmNotifier _notifier;
    protected AtomicReference<Schedule> _globalBest;
    protected int _depth;


    public BoundableAlgorithm(int processors, Arborist arborist, LowerBound lowerBound, MultiAlgorithmNotifier notifier) {
        super(processors, false, arborist, lowerBound);
        this._notifier = notifier;
    }

    @Override
    public Schedule getCurrentBest(){
        return _globalBest.get();
    }

}
