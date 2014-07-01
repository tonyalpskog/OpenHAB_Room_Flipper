package org.openhab.domain.rule2.values;

import org.openhab.domain.rule2.INode;

import java.util.Date;

public class DateNode implements INode<Date> {
    private final Date mValue;

    public DateNode(Date value) {
        mValue = value;
    }

    @Override
    public Date evaluate() {
        return mValue;
    }
}
