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
    Map<String, Map<AccessModifier, List<Rule>>> mUserRules;
    Map<AccessModifier, List<Rule>> mRulesAccessMap;

    @Inject
    public RuleProvider() {
        mUserRules = new HashMap<String, Map<AccessModifier, List<Rule>>>();
        mRulesAccessMap = new HashMap<AccessModifier, List<Rule>>();
    }

    @Override
    public List<Rule> getUserRules(String userId) {
        List<Rule> result = new ArrayList<Rule>();
        result.addAll(mUserRules.get(userId).get(AccessModifier.Private));
        result.addAll(mUserRules.get(userId).get(AccessModifier.ReadOnly));
        result.addAll(mUserRules.get(userId).get(AccessModifier.Writable));
        return result;
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
            if (mUserRules.get(userId).get(accessModifier).contains(rule))
                mUserRules.get(userId).get(accessModifier).remove(rule);
            if(mRulesAccessMap.get(accessModifier).contains(rule))
                mRulesAccessMap.get(accessModifier).remove(rule);
            break;
        }
        mUserRules.get(userId).get(rule.getAccess()).add(rule);
        mRulesAccessMap.get(rule.getAccess()).add(rule);
    }
}

