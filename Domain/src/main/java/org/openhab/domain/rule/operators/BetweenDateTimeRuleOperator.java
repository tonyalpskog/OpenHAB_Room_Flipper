package org.openhab.domain.rule.operators;

import org.openhab.domain.rule.RuleOperatorType;

import java.util.Date;
import java.util.List;

public class BetweenDateTimeRuleOperator extends DateTimeRuleOperator<Date> {
    public BetweenDateTimeRuleOperator() {
        super(RuleOperatorType.Between, false);
    }

    @Override
    public boolean getOperationResult2(List<Date> args) {
        validateArgumentNumber(args);

        return args.get(0).after(args.get(1)) && args.get(0).before(args.get(2));
    }
}
