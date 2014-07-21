package org.openhab.domain.rule.operations;

public class NotEqualRuleOperation<T> extends RuleOperation<T> {
    public NotEqualRuleOperation() {
        super("Not equal to");
    }

    @Override
    public Boolean getValue() {
        final T leftValue = getLeftValue();
        final T rightValue = getRightValue();

        return leftValue == null || rightValue == null || !leftValue.equals(rightValue);
    }
}
