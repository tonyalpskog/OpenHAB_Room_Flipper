package org.openhab.domain.rule2.conditions;

import org.openhab.domain.rule2.IConditionNode;

public class NotNode implements IConditionNode {
    private final IConditionNode mCondition;

    public NotNode(IConditionNode condition) {
        mCondition = condition;
    }

    @Override
    public Boolean evaluate() {
        return !mCondition.evaluate();
    }

}
