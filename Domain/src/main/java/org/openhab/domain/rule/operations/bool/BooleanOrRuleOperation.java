package org.openhab.domain.rule.operations.bool;

import org.openhab.domain.rule.operations.RuleOperation;

public class BooleanOrRuleOperation extends RuleOperation<Boolean> {
    public BooleanOrRuleOperation() {
        super("Or");
    }

    @Override
    public Boolean getValue() {
        final Boolean leftValue = getLeftValue();
        if(leftValue != null && leftValue)
            return true;

        final Boolean rightValue = getRightValue();
        return rightValue != null && rightValue;
    }
}
