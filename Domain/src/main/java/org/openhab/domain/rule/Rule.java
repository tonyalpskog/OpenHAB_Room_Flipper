package org.openhab.domain.rule;

import org.openhab.domain.INotificationHost;
import org.openhab.domain.IOpenHABWidgetControl;
import org.openhab.domain.SenderType;
import org.openhab.domain.user.AccessModifier;
import org.openhab.domain.util.StringHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Tony Alpskog in 2014.
 */
public class Rule implements OnOperandValueChangedListener {
    public final static String ARG_RULE_ID = "Rule ID";

    private String mName;
    protected RuleOperation mRuleOperation;
    protected Map<String, RuleOperation> mRuleOperationDataSourceIdMap;
    protected List<RuleAction> mActions;//OpenHABNFCActionList, Intent writeTagIntent
    protected boolean mEnabled;
    protected AccessModifier mAccessModifier;
    protected UUID mRuleId;

    private final IOpenHABWidgetControl mOpenHABWidgetControl;
    private final INotificationHost mNotificationHost;

    public Rule(IOpenHABWidgetControl widgetControl, INotificationHost notificationHost) {
        this("New Rule", widgetControl, notificationHost);
    }

    public Rule(String name, IOpenHABWidgetControl widgetControl, INotificationHost notificationHost) {
        mOpenHABWidgetControl = widgetControl;
        mNotificationHost = notificationHost;
        setRuleId(UUID.randomUUID());
        setName(name);
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
        if(mRuleOperation == null)
            return "<Missing operation>";//TODO - TA: Language independent

        StringBuilder result = new StringBuilder();
        if(!mRuleOperation.isValid())
            result.append("<Invalid operation> ");//TODO - TA: Language independent
        else
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
        if(!mEnabled || operand.getValue().equals(new LogicBoolean(false)))
            return;

        for(RuleAction action : mActions) {
            if((action.getActionType() == RuleActionType.COMMAND && StringHandler.isNullOrEmpty(action.mTargetOpenHABItemName)) || StringHandler.isNullOrEmpty(action.getCommand()))
                continue;
            if(action.getActionType() == RuleActionType.COMMAND)
                mOpenHABWidgetControl.sendItemCommand(action.getTargetOpenHABItemName(), action.getCommand());
            else
                mNotificationHost.startSession(SenderType.System, "Rule Action", action.getTextValue());
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
}
