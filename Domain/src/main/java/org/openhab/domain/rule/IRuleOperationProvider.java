package org.openhab.domain.rule;

import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.rule.operators.RuleOperator;

import java.util.HashMap;

public interface IRuleOperationProvider {
    HashMap<Class<?>, HashMap<RuleOperatorType, ?>> getOperatorHash();

    HashMap<RuleOperatorType, RuleOperator<?>> getUnitRuleOperator(OpenHABWidget openHABWidget);

    HashMap<RuleOperatorType, RuleOperator<?>> getUnitRuleOperatorHash(Class<?> operandClass);
}
