package visualisation;

public class Statistics {

    private int _maxScheduleLength;
    private int _minScheduleLength;

    public int getMaxScheduleBound() {
        return _maxScheduleLength;
    }

    public void setMaxScheduleBound(int _maxScheduleLength) {
        this._maxScheduleLength = _maxScheduleLength;
    }

    public int getMinScheduleBound() {
        return _minScheduleLength;
    }

    public void setMinScheduleBound(int _minScheduleLength) {
        this._minScheduleLength = _minScheduleLength;
    }
}
