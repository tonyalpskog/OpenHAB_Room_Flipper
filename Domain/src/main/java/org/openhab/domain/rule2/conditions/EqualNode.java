package org.openhab.domain.rule2.conditions;

import org.openhab.domain.rule2.IConditionNode;
import org.openhab.domain.rule2.INode;

public class EqualNode<T> implements IConditionNode {
    private final INode<T> mFirst;
    private final INode<T> mSecond;

    public EqualNode(INode<T> first,
                     INode<T> second) {
        mFirst = first;
        mSecond = second;
    }

    @Override
    public boolean evaluate() {
        return mFirst.getValue().equals(mSecond.getValue());
    }

}
