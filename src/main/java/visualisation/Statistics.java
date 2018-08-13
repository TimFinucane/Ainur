package visualisation;

public class Statistics {

    private int _maxScheduleBound;
    private int _minScheduleBound;

    private long _searchSpaceLookedAt;
    private long _searchSpaceCulled;

    public int getMaxScheduleBound() {
        return _maxScheduleBound;
    }

    public void setMaxScheduleBound(int _maxScheduleLength) {
        this._maxScheduleBound = _maxScheduleLength;
    }

    public int getMinScheduleBound() {
        return _minScheduleBound;
    }

    public void setMinScheduleBound(int _minScheduleLength) {
        this._minScheduleBound = _minScheduleLength;
    }

    public long getSearchSpaceLookedAt() {
        return _searchSpaceLookedAt;
    }

    public void setSearchSpaceLookedAt(long _searchSpaceLookedAt) {
        this._searchSpaceLookedAt = _searchSpaceLookedAt;
    }

    public long getSearchSpaceCulled() {
        return _searchSpaceCulled;
    }

    public void setSearchSpaceCulled(long _searchSpaceCulled) {
        this._searchSpaceCulled = _searchSpaceCulled;
    }
}