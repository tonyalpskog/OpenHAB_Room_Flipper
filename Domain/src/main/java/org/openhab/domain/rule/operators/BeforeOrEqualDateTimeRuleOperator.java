package org.openhab.domain.rule.operators;

import org.openhab.domain.rule.RuleOperatorType;

import java.util.Date;
import java.util.List;

public class BeforeOrEqualDateTimeRuleOperator extends DateTimeRuleOperator<Date> {
    public BeforeOrEqualDateTimeRuleOperator() {
        super(RuleOperatorType.BeforeOrEqual, false);
    }

    @Override
    public boolean getOperationResult2(List<Date> args) {
        validateArgumentNumber(args);

        return args.get(0).before(args.get(1)) || args.get(0).equals(args.get(1));
    }
}
