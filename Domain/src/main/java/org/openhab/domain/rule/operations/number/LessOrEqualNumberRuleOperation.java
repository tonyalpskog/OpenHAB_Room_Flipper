package org.openhab.domain.rule.operations.number;

public class LessOrEqualNumberRuleOperation extends NumberRuleOperation {
    public LessOrEqualNumberRuleOperation() {
        super("Less than or equal");
    }

    @Override
    public Boolean getValue() {
        return compareNumbers() <= 0;
    }
}
