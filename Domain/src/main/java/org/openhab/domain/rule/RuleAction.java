package org.openhab.domain.rule;

import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.IUnitEntityDataTypeProvider;
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
    protected UnitEntityDataType mSourceUnit;
    protected RuleActionType mActionType;
    protected String mStaticValue;
    protected String mTextValue;
    protected String mID;

    private final IOpenHABWidgetProvider widgetProvider;
    private final IUnitEntityDataTypeProvider mUnitEntityDataTypeProvider;

    public RuleAction(RuleActionType mActionType, IOpenHABWidgetProvider widgetProvider, IUnitEntityDataTypeProvider IUnitEntityDataTypeProvider) {
        this.mActionType = mActionType;
        this.widgetProvider = widgetProvider;
        mID = UUID.randomUUID().toString();
        mUnitEntityDataTypeProvider = IUnitEntityDataTypeProvider;
    }

    public boolean validate() {
        if(StringHandler.isNullOrEmpty(mTargetOpenHABItemName)) {
            //mTargetOpenHABItemName = mStaticValue = mTextValue = null;//TODO - TA: need code here? I dont think misc data shall be cleared due to missing target.
        } else {
            //Check if target getUnitEntityDataType match the source unit if any.
            final OpenHABWidget targetWidget = widgetProvider.getWidgetByItemName(mTargetOpenHABItemName);
            final OpenHABWidget sourceWidget = mSourceUnit == null? null : widgetProvider.getWidgetByItemName(mSourceUnit.mDataSourceId);

            OpenHABItemType targetType = targetWidget.getItem().getType();
            if(!targetType.equals(OpenHABItemType.String) && mSourceUnit != null && !mSourceUnit.getSourceType().equals(targetType)) {
                removeSourceUnit();
            }
            //Check if target getUnitEntityDataType has getStaticValues() that match mStaticValue if it´s not null.
            if(!StringHandler.isNullOrEmpty(mStaticValue)) {
                UnitEntityDataType unitEntityDataType = mUnitEntityDataTypeProvider.getUnitEntityDataType(targetWidget);
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
            case SOURCE_UNIT: return getSourceUnit().getFormattedString();/*widgetProvider.getWidgetByItemName(getSourceUnit().getDataSourceId()).getItem().getState()*/
            case STATIC: return getStaticValue();
            case TEXT: return getTextValue();
            default: return null;
        }
    }

    public RuleActionValueType getValueType() {
        if(mSourceUnit != null)
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

    public UnitEntityDataType getSourceUnit() {
        return mSourceUnit;
    }

    public void setSourceOpenHABItemName(String sourceOpenHABItemName) {
        if(mSourceUnit != null && mSourceUnit.getDataSourceId().equalsIgnoreCase(sourceOpenHABItemName))
                return;
        removeSourceUnit();
        if(!StringHandler.isNullOrEmpty(sourceOpenHABItemName)) {
            OpenHABWidget widget = widgetProvider.getWidgetByItemName(sourceOpenHABItemName);
            if(widget != null) {
                mSourceUnit = mUnitEntityDataTypeProvider.getUnitEntityDataType(widgetProvider.getWidgetByItemName(sourceOpenHABItemName));
                widgetProvider.addItemListener(mSourceUnit);
                mStaticValue = mTextValue = null;
            } else
                throw new IllegalArgumentException("Unable to find widget containing item '" + sourceOpenHABItemName + "'");
        }
    }

    private void removeSourceUnit() {
        if(mSourceUnit != null)
            widgetProvider.removeItemListener(mSourceUnit);
        mSourceUnit = null;
    }

    public String getStaticValue() {
        return mStaticValue;
    }

    public void setStaticValue(String staticValue) {
        mStaticValue = staticValue;
        if(mStaticValue != null) {
            mTextValue = null;
            removeSourceUnit();
        }
    }

    public String getTextValue() {
        return mTextValue;
    }

    public void setTextValue(String textValue) {
        mTextValue = textValue;
        if(mTextValue != null) {
            mStaticValue = null;
            removeSourceUnit();
        }
    }

    @Override
    public String toString() {
        //TODO - TA: use resource strings (language independent)
        StringBuilder sb = new StringBuilder();
        if(getActionType() == RuleActionType.COMMAND) {
            sb.append(StringHandler.isNullOrEmpty(mTargetOpenHABItemName) ? "<No target>" : mTargetOpenHABItemName);
            sb.append(" = ");
        } else {
            sb.append("Send message: ");
        }
        switch (getValueType()) {
            case SOURCE_UNIT: sb.append(mSourceUnit.getDataSourceId());
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

