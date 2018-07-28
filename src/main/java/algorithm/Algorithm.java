package algorithm;

import common.Graph;
import common.Schedule;

public abstract class Algorithm {
    protected int _processors;
    protected boolean _multithreaded;

    public Algorithm(int processors, boolean multithreaded) {
        _processors = processors;
        _multithreaded = multithreaded;
    }

    public abstract void start(Graph graph);

    // TODO Implement method
    // This method is up for debate. May not be needed.
    public boolean isComplete() { return false; }

    // TODO Implement method
    public Schedule getCurrentBest() { return null; }
}
