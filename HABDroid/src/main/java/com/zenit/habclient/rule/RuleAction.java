package com.zenit.habclient.rule;

import com.zenit.habclient.util.StringHandler;

import java.util.UUID;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleAction {
    protected String mTargetOpenHABItemName;
    protected String mSourceOpenHABItemName;
    protected RuleActionType mActionType;
    protected String mStaticValue;
    protected String mTextValue;
    protected String mID;

    public RuleAction(RuleActionType mActionType) {
        this.mActionType = mActionType;
        mID = UUID.randomUUID().toString();
    }

    public boolean validate() {
        //TODO - TA: 1. Check if target getUnitEntityDataType match the source unit if any.
        //TODO - TA: 2. Check if target getUnitEntityDataType has getStaticValues() that match mStaticValue if it´s not null.
        //TODO - TA: 3. Clear any COMMAND specific data if of type MESSAGE and vice versa.
       return true;
    }

    public String getID() {
        return mID;
    }

    public String getCommand() {
        return "joo";
    }

    public RuleActionValueType getValueType() {
        if(mSourceOpenHABItemName != null)
            return RuleActionValueType.SOURCE_UNIT;
        if(mStaticValue != null)
            return RuleActionValueType.STATIC;
        if(mTextValue != null)
            return RuleActionValueType.TEXT;
        return RuleActionValueType.NA;
    }

    public RuleActionType getActionType() {
        return mActionType;
    }

    public void setActionType(RuleActionType actionType) {
        mActionType = actionType;
    }
    public String getTargetOpenHABItemName() {
        return mTargetOpenHABItemName;
    }

    public void setTargetOpenHABItemName(String targetOpenHABItemName) {
        mTargetOpenHABItemName = targetOpenHABItemName;
    }

    public String getSourceOpenHABItemName() {
        return mSourceOpenHABItemName;
    }

    public void setSourceOpenHABItemName(String sourceOpenHABItemName) {
        mSourceOpenHABItemName = sourceOpenHABItemName;
        if(mSourceOpenHABItemName != null)
            mStaticValue = mTextValue = null;
    }

    public String getStaticValue() {
        return mStaticValue;
    }

    public void setStaticValue(String staticValue) {
        mStaticValue = staticValue;
        if(mStaticValue != null)
            mSourceOpenHABItemName = mTextValue = null;
    }

    public String getTextValue() {
        return mTextValue;
    }

    public void setTextValue(String textValue) {
        mTextValue = textValue;
        if(mTextValue != null)
            mSourceOpenHABItemName = mStaticValue = null;
    }

    @Override
    public String toString() {
        //TODO - TA: use resource strings (missing context)
        StringBuilder sb = new StringBuilder();
        if(getActionType() == RuleActionType.COMMAND) {
            sb.append(StringHandler.isNullOrEmpty(mTargetOpenHABItemName) ? "<No target>" : mTargetOpenHABItemName);
            sb.append(" = ");
        } else {
            sb.append("Send message: ");
        }
        switch (getValueType()) {
            case SOURCE_UNIT: sb.append(mSourceOpenHABItemName);
                break;
            case STATIC: sb.append(mStaticValue);
                break;
            case TEXT: sb.append(mTextValue == null? "<No message>" : "'" + mTextValue + "'");
                break;
            default: sb.append("<No value>"/*getString(R.string.no_value)*/);
        }
        return sb.toString();
    }
}

