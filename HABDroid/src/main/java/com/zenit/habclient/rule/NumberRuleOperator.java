package com.zenit.habclient.rule;

import java.util.Date;

/**
 * Created by Tony Alpskog in 2014.
 */
public abstract class NumberRuleOperator<T> extends RuleOperator<T> {

    public NumberRuleOperator(RuleOperatorType type, Boolean supportsMultipleOperations) {
        super(type, supportsMultipleOperations);
    }

    @Override
    public T parseValue(String valueAsString) {
        return (T) Float.valueOf(valueAsString);
    }
}
