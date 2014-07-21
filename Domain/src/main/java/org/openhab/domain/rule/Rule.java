package org.openhab.domain.rule;

import org.openhab.domain.rule.operations.RuleOperation;
import org.openhab.domain.user.AccessModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Tony Alpskog in 2014.
 */
public class Rule extends EntityDataType<Boolean> {
    public final static String ARG_RULE_ID = "Rule ID";

    private String mName;
    protected RuleOperation<Boolean> mRuleOperation;
    protected List<RuleAction> mActions;//OpenHABNFCActionList, Intent writeTagIntent
    protected boolean mEnabled;
    protected AccessModifier mAccessModifier;
    protected UUID mRuleId;

    public Rule() {
        this("New Rule");
    }

    public Rule(String name) {
        super(Boolean.class, name);
        setRuleId(UUID.randomUUID());
        setName(name);
        mActions = new ArrayList<RuleAction>();
    }

    public RuleOperation<?> getRuleOperation() {
        return mRuleOperation;
    }

    public void setRuleOperation(RuleOperation ruleOperation) {
        mRuleOperation = ruleOperation;
    }

    public void setEnabled(boolean value) {
        mEnabled = value;
    }

    public void addAction(RuleAction action) {
        mActions.add(action);
    }

    public List<RuleAction> getActions() {
        return mActions;
    }


    public AccessModifier getAccess() {
        return mAccessModifier;
    }

    public void setAccess(AccessModifier access) {
        mAccessModifier = access;
    }

    public UUID getRuleId() {
        return mRuleId;
    }

    public void setRuleId(UUID ruleId) {
        mRuleId = ruleId;
    }

    @Override
    public EntityDataTypeSource getSourceType() {
        return EntityDataTypeSource.OPERATION;
    }

    public void executeActions() {
        for(RuleAction action : mActions) {
            if(action == null) continue;

            action.execute();
        }
    }

    @Override
    public Boolean getValue() {
        final Boolean result = mRuleOperation.getValue();
        if(result) {
            executeActions();
        }
        return result;
    }
}
