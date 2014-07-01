package org.openhab.domain.rule.operators;

import org.openhab.domain.rule.RuleOperatorType;

import java.util.List;

public class NotEqualBooleanRuleOperator extends BooleanRuleOperator<Boolean> {
    public NotEqualBooleanRuleOperator() {
        super(RuleOperatorType.NotEqual, false);
    }

    @Override
    public boolean getOperationResult2(List<Boolean> args) {
        validateArgumentNumber(args);

        return args.get(0) != args.get(1);
    }
}
