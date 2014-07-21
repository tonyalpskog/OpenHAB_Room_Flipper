package org.openhab.domain.rule.operations.number;

import org.openhab.domain.rule.operations.RuleOperation;

/**
 * Created by Tony Alpskog in 2014.
 */
public abstract class NumberRuleOperation extends RuleOperation<Number> {
    private static final double EPSILON = 8E-7f;

    public NumberRuleOperation(String name) {
        super(name);
    }

    protected int compareNumbers()
    {
        Number a = getLeftValue();
        Number b = getRightValue();
        if (a == null)
        {
            return b == null? 0: -1;
        }
        else if (b == null)
        {
            return 1;
        }
        else
        {
            if(Math.abs(a.doubleValue() - b.doubleValue()) < EPSILON)
                return 0;
            else
                return a.doubleValue() > b.doubleValue()? 1: -1;
        }
    }
}
