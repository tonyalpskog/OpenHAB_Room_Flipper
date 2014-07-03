package org.openhab.domain.rule.operators;

import org.openhab.domain.rule.LogicBoolean;
import org.openhab.domain.rule.RuleOperatorType;

import java.util.List;

public class OrLogicBooleanRuleOperator extends BooleanRuleOperator<LogicBoolean> {
    public OrLogicBooleanRuleOperator() {
        super(RuleOperatorType.Or, false);
    }

    @Override
    public boolean getOperationResult2(List<LogicBoolean> args) {
        validateArgumentNumber(args);

        int index = 0;
        boolean result = false;

        for(LogicBoolean value : args) {
            result = result || value.getValue();
            if(result) return true;
        }

        return result;
    }
}
