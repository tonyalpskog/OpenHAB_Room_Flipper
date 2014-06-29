package org.openhab.domain.business.rule;

import org.openhab.domain.business.IOpenHABWidgetControl;
import org.openhab.domain.util.StringHandler;

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
    protected Map<String, RuleOperation> mRuleOperationDataSourceIdMap;
    protected List<RuleAction> mActions;//OpenHABNFCActionList, Intent writeTagIntent
    protected final IOpenHABWidgetControl mOpenHABWidgetControl;
    protected boolean mEnabled;

    public Rule(IOpenHABWidgetControl widgetControl) {
        this("New Rule", widgetControl);
    }

    public Rule(String name, IOpenHABWidgetControl widgetControl) {
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
        if(mRuleOperation != null)
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
            if(/*StringHandler.isNullOrEmpty(action.OpenHABItemCommand) || */(StringHandler.isNullOrEmpty(action.mTargetOpenHABItemName)))//TODO - TA: Temporary removed code
                continue;
            mOpenHABWidgetControl.sendItemCommand(action.mTargetOpenHABItemName, "ON"/*action.OpenHABItemCommand*/);//TODO - TA: Fix this...
        }
    }

    public void addAction(RuleAction action) {
        mActions.add(action);
    }

    public List<RuleAction> getActions() {
        return mActions;
    }
}
