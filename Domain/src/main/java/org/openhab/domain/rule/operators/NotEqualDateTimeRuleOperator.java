package org.openhab.domain.rule.operators;

import org.openhab.domain.rule.RuleOperatorType;

import java.util.Date;
import java.util.List;

public class NotEqualDateTimeRuleOperator extends DateTimeRuleOperator<Date> {
    public NotEqualDateTimeRuleOperator() {
        super(RuleOperatorType.NotEqual, false);
    }

    @Override
    public boolean getOperationResult2(List<Date> args) {
        validateArgumentNumber(args);

        return !args.get(0).equals(args.get(1));
    }
}
