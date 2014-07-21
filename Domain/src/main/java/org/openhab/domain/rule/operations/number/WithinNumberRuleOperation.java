package org.openhab.domain.rule.operations.number;

import org.openhab.domain.rule.operations.CompositeRuleOperation;
import org.openhab.domain.rule.operations.RuleOperation;

public class WithinNumberRuleOperation extends CompositeRuleOperation<Number> {
    public WithinNumberRuleOperation() {
        super("Within number");
    }

    @Override
    protected RuleOperation<Number> createLeft() {
        return new MoreOrEqualNumberRuleOperation();
    }

    @Override
    protected RuleOperation<Number> createRight() {
        return new LessOrEqualNumberRuleOperation();
    }
}
