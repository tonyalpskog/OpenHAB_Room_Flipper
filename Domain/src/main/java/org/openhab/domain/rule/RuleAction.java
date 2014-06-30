package org.openhab.domain.rule;

import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.model.OpenHABItemType;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.util.StringHandler;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleAction {
    protected String mTargetOpenHABItemName;
    protected String mSourceOpenHABItemName;
    protected RuleActionType mActionType;
    private final IOpenHABWidgetProvider widgetProvider;
    protected String mStaticValue;
    protected String mTextValue;
    protected String mID;

    public RuleAction(RuleActionType mActionType, IOpenHABWidgetProvider widgetProvider, String id) {
        this.mActionType = mActionType;
        this.widgetProvider = widgetProvider;
        mID = UUID.randomUUID().toString();
    }

    public RuleAction(RuleActionType mActionType, IOpenHABWidgetProvider widgetProvider) {
        this.mActionType = mActionType;
        this.widgetProvider = widgetProvider;
        mID = UUID.randomUUID().toString();
    }

    public boolean validate() {
        if(StringHandler.isNullOrEmpty(mTargetOpenHABItemName)) {
            //mTargetOpenHABItemName = mStaticValue = mTextValue = null;//TODO - TA: need code here? I dont think misc data shall be cleared due to missing target.
        } else {
            //Check if target getUnitEntityDataType match the source unit if any.
            final OpenHABWidget targetWidget = widgetProvider.getWidgetByItemName(mTargetOpenHABItemName);
            final OpenHABWidget sourceWidget = widgetProvider.getWidgetByItemName(mSourceOpenHABItemName);

            OpenHABItemType targetType = targetWidget.getItem().getType();
            if(!targetType.equals(OpenHABItemType.String) && !StringHandler.isNullOrEmpty(mSourceOpenHABItemName) && sourceWidget.getItem().getType().equals(targetType)) {
                mSourceOpenHABItemName = null;
            }
            //Check if target getUnitEntityDataType has getStaticValues() that match mStaticValue if it´s not null.
            if(!StringHandler.isNullOrEmpty(mStaticValue)) {
                UnitEntityDataType unitEntityDataType = UnitEntityDataType.getUnitEntityDataType(targetWidget);
                Map<String, ?> staticValueHash = unitEntityDataType.getStaticValues();
                if(staticValueHash == null || !staticValueHash.containsKey(mStaticValue))
                    mStaticValue = null;
            }
        }
        //TODO - TA: 3. Clear any COMMAND specific data if of type MESSAGE and vice versa.
       return true;
    }

    public String getID() {
        return mID;
    }

    public String getCommand() {
        switch(getValueType()) {
            case SOURCE_UNIT: return widgetProvider.getWidgetByItemName(getSourceOpenHABItemName()).getItem().getState();
            case STATIC: return getStaticValue();
            case TEXT: return getTextValue();
            default: return null;
        }
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
