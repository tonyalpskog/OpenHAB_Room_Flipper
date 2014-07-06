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
    Map<String, List<Rule>> mUserRules;
    Map<AccessModifier, List<Rule>> mRulesAccessMap;

    @Inject
    public RuleProvider() {
        mUserRules = new HashMap<String,List<Rule>>();
        mRulesAccessMap = new HashMap<AccessModifier, List<Rule>>();
    }

    @Override
    public List<Rule> getUserRules(String userId) {
        return mUserRules.get(userId);
    }

    @Override
    public Rule getUserRule(String userId, String ruleId) {
        if(ruleId == null || userId == null)
            return null;

        List<Rule> ruleList = getUserRules(userId);
        for(Rule rule : ruleList) {
            if(ruleId.endsWith(rule.getRuleId().toString()))
                return rule;
        }
    }

    @Override
    public List<Rule> getPublicRules() {
        List<Rule> result = new ArrayList<Rule>();
        result.addAll(mRulesAccessMap.get(AccessModifier.ReadOnly));
        result.addAll(mRulesAccessMap.get(AccessModifier.Writable));
        return result;
    }

    @Override
    public void saveRule(Rule rule, String userId) {
        for(AccessModifier accessModifier : AccessModifier.values()) {
            if (mUserRules.get(userId).contains(rule))
                mUserRules.get(userId).remove(rule);
            if(mRulesAccessMap.get(accessModifier).contains(rule))
                mRulesAccessMap.get(accessModifier).remove(rule);
            break;
        }
        mUserRules.get(userId).add(rule);
        mRulesAccessMap.get(rule.getAccess()).add(rule);
    }
}

