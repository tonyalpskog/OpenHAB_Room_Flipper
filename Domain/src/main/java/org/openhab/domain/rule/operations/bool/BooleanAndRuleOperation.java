package org.openhab.domain.rule.operations.bool;

import org.openhab.domain.rule.operations.RuleOperation;

public class BooleanAndRuleOperation extends RuleOperation<Boolean> {
    public BooleanAndRuleOperation() {
        super("And");
    }

    @Override
    public Boolean getValue() {
        final Boolean leftValue = getLeftValue();
        final Boolean rightValue = getRightValue();

        return leftValue != null && rightValue != null && leftValue && rightValue;
    }
}
