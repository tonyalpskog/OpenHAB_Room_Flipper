package org.openhab.domain.rule;

import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface IRuleProvider {
    List<Rule> getUserRules(String userId);
    List<Rule> getPublicRules();
    void saveRule(Rule rule, String userId);
}
