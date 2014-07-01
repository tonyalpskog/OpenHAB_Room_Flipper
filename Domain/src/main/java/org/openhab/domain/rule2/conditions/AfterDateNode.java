package org.openhab.domain.rule2.conditions;

import org.openhab.domain.rule2.IConditionNode;
import org.openhab.domain.rule2.INode;

import java.util.Date;

public class AfterDateNode implements IConditionNode {
    private final INode<Date> mFirst;
    private final INode<Date> mSecond;

    public AfterDateNode(INode<Date> first,
                         INode<Date> second) {
        mFirst = first;
        mSecond = second;
    }

    @Override
    public Boolean evaluate() {
        return mFirst.evaluate().after(mSecond.evaluate());
    }

}
