package visualisation.modules;

import java.math.BigInteger;

public class Statistics {

    private int _maxScheduleBound;
    private int _minScheduleBound;

    private BigInteger _searchSpaceLookedAt;
    private BigInteger _searchSpaceCulled;

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

    public BigInteger getSearchSpaceLookedAt() {
        return _searchSpaceLookedAt;
    }

    public void setSearchSpaceLookedAt(BigInteger _searchSpaceLookedAt) {
        this._searchSpaceLookedAt = _searchSpaceLookedAt;
    }

    public BigInteger getSearchSpaceCulled() {
        return _searchSpaceCulled;
    }

    public void setSearchSpaceCulled(BigInteger _searchSpaceCulled) {
        this._searchSpaceCulled = _searchSpaceCulled;
    }
}