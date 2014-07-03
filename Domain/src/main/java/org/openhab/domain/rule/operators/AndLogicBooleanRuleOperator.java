package org.openhab.domain.rule.operators;

import org.openhab.domain.rule.LogicBoolean;
import org.openhab.domain.rule.RuleOperatorType;

import java.util.List;

public class AndLogicBooleanRuleOperator extends BooleanRuleOperator<LogicBoolean> {
    public AndLogicBooleanRuleOperator() {
        super(RuleOperatorType.And, true);
    }

    @Override
    public boolean getOperationResult2(List<LogicBoolean> args) {
        validateArgumentNumber(args);

        int index = 0;
        boolean result = true;

        while (result && index < args.size()) {
            result = result && args.get(index++).getValue().booleanValue();
        }

        return result;
    }
}
