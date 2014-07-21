package org.openhab.domain.rule.operations.number;

public class NumberEqualRuleOperation extends NumberRuleOperation {
    public NumberEqualRuleOperation() {
        super("Number equal");
    }

    @Override
    public Boolean getValue() {
        return compareNumbers() == 0;
    }
}
