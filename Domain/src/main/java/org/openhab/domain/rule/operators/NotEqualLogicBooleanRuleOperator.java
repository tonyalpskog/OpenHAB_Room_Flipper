package org.openhab.domain.rule.operators;

import org.openhab.domain.rule.LogicBoolean;
import org.openhab.domain.rule.RuleOperatorType;

import java.util.List;

public class NotEqualLogicBooleanRuleOperator extends BooleanRuleOperator<LogicBoolean> {
    public NotEqualLogicBooleanRuleOperator() {
        super(RuleOperatorType.NotEqual, false);
    }

    @Override
    public boolean getOperationResult2(List<LogicBoolean> args) {
        validateArgumentNumber(args);

        return args.get(0).getValue() != args.get(1).getValue();
    }
}