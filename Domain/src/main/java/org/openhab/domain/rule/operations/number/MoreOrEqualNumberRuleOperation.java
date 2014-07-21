package org.openhab.domain.rule.operations.number;

public class MoreOrEqualNumberRuleOperation extends NumberRuleOperation {
    public MoreOrEqualNumberRuleOperation() {
        super("More or equal");
    }

    @Override
    public Boolean getValue() {
        return compareNumbers() >= 0;
    }
}
