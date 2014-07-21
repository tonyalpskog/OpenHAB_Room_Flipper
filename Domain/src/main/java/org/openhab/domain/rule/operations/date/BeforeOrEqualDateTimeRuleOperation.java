package org.openhab.domain.rule.operations.date;

import org.openhab.domain.rule.operations.RuleOperation;

import java.util.Date;

public class BeforeOrEqualDateTimeRuleOperation extends RuleOperation<Date> {
    public BeforeOrEqualDateTimeRuleOperation() {
        super("Before or equal to");
    }

    @Override
    public Boolean getValue() {
        final Date leftDate = getLeftValue();
        final Date rightDate = getRightValue();

        return leftDate != null && rightDate != null && (
                leftDate.before(rightDate) || leftDate.equals(rightDate)
        );
    }
}
