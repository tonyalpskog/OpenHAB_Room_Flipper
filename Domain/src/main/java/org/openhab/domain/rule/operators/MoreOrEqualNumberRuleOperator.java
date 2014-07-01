package org.openhab.domain.rule.operators;

import org.openhab.domain.rule.RuleOperatorType;

import java.util.List;

public class MoreOrEqualNumberRuleOperator extends NumberRuleOperator<Number> {
    public MoreOrEqualNumberRuleOperator() {
        super(RuleOperatorType.MoreOrEqual, false);
    }

    @Override
    public boolean getOperationResult2(List<Number> args) {
        validateArgumentNumber(args);

        return compareNumbers(args.get(0), args.get(1)) >= 0;
    }
}
