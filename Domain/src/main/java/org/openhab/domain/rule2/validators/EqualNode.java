package org.openhab.domain.rule2.validators;

import org.openhab.domain.rule2.IValidationNode;
import org.openhab.domain.rule2.IValueNode;

public class EqualNode<T> implements IValidationNode {
    private final IValueNode<T> mFirst;
    private final IValueNode<T> mSecond;

    public EqualNode(IValueNode<T> first,
                     IValueNode<T> second) {
        mFirst = first;
        mSecond = second;
    }

    @Override
    public boolean validate() {
        return mFirst.getValue().equals(mSecond.getValue());
    }

    @Override
    public String getName() {
        return "Equal";
    }
}
