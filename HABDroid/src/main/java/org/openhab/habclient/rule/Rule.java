package org.openhab.habclient.rule;

import android.content.Context;

import org.openhab.habclient.HABApplication;
import org.openhab.habclient.OnOperandValueChangedListener;
import org.openhab.habclient.OpenHABWidgetControl;
import org.openhab.habclient.util.StringHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tony Alpskog in 2014.
 */
public class Rule implements OnOperandValueChangedListener {
    private String mName;
    protected RuleOperation mRuleOperation;
    protected Context mContext;
    protected Map<String, RuleOperation> mRuleOperationDataSourceIdMap;
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
            if(!(StringHandler.isNullOrEmpty(action.mTargetOpenHABItemName) && StringHandler.isNullOrEmpty(action.getCommand())))
                continue;
            mOpenHABWidgetControl.sendItemCommand(action.getTargetOpenHABItemName(), action.getCommand());
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
}
