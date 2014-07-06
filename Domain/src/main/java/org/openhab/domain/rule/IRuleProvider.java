package org.openhab.domain.rule;

import org.openhab.domain.user.AccessModifier;

import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface IRuleProvider {
    List<Rule> getUserRules(String userId);
    Rule getUserRule(String userId, String ruleId);
    List<Rule> getPublicRules();
    void saveRule(Rule rule, String userId);
    Rule createNewRule(String userId, AccessModifier accessModifier, String ruleName);
}
