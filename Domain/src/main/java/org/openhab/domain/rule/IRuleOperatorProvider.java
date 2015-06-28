package org.openhab.domain.rule;

import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.rule.operators.RuleOperator;

import java.util.Set;

public interface IRuleOperatorProvider {
    RuleOperator<?> getRuleOperator(OpenHABWidget openHABWidget, RuleOperatorType type);

    RuleOperator<?> getRuleOperator(Class<?> operandClass, RuleOperatorType type);

    Set<RuleOperatorType> getRuleOperatorTypes(Class<?> operandClass);
}
