package com.zenit.habclient.rule;

import android.content.Context;

import com.zenit.habclient.HABApplication;
import com.zenit.habclient.OnOperandValueChangedListener;
import com.zenit.habclient.OpenHABWidgetControl;
import com.zenit.habclient.util.StringHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public class Rule implements OnOperandValueChangedListener {
    private String mName;
    protected RuleOperation mRuleOperation;
    protected Context mContext;
    protected List<RuleAction> mActions;//OpenHABNFCActionList, Intent writeTagIntent
    protected OpenHABWidgetControl mOpenHABWidgetControl;
    protected boolean mEnabled;

    public Rule(Context context) {
        this("New Rule", context);
    }

    public Rule(String name, Context context) {
        mContext = context;
        setName(name);
        mOpenHABWidgetControl = HABApplication.getOpenHABWidgetControl(mContext);
        mActions = new ArrayList<RuleAction>();
    }

    public RuleOperation getRuleOperation() {
        return mRuleOperation;
    }

    public void setRuleOperation(RuleOperation ruleOperation) {
        mRuleOperation = ruleOperation;
        ((IRuleOperationOperand)mRuleOperation).setOnOperandValueChangedListener(this);
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
        String result;
        if(!StringHandler.isNullOrEmpty(getName())) {
            result = getName() + " [" + mRuleOperation.getFormattedString() + "]";
        } else {
            result = mRuleOperation.toString();
        }
        return result;
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
            if(StringHandler.isNullOrEmpty(action.OpenHABItemCommand) || (StringHandler.isNullOrEmpty(action.OpenHABItemName) && StringHandler.isNullOrEmpty(action.OpenHABWidgetName)))
                continue;
            mOpenHABWidgetControl.sendItemCommand(StringHandler.isNullOrEmpty(action.OpenHABItemName)? action.OpenHABWidgetName : action.OpenHABItemName, action.OpenHABItemCommand);
        }
    }

    public void addAction(RuleAction action) {
        mActions.add(action);
    }

    public List<RuleAction> getActions() {
        return mActions;
    }
}
