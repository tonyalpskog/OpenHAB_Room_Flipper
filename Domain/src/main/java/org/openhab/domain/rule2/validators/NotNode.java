package org.openhab.domain.rule2.validators;

import org.openhab.domain.rule2.IValidationNode;

public class NotNode implements IValidationNode {
    private final IValidationNode mValidationNode;

    public NotNode(IValidationNode validationNode) {
        mValidationNode = validationNode;
    }

    @Override
    public boolean validate() {
        return !mValidationNode.validate();
    }

    @Override
    public String getName() {
        return "Not";
    }
}
