package org.openhab.domain.rule2.values;

import org.openhab.domain.rule2.IConditionNode;

public class BooleanNode implements IConditionNode {
    private final boolean mValue;

    public BooleanNode(boolean value) {
        mValue = value;
    }

    @Override
    public Boolean evaluate() {
        return mValue;
    }
}
