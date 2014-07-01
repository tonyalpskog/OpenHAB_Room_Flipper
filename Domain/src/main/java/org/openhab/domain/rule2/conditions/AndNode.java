package org.openhab.domain.rule2.conditions;

import org.openhab.domain.rule2.IConditionNode;

public class AndNode implements IConditionNode {
    private final IConditionNode mLeft;
    private final IConditionNode mRight;

    public AndNode(IConditionNode left,
                   IConditionNode right) {
        mLeft = left;
        mRight = right;
    }

    @Override
    public boolean evaluate() {
        return mLeft.evaluate() && mRight.evaluate();
    }

}
