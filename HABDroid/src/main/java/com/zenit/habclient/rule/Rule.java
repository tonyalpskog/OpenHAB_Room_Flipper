package com.zenit.habclient.rule;

import com.zenit.habclient.OnOperandValueChangedListener;
import com.zenit.habclient.OpenHABWidgetControl;
import com.zenit.habclient.util.StringHandler;

/**
 * Created by Tony Alpskog in 2014.
 */
public class Rule implements OnOperandValueChangedListener {
    private String mName;
    protected RuleOperation mRuleOperation;
    protected List<RuleAction> mActions;//OpenHABNFCActionList, Intent writeTagIntent

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

    @Override
    public void onOperandValueChanged(IEntityDataType operand) {

        openHABWidgetControl.sendItemCommand(unitMatchResult.get(bestKeySoFar).getWidget().getItem(), value);
    }
}
