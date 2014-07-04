package org.openhab.domain.rule;

import org.openhab.domain.IOpenHABWidgetControl;
import org.openhab.domain.util.StringHandler;
import org.openhab.domain.wear.IWearCommandHost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2014.
 */
public class Rule implements OnOperandValueChangedListener {
    private String mName;
    protected RuleOperation mRuleOperation;
    protected Map<String, RuleOperation> mRuleOperationDataSourceIdMap;
    protected List<RuleAction> mActions;//OpenHABNFCActionList, Intent writeTagIntent
    protected final IOpenHABWidgetControl mOpenHABWidgetControl;
    protected boolean mEnabled;
    protected AccessModifier mAccess;

    @Inject IWearCommandHost mWearCommandHost;

    public Rule(IOpenHABWidgetControl widgetControl) {
        this("New Rule", widgetControl);
    }

    public Rule(String name, IOpenHABWidgetControl widgetControl) {
        if(widgetControl == null) throw new IllegalArgumentException("widgetControl is null");

        setName(name);
        mOpenHABWidgetControl = widgetControl;
        mActions = new ArrayList<RuleAction>();
        mRuleOperationDataSourceIdMap = new HashMap<String, RuleOperation>();
    }

    public RuleOperation getRuleOperation() {
        return mRuleOperation;
    }

    public void setRuleOperation(RuleOperation ruleOperation) {
        mRuleOperation = ruleOperation;
        if(mRuleOperation != null) {
            ((IRuleOperationOperand) mRuleOperation).setOnOperandValueChangedListener(this);
            getRuleOperation().runCalculation();
        }
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
        if(mRuleOperation != null)
            mRuleOperation.setName(name);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("[" + mRuleOperation.getFormattedString() + "]  ");
        if(!StringHandler.isNullOrEmpty(getName())) {
            result.append(getName());
        } else {
            result.append(mRuleOperation.toString());
        }
        return result.toString();
    }

    public void setEnabled(boolean value) {
        mEnabled = value;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    @Override
    public void onOperandValueChanged(IEntityDataType operand) {
        if(!mEnabled)
            return;

        for(RuleAction action : mActions) {
            if(StringHandler.isNullOrEmpty(action.mTargetOpenHABItemName) || StringHandler.isNullOrEmpty(action.getCommand()))
                continue;
            if(action.getActionType() == RuleActionType.COMMAND)
                mOpenHABWidgetControl.sendItemCommand(action.getTargetOpenHABItemName(), action.getCommand());
            else
                mWearCommandHost.startSession("Rule Action", action.getTextValue());
        }
    }

    public void addAction(RuleAction action) {
        mActions.add(action);
        if(getRuleOperation() != null)
            getRuleOperation().runCalculation();
    }

    public List<RuleAction> getActions() {
        return mActions;
    }


    public AccessModifier getAccess() {
        return mAccess;
    }

    public void setAccess(AccessModifier access) {
        mAccess = access;
    }
}
