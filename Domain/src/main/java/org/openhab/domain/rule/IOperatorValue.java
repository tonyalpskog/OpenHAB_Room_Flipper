package org.openhab.domain.rule;

import org.openhab.domain.rule.operators.RuleOperator;

import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface IOperatorValue<T> {
//    public Class<?> getType();
    public List<RuleOperator> getSupportedRelationalOperators();
    public String getFormattedString();
    public String getName();
    public Object getValue();
}
