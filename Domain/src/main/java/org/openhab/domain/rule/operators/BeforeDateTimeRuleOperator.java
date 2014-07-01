package org.openhab.domain.rule.operators;

import org.openhab.domain.rule.RuleOperatorType;

import java.util.Date;
import java.util.List;

public class BeforeDateTimeRuleOperator extends DateTimeRuleOperator<Date> {
    public BeforeDateTimeRuleOperator() {
        super(RuleOperatorType.Before, false);
    }

    @Override
    public boolean getOperationResult2(List<Date> args) {
        validateArgumentNumber(args);

        return args.get(0).before(args.get(1));
    }
}
