package visualisation;

public class Statistics {

    private int _maxScheduleBound;
    private int _minScheduleBound;

    private long _brancesLookedAt;
    private int _processorsUsed;
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

    public long getBrancesLookedAt() {
        return _brancesLookedAt;
    }

    public void setBrancesLookedAt(long _brancesLookedAt) {
        this._brancesLookedAt = _brancesLookedAt;
    }

    public int getProcessorsUsed() {
        return _processorsUsed;
    }

    public void setProcessorsUsed(int _processorsUsed) {
        this._processorsUsed = _processorsUsed;
    }

    public long getSearchSpaceLookedAt() {
        return _searchSpaceLookedAt;
    }

    public void setSearchSpaceLookedAt(long _ratioLookedAt) {
        this._searchSpaceLookedAt = _ratioLookedAt;
    }

    public long getSearchSpaceCulled() {
        return _searchSpaceCulled;
    }

    public void setSearchSpaceCulled(long _percentCulled) {
        this._searchSpaceCulled = _percentCulled;
    }
}