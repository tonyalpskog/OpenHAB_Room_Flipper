package org.openhab.domain.rule;

import org.openhab.domain.IOpenHABWidgetControl;
import org.openhab.domain.user.AccessModifier;
import org.openhab.domain.util.StringHandler;

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

    @Inject IOpenHABWidgetControl mOpenHABWidgetControl;

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
        if(ruleList == null)
            return null;
        for(Rule rule : ruleList) {
            if(ruleId.endsWith(rule.getRuleId().toString()))
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
    public void saveRule(Rule rule, String userId) {
        if(mUserRules.get(userId) == null)
            mUserRules.put(userId, new ArrayList<Rule>());
        if(mRulesAccessMap.get(rule.getAccess()) == null)
            mRulesAccessMap.put(rule.getAccess(), new ArrayList<Rule>());

        for(AccessModifier accessModifier : AccessModifier.values()) {
            if (mUserRules.get(userId) != null && mUserRules.get(userId).contains(rule))
                mUserRules.get(userId).remove(rule);
            if(mRulesAccessMap.get(accessModifier) != null && mRulesAccessMap.get(accessModifier).contains(rule))
                mRulesAccessMap.get(accessModifier).remove(rule);
            break;
        }

        mUserRules.get(userId).add(rule);
        mRulesAccessMap.get(rule.getAccess()).add(rule);
    }

    @Override
    public Rule createNewRule(String userId, AccessModifier accessModifier, String ruleName) {
        if(StringHandler.isNullOrEmpty(userId))
            throw new IllegalArgumentException("userId is null or empty");
        String name = StringHandler.isNullOrEmpty(ruleName)? "New rule" : ruleName;
        Rule rule = new Rule(ruleName, mOpenHABWidgetControl);
        if(accessModifier != null)
            rule.setAccess(accessModifier);
        saveRule(rule, userId);
        return rule;
    }
}

