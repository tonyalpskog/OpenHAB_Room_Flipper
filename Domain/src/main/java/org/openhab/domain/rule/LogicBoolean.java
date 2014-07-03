package org.openhab.domain.rule;

/**
 * Created by Tony Alpskog on 2014-07-03.
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
}
