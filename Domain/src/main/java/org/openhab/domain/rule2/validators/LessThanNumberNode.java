package org.openhab.domain.rule2.validators;

import org.openhab.domain.rule2.IValidationNode;
import org.openhab.domain.rule2.IValueNode;

public class LessThanNumberNode implements IValidationNode {
    private final IValueNode<Number> mFirst;
    private final IValueNode<Number> mSecond;

    public LessThanNumberNode(IValueNode<Number> first,
                              IValueNode<Number> second) {
        mFirst = first;
        mSecond = second;
    }

    @Override
    public boolean validate() {
        return mFirst.getValue().doubleValue() < mSecond.getValue().doubleValue();
    }

    @Override
    public String getName() {
        return "Less than";
    }
}
