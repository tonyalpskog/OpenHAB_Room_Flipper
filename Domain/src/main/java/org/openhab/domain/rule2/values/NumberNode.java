package org.openhab.domain.rule2.values;

import org.openhab.domain.rule2.INode;

public class NumberNode implements INode<Number> {
    private final Number mNumber;

    public NumberNode(Number number) {
        mNumber = number;
    }

    @Override
    public Number evaluate() {
        return mNumber;
    }
}
