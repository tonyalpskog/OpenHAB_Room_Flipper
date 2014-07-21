package org.openhab.domain.rule.operations.date;

import org.openhab.domain.rule.operations.RuleOperation;

import java.util.Date;

public class AfterOrEqualDateTimeRuleOperation extends RuleOperation<Date> {
    public AfterOrEqualDateTimeRuleOperation() {
        super("After or equal to");
    }

    @Override
    public Boolean getValue() {
        final Date leftDate = getLeftValue();
        final Date rightDate = getRightValue();

        return leftDate != null && rightDate != null && (
                leftDate.after(rightDate) || leftDate.equals(rightDate)
        );
    }
}
