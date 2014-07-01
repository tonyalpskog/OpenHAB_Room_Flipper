package org.openhab.domain.rule2.values;

import org.openhab.domain.rule2.IValueNode;

public class NumberNode implements IValueNode<Number> {
    private final Number mNumber;

    public NumberNode(Number number) {
        mNumber = number;
    }

    @Override
    public Number getValue() {
        return mNumber;
    }

    @Override
    public String getName() {
        return "Number";
    }
}
