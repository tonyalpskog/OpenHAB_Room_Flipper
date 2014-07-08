package org.openhab.domain.rule;

/**
 * Created by Tony Alpskog in 2014.
 */
public class LogicBoolean {
    private Boolean mValue;

    public LogicBoolean(Boolean value) {
        mValue = value;
    }

    public Boolean getValue() {
        return mValue;
    }

    public void setValue(Boolean value) {
        mValue = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof LogicBoolean))
            return false;
        LogicBoolean compareObject = (LogicBoolean) o;
        return getValue() == compareObject.getValue();
    }
}
