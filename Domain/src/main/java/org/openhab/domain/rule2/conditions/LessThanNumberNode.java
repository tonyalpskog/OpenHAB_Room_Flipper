package org.openhab.domain.rule2.conditions;

import org.openhab.domain.rule2.IConditionNode;
import org.openhab.domain.rule2.INode;

public class LessThanNumberNode implements IConditionNode {
    private final INode<Number> mFirst;
    private final INode<Number> mSecond;

    public LessThanNumberNode(INode<Number> first,
                              INode<Number> second) {
        mFirst = first;
        mSecond = second;
    }

    @Override
    public boolean evaluate() {
        return mFirst.getValue().doubleValue() < mSecond.getValue().doubleValue();
    }

}
