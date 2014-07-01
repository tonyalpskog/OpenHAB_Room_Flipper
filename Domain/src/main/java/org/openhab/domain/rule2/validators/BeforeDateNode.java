package org.openhab.domain.rule2.validators;

import org.openhab.domain.rule2.IValidationNode;
import org.openhab.domain.rule2.IValueNode;

import java.util.Date;

public class BeforeDateNode implements IValidationNode {
    private final IValueNode<Date> mFirst;
    private final IValueNode<Date> mSecond;

    public BeforeDateNode(IValueNode<Date> first,
                          IValueNode<Date> second) {
        mFirst = first;
        mSecond = second;
    }

    @Override
    public boolean validate() {
        return mFirst.getValue().before(mSecond.getValue());
    }

    @Override
    public String getName() {
        return "Before date";
    }
}
