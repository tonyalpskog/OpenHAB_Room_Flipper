package org.openhab.domain.rule2.values;

import org.openhab.domain.rule2.IValidationNode;
import org.openhab.domain.rule2.IValueNode;

public class BooleanNode implements IValidationNode, IValueNode<Boolean> {
    private final boolean mValue;

    public BooleanNode(boolean value) {
        mValue = value;
    }

    @Override
    public boolean validate() {
        return mValue;
    }

    @Override
    public Boolean getValue() {
        return mValue;
    }

    @Override
    public String getName() {
        return "Boolean";
    }
}
