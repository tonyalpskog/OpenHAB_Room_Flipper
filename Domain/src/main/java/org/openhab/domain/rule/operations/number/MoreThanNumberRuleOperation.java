package org.openhab.domain.rule.operations.number;

public class MoreThanNumberRuleOperation extends NumberRuleOperation {
    public MoreThanNumberRuleOperation() {
        super("More than");
    }

    @Override
    public Boolean getValue() {
        return compareNumbers() > 0;
    }
}
