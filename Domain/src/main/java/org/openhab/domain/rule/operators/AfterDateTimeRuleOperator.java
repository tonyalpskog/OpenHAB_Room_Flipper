package org.openhab.domain.rule.operators;

import org.openhab.domain.rule.RuleOperatorType;

import java.util.Date;
import java.util.List;

public class AfterDateTimeRuleOperator extends DateTimeRuleOperator<Date> {
    public AfterDateTimeRuleOperator() {
        super(RuleOperatorType.After, false);
    }

    @Override
    public boolean getOperationResult2(List<Date> args) {
        validateArgumentNumber(args);

        return args.get(0).after(args.get(1));
    }
}
