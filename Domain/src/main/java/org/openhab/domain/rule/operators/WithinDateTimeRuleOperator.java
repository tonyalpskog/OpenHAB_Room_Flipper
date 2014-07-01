package org.openhab.domain.rule.operators;

import org.openhab.domain.rule.RuleOperatorType;

import java.util.Date;
import java.util.List;

public class WithinDateTimeRuleOperator extends DateTimeRuleOperator<Date> {
    public WithinDateTimeRuleOperator() {
        super(RuleOperatorType.Within, false);
    }

    @Override
    public boolean getOperationResult2(List<Date> args) {
        validateArgumentNumber(args);

        return (args.get(0).after(args.get(1)) || args.get(0).equals(args.get(1))) && (args.get(0).before(args.get(2)) || args.get(0).equals(args.get(2)));
    }
}
