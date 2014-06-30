package org.openhab.domain.rule;

/**
 * Created by Tony Alpskog in 2014.
 */
public abstract class BooleanRuleOperator<T> extends RuleOperator<T> {

    public BooleanRuleOperator(RuleOperatorType type, Boolean supportsMultipleOperations) {
        super(type, supportsMultipleOperations);
    }

    @Override
    public T parseValue(String valueAsString) {
        return (T) Boolean.valueOf(valueAsString);
    }
}
