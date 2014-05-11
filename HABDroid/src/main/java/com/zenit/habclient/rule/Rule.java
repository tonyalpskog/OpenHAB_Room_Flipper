package com.zenit.habclient.rule;

/**
 * Created by Tony Alpskog in 2014.
 */
public class Rule {
    private String mName;
    protected RuleOperation mRuleOperation;
    //protected List<IRuleAction> mActions;//OpenHABNFCActionList, Intent writeTagIntent

    public Rule() {
        this("New Rule");
    }

    public Rule(String name) {
        setName(name);
    }

    public RuleOperation getRuleOperation() {
        return mRuleOperation;
    }

    public void setRuleOperation(RuleOperation ruleOperation) {
        mRuleOperation = ruleOperation;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
        if(mRuleOperation != null)
            mRuleOperation.setName(name);
    }
}
