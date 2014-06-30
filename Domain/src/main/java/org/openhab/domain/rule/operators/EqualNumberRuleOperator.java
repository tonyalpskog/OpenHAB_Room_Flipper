package org.openhab.domain.rule.operators;

import org.openhab.domain.rule.RuleOperatorType;

import java.util.List;

public class EqualNumberRuleOperator extends NumberRuleOperator<Number> {
    public EqualNumberRuleOperator() {
        super(RuleOperatorType.Equal, false);
    }

    @Override
    public boolean getOperationResult2(List<Number> args) {
        validateArgumentNumber(args);

        return compareNumbers(args.get(0), args.get(1)) == 0;
    }
}
