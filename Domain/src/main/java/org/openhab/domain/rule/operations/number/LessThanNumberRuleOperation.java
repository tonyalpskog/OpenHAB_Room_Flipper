package org.openhab.domain.rule.operations.number;

import org.openhab.domain.rule.EntityDataTypeSource;

public class LessThanNumberRuleOperation extends NumberRuleOperation {
    public LessThanNumberRuleOperation() {
        super("Less than");
    }

    @Override
    public EntityDataTypeSource getSourceType() {
        return EntityDataTypeSource.OPERATION;
    }

    @Override
    public Boolean getValue() {
        return compareNumbers() < 0;
    }
}
