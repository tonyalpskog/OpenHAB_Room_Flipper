package org.openhab.domain.rule2.values;

import org.openhab.domain.rule2.IValueNode;

import java.util.Date;

public class DateNode implements IValueNode<Date> {
    private final Date mValue;

    public DateNode(Date value) {
        mValue = value;
    }

    @Override
    public Date getValue() {
        return mValue;
    }

    @Override
    public String getName() {
        return "DateNode";
    }
}
