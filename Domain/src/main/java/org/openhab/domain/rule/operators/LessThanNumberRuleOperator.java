package org.openhab.domain.rule.operators;

import org.openhab.domain.rule.RuleOperatorType;

import java.util.List;

public class LessThanNumberRuleOperator extends NumberRuleOperator<Number> {
    public LessThanNumberRuleOperator() {
        super(RuleOperatorType.LessThan, false);
    }

    @Override
    public boolean getOperationResult2(List<Number> args) {
        validateArgumentNumber(args);

        return compareNumbers(args.get(0), args.get(1)) < 0;
    }
}
