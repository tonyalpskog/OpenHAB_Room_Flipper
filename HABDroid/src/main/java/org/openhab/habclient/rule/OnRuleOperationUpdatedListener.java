package org.openhab.habclient.rule;

import org.openhab.domain.rule.operations.RuleOperation;

public interface OnRuleOperationUpdatedListener {
    void onRuleOperationUpdated(RuleOperation<?> ruleOperation);
}
