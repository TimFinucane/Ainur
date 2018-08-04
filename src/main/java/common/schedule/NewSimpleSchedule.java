package common.schedule;

import common.graph.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewSimpleSchedule extends NewSchedule {
    private Task _tasks[];
    // TODO: This is probably somewhat inefficient, especially with memory. Can we find a way?
    private List<Task>[] _processors;

    public NewSimpleSchedule(int numProcessors, int nodes) {
        super(numProcessors);
        _tasks = new Task[nodes];
        _processors = new ArrayList[nodes]; // Java generics means i have to do it like this
    }

    @Override
    public void addTask(Task task) {
        if(getLatest(task.getProcessor()).getStartTime() > task.getStartTime())
            throw new IllegalArgumentException("TODO: Code logic for inserting task into processor in the right place");

        _tasks[task.getNode().getId()] = task;
        _processors[task.getProcessor()].add(task);
    }

    @Override
    public Task findTask(Node node) {
        return _tasks[node.getId()];
    }

    @Override
    public Task getLatest(int processor) {
        return _processors[processor].size() > 0 ? _processors[processor].get(_processors[processor].size()) : null;
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
