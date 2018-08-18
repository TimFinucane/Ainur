package common.schedule;

import common.graph.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

public class PackedScheduleQueue {
    private ArrayList<Integer>          _lowerBounds = new ArrayList<>();
    private ArrayList<SimpleSchedule>   _schedules = new ArrayList<>();
    private ArrayList<HashSet<Node>>    _visitableNodes = new ArrayList<>();

    /**
     * Adds the given items into their relevant positions in the queue
     */
    public void add(int lowerBound, SimpleSchedule schedule, HashSet<Node> visitableNodes) {
        // Calculate position
        int index;
        if(_lowerBounds.size() > 0)
            index = findPos(lowerBound);
        else
            index = 0;

        _lowerBounds.add(index, lowerBound);
        _schedules.add(index, schedule);
        _visitableNodes.add(index, visitableNodes);
    }

    /**
     * Gets the lower bound of the head item
     */
    public int getLowerBound() {
        return _lowerBounds.get(_lowerBounds.size() - 1);
    }

    /**
     * Gets the head schedule
     */
    public SimpleSchedule getSchedule() {
        return _schedules.get(_schedules.size() - 1);
    }

    /**
     * Gets the head set of visitable nodes
     */
    public HashSet<Node> getVisitableNodes() {
        return _visitableNodes.get(_visitableNodes.size() - 1);
    }

    /**
     * Removes the head item
     */
    public void remove() {
        _lowerBounds.remove(_visitableNodes.size() - 1);
        _schedules.remove(_visitableNodes.size() - 1);
        _visitableNodes.remove(_visitableNodes.size() - 1);
    }

    public int size() {
        return _lowerBounds.size();
    }

    public boolean isEmpty() {
        return _lowerBounds.isEmpty();
    }

    /**
     * Culls the items with a lower bound greater than the given bound
     */
    public void cull(int limit) {
        if(_lowerBounds.size() > 0 && limit <= _lowerBounds.get(0)) {
            int index = findPos(limit + 1);

            _lowerBounds.subList(0, index).clear();
            _schedules.subList(0, index).clear();
            _visitableNodes.subList(0, index).clear();
        }
    }

    private int findPos(int lowerBound) {
        int pos = Collections.binarySearch(_lowerBounds, lowerBound, Comparator.reverseOrder());

        return pos < 0 ? -pos - 1 : pos;
    }
}
