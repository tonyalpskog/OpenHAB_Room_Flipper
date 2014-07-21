package org.openhab.domain.rule;

import org.openhab.domain.user.AccessModifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleProvider implements IRuleProvider {
    private static final String HARDCODED_USER = "Admin123";
    private final Map<String, List<Rule>> mUserRules;
    private final Map<AccessModifier, List<Rule>> mRulesAccessMap;

    @Inject
    public RuleProvider() {
        mUserRules = new HashMap<String,List<Rule>>();
        mRulesAccessMap = new HashMap<AccessModifier, List<Rule>>();
    }

    @Override
    public List<Rule> getUserRules() {
        if(!mUserRules.containsKey(HARDCODED_USER))
            return new ArrayList<Rule>();

        return mUserRules.get(HARDCODED_USER);
    }

    @Override
    public Rule getUserRule(String ruleId) {
        if(ruleId == null)
            return null;

        List<Rule> ruleList = getUserRules();
        for(Rule rule : ruleList) {
            if(ruleId.equalsIgnoreCase(rule.getRuleId().toString()))
                return rule;
        }

        return null;
    }

    @Override
    public List<Rule> getPublicRules() {
        List<Rule> result = new ArrayList<Rule>();
        result.addAll(mRulesAccessMap.get(AccessModifier.ReadOnly));
        result.addAll(mRulesAccessMap.get(AccessModifier.Writable));
        return result;
    }

    @Override
    public void saveRule(Rule rule) {
        if(mUserRules.get(HARDCODED_USER) == null)
            mUserRules.put(HARDCODED_USER, new ArrayList<Rule>());
        if(mRulesAccessMap.get(rule.getAccess()) == null)
            mRulesAccessMap.put(rule.getAccess(), new ArrayList<Rule>());

        for(AccessModifier accessModifier : AccessModifier.values()) {
            if (mUserRules.get(HARDCODED_USER) != null && mUserRules.get(HARDCODED_USER).contains(rule))
                mUserRules.get(HARDCODED_USER).remove(rule);
            if(mRulesAccessMap.get(accessModifier) != null && mRulesAccessMap.get(accessModifier).contains(rule))
                mRulesAccessMap.get(accessModifier).remove(rule);
        }

        mUserRules.get(HARDCODED_USER).add(rule);
        mRulesAccessMap.get(rule.getAccess()).add(rule);
    }

    @Override
    public Rule createNewRule(AccessModifier accessModifier, String ruleName) {
        final Rule rule = new Rule(ruleName);
        if(accessModifier != null)
            rule.setAccess(accessModifier);

        saveRule(rule);
        return rule;
    }
}

