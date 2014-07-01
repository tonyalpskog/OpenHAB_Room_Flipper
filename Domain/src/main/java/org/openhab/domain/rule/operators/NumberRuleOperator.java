package org.openhab.domain.rule.operators;

import org.openhab.domain.rule.RuleOperatorType;

/**
 * Created by Tony Alpskog in 2014.
 */
public abstract class NumberRuleOperator<T> extends RuleOperator<T> {
    private static final double EPSILON = 8E-7f;

    public NumberRuleOperator(RuleOperatorType type, Boolean supportsMultipleOperations) {
        super(type, supportsMultipleOperations);
    }

    @Override
    public T parseValue(String valueAsString) {
        return (T) Float.valueOf(valueAsString);
    }

    protected int compareNumbers(Number a, Number b)
    {
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
