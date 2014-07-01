package org.openhab.domain.rule.operators;

import org.openhab.domain.rule.RuleOperatorType;

import java.util.List;

public class AndBooleanRuleOperator extends BooleanRuleOperator<Boolean> {
    public AndBooleanRuleOperator() {
        super(RuleOperatorType.And, true);
    }

    @Override
    public boolean getOperationResult2(List<Boolean> args) {
        validateArgumentNumber(args);

        int index = 0;
        boolean result = true;

        while (result && index < args.size()) {
            result = result && args.get(index++);
        }

        return result;
    }
}
