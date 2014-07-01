package org.openhab.domain.rule2.conditions;

import org.openhab.domain.rule2.IConditionNode;
import org.openhab.domain.rule2.INode;

public class MoreThanNumberNode implements IConditionNode {
    private final INode<Number> mFirst;
    private final INode<Number> mSecond;

    public MoreThanNumberNode(INode<Number> first,
                              INode<Number> second) {
        mFirst = first;
        mSecond = second;
    }

    @Override
    public Boolean evaluate() {
        return mFirst.evaluate().doubleValue() > mSecond.evaluate().doubleValue();
    }
}
