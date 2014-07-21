package org.openhab.domain.rule.operations.number;

import org.openhab.domain.rule.operations.CompositeRuleOperation;
import org.openhab.domain.rule.operations.RuleOperation;

public class BetweenNumberRuleOperation extends CompositeRuleOperation<Number> {
    public BetweenNumberRuleOperation() {
        super("Between number");
    }

    @Override
    protected RuleOperation<Number> createLeft() {
        return new MoreThanNumberRuleOperation();
    }

    @Override
    protected RuleOperation<Number> createRight() {
        return new LessThanNumberRuleOperation();
    }
}
