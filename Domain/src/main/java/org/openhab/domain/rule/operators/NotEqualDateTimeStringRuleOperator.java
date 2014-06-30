package org.openhab.domain.rule.operators;

import org.openhab.domain.rule.RuleOperatorType;

import java.util.List;

public class NotEqualDateTimeStringRuleOperator extends DateTimeRuleOperator<String> {
    public NotEqualDateTimeStringRuleOperator() {
        super(RuleOperatorType.NotEqual, false);
    }

    @Override
    public boolean getOperationResult2(List<String> args) {
        validateArgumentNumber(args);

        return !args.get(0).equalsIgnoreCase(args.get(1));
    }
}
