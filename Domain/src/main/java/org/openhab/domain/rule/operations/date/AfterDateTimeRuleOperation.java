package org.openhab.domain.rule.operations.date;

import org.openhab.domain.rule.operations.RuleOperation;

import java.util.Date;

public class AfterDateTimeRuleOperation extends RuleOperation<Date> {
    public AfterDateTimeRuleOperation() {
        super("After");
    }

    @Override
    public Boolean getValue() {
        final Date leftDate = getLeftValue();
        final Date rightDate = getRightValue();

        return leftDate != null && rightDate != null && leftDate.after(rightDate);
    }
}
