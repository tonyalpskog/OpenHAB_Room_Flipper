package org.openhab.domain.rule;

import org.openhab.domain.rule.operations.RuleOperation;

public interface IRuleOperationProvider {

    RuleOperation<?> getRuleOperation(String sourceId);
}
