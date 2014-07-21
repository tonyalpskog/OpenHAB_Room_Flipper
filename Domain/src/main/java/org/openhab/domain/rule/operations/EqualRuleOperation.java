package org.openhab.domain.rule.operations;

public class EqualRuleOperation<T> extends RuleOperation<T> {
    public EqualRuleOperation() {
        super("Equal");
    }

    @Override
    public Boolean getValue() {
        final T leftValue = getLeftValue();
        final T rightValue = getRightValue();

        return leftValue != null && rightValue != null && (
                leftValue == rightValue || leftValue.equals(rightValue)
        );
    }
}
