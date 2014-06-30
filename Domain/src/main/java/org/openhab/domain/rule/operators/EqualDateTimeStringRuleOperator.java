package org.openhab.domain.rule.operators;

import org.openhab.domain.rule.RuleOperatorType;

import java.util.List;

public class EqualDateTimeStringRuleOperator extends DateTimeRuleOperator<String> {
    public EqualDateTimeStringRuleOperator() {
        super(RuleOperatorType.Equal, false);
    }

    @Override
    public boolean getOperationResult2(List<String> args) {
        validateArgumentNumber(args);

        return args.get(0).equalsIgnoreCase(args.get(1));
    }
}
