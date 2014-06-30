package org.openhab.domain.rule.operators;

import org.openhab.domain.rule.RuleOperatorType;

import java.util.List;

public class OrBooleanRuleOperator extends BooleanRuleOperator<Boolean> {
    public OrBooleanRuleOperator() {
        super(RuleOperatorType.Or, false);
    }

    @Override
    public boolean getOperationResult2(List<Boolean> args) {
        validateArgumentNumber(args);

        int index = 0;
        boolean result = false;

        while (!result && index < args.size()) {
            result = result || args.get(index++);
        }

        return result;
    }
}
