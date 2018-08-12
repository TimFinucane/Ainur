package visualisation;

public class Statistics {

    private int _maxScheduleBound;
    private int _minScheduleBound;

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
}