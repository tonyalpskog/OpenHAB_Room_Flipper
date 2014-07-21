package org.openhab.domain.rule;

import org.openhab.domain.rule.operations.RuleOperation;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleOperationProvider implements IRuleOperationProvider {

    @Inject
    public RuleOperationProvider() {

    }

    @Override
    public RuleOperation<?> getRuleOperation(String sourceId) {
        return null;
    }
}
