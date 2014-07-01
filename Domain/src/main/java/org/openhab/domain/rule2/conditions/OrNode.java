package org.openhab.domain.rule2.conditions;

import org.openhab.domain.rule2.IConditionNode;

public class OrNode implements IConditionNode {
    private final IConditionNode mLeft;
    private final IConditionNode mRight;

    public OrNode(IConditionNode left,
                  IConditionNode right) {
        mLeft = left;
        mRight = right;
    }

    @Override
    public Boolean evaluate() {
        return mLeft.evaluate() || mRight.evaluate();
    }

}
