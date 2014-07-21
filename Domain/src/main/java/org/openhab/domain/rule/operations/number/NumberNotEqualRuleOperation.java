package org.openhab.domain.rule.operations.number;

public class NumberNotEqualRuleOperation extends NumberRuleOperation {
    public NumberNotEqualRuleOperation() {
        super("Number not equal");
    }

    @Override
    public Boolean getValue() {
        return compareNumbers() != 0;
    }
}
