package org.openhab.domain.rule.operations.date;

import org.openhab.domain.rule.operations.RuleOperation;

import java.util.Date;

public class BeforeDateTimeRuleOperation extends RuleOperation<Date> {
    public BeforeDateTimeRuleOperation() {
        super("Before");
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
