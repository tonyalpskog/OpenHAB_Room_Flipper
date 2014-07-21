package org.openhab.domain.rule;

import org.openhab.domain.user.AccessModifier;

import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface IRuleProvider {
    List<Rule> getUserRules();
    Rule getUserRule(String ruleId);
    List<Rule> getPublicRules();
    void saveRule(Rule rule);
    Rule createNewRule(AccessModifier accessModifier, String ruleName);
}
