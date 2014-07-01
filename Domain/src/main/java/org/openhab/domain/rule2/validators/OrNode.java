package org.openhab.domain.rule2.validators;

import org.openhab.domain.rule2.IValidationNode;

public class OrNode implements IValidationNode {
    private final IValidationNode mLeft;
    private final IValidationNode mRight;

    public OrNode(IValidationNode left,
                  IValidationNode right) {
        mLeft = left;
        mRight = right;
    }

    @Override
    public boolean validate() {
        return mLeft.validate() || mRight.validate();
    }

    @Override
    public String getName() {
        return "OR";
    }
}
