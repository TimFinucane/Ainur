package common.schedule;

import common.graph.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleSchedule extends Schedule {
    private List<Task> _tasks = new ArrayList<>();
    // TODO: This is probably somewhat inefficient, especially with memory. Can we find a way?
    private List<Task>[] _processors;

    public SimpleSchedule(int numProcessors, int nodes) {
        this(numProcessors);

        _tasks = new ArrayList<>(Collections.nCopies(nodes, null));
    }
    public SimpleSchedule(int numProcessors) {
        super(numProcessors);
        _processors = new ArrayList[numProcessors]; // Java generics means i have to do it like this

        for(int i = 0; i < _numProcessors; ++i)
            _processors[i] = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public SimpleSchedule(SimpleSchedule other) {
        super(other._numProcessors);

        _tasks = new ArrayList<>(other._tasks);

        // No other way to do this unfortunately :(
        _processors = new ArrayList[_numProcessors];

        for(int i = 0; i < _numProcessors; ++i)
            _processors[i] = new ArrayList<>(other._processors[i]);
    }

    @Override
    public void addTask(Task task) {
        if(size(task.getProcessor()) > 0 && (getLatest(task.getProcessor()).getStartTime() > task.getStartTime()))
            throw new IllegalArgumentException("TODO: Code logic for inserting task into processor in the right place");

        if(_tasks.size() <= task.getNode().getId())
            _tasks.addAll(Collections.nCopies( 1 + task.getNode().getId() - _tasks.size(), null));

        _tasks.set(task.getNode().getId(), task);
        _processors[task.getProcessor()].add(task);
    }
    @Override
    public void removeTask(Task task) {
        if(getLatest(task.getProcessor()).getNode() != task.getNode())
            throw new IllegalArgumentException("TODO: Removing tasks that arent at the end of a processor");

        _tasks.set(task.getNode().getId(), null);
        _processors[task.getProcessor()].remove(size(task.getProcessor()) - 1);
    }

    @Override
    public Task findTask(Node node) {
        return _tasks.size() > node.getId() ? _tasks.get(node.getId()) : null;
    }

    @Override
    public Task getLatest(int processor) {
        return _processors[processor].size() > 0 ? _processors[processor].get(_processors[processor].size() - 1) : null;
    }

    @Override
    public List<Task> getTasks(int processor) {
        return Collections.unmodifiableList(_processors[processor]);
    }

    @Override
    public int size(int processor) {
        return _processors[processor].size();
    }
}
