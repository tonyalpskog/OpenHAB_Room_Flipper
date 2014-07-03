package org.openhab.domain.rule;

import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.util.StringHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Tony Alpskog in 2014.
 */
public abstract class UnitEntityDataType<T> extends EntityDataType<T> implements OnValueChangedListener {

    protected UnitValueChangedListener mUnitValueChangedListener;
    protected boolean mIsRegistered = false;

    public UnitEntityDataType() {
        super();
    }

    public UnitEntityDataType(String name, T value) {
        super(name, value);
        setDataSourceId(name);
    }

    public UnitEntityDataType(String name, T value, UnitValueChangedListener unitValueChangedListener) {
        super(name, value);
        mUnitValueChangedListener = unitValueChangedListener;
    }

    public abstract Map<String, T> getStaticValues();

    @Override
    public void setDataSourceId(String dataSourceId) {
        if(mIsRegistered && mUnitValueChangedListener != null)
            mUnitValueChangedListener.unregisterOnValueChangedListener(this);

        mDataSourceId = dataSourceId;
        if(StringHandler.isNullOrEmpty(mDataSourceId)) {
            mIsRegistered = false;
            return;
        }

        if(mUnitValueChangedListener != null) {
            mUnitValueChangedListener.registerOnValueChangedListener(this, mDataSourceId);
            mIsRegistered = true;
        }
    }

    @Override
    public EntityDataTypeSource getSourceType() {
        return EntityDataTypeSource.UNIT;
    }

    public void setValue(T value) {
        boolean changed = mValue != null? !mValue.equals(value) : value != null;
        mValue = value;
        if(changed && mOnOperandValueChangedListener != null)
            mOnOperandValueChangedListener.onOperandValueChanged(this);
    }

    @Override
    public void onValueChanged(String sourceID, String value) {
        setValue(valueOf(value));
    }

    public static UnitEntityDataType getUnitEntityDataType(OpenHABWidget openHABWidget) {
        return getEntityDataType(openHABWidget, null, EntityDataTypeSource.UNIT);
    }

    public static UnitEntityDataType getStaticEntityDataType(OpenHABWidget openHABWidget, String staticValue) {
        return getEntityDataType(openHABWidget, staticValue, EntityDataTypeSource.STATIC);
    }

    private static UnitEntityDataType getEntityDataType(OpenHABWidget openHABWidget, String staticValue, final EntityDataTypeSource sourceType) {
        UnitEntityDataType unitEntityDataType = null;
        final boolean isUnitType = sourceType == EntityDataTypeSource.UNIT;

        //TODO - TA: replace static values with constants (ON, OFF, OPEN, CLOSED, Undefined)
        switch(openHABWidget.getItem().getType()) {
            case Switch:
                Boolean aBoolean;

                if((isUnitType && openHABWidget.getItem().getState().equalsIgnoreCase("Undefined")) ||
                        (!isUnitType && (staticValue == null || staticValue.equalsIgnoreCase("Undefined"))))
                    aBoolean = null;
                else
                    aBoolean = isUnitType? openHABWidget.getItem().getState().equalsIgnoreCase("ON") : staticValue.equalsIgnoreCase("ON");

                unitEntityDataType = new UnitEntityDataType<Boolean>(isUnitType? openHABWidget.getItem().getName() : UUID.randomUUID().toString(), aBoolean)
                {
                    public String getFormattedString(){
                        return mValue.booleanValue()? "ON": "OFF";
                    }

                    @Override
                    public Boolean valueOf(String input) {
                        if(input.equalsIgnoreCase("ON"))
                                return Boolean.valueOf(true);

                        if(input.equalsIgnoreCase("OFF"))
                                return Boolean.valueOf(false);

                        return null;
                    }

                    @Override
                    public Map<String, Boolean> getStaticValues() {
                        Map<String, Boolean> nameValueMap = new HashMap<String, Boolean>(2);
                        nameValueMap.put("OFF", Boolean.FALSE);
                        nameValueMap.put("ON", Boolean.TRUE);
                        return nameValueMap;
                    }

                    @Override
                    public String toString() {
                        if(isUnitType)
                            return super.toString();
                        return getFormattedString();
                    }

                    @Override
                    public EntityDataTypeSource getSourceType() {
                        return sourceType;
                    }
                };
                break;

            case Contact:
                if((isUnitType && openHABWidget.getItem().getState().equalsIgnoreCase("Undefined"))
                        || (!isUnitType && (staticValue == null || staticValue.equalsIgnoreCase("Undefined"))))
                    aBoolean = null;
                else
                    aBoolean = isUnitType? openHABWidget.getItem().getState().equalsIgnoreCase("CLOSED") : staticValue.equalsIgnoreCase("CLOSED");

                unitEntityDataType = new UnitEntityDataType<Boolean>(isUnitType? openHABWidget.getItem().getName() : UUID.randomUUID().toString(), aBoolean)
                {
                    public String getFormattedString(){
                        return mValue.booleanValue()? "CLOSED": "OPEN";
                    }

                    @Override
                    public Boolean valueOf(String input) {
                        if(input.equalsIgnoreCase("CLOSED"))
                            return Boolean.valueOf(true);

                        if(input.equalsIgnoreCase("OPEN"))
                            return Boolean.valueOf(false);

                        return null;
                    }

                    @Override
                    public Map<String, Boolean> getStaticValues() {
                        Map<String, Boolean> nameValueMap = new HashMap<String, Boolean>(2);
                        nameValueMap.put("OPEN", Boolean.FALSE);
                        nameValueMap.put("CLOSED", Boolean.TRUE);
                        return nameValueMap;
                    }

                    @Override
                    public String toString() {
                        if(isUnitType)
                            return super.toString();
                        return getFormattedString();
                    }

                    @Override
                    public EntityDataTypeSource getSourceType() {
                        return sourceType;
                    }
                };
                break;

            case Rollershutter:
            case Dimmer:
            case Number:
                Double aNumber;
                if((isUnitType && openHABWidget.getItem().getState().equalsIgnoreCase("Undefined"))
                        || (!isUnitType && (staticValue == null || staticValue.equalsIgnoreCase("Undefined"))))
                    aNumber = null;
                else
                    aNumber = isUnitType? Double.valueOf(openHABWidget.getItem().getState()) : Double.valueOf(staticValue);

                unitEntityDataType = new UnitEntityDataType<Double>(isUnitType? openHABWidget.getItem().getName() : UUID.randomUUID().toString(), aNumber)
                {
                    public String getFormattedString(){
                        return mValue.toString();
                    }

                    @Override
                    public Double valueOf(String input) {
                        return Double.valueOf(input);
                    }

                    @Override
                    public Map<String, Double> getStaticValues() {
                        return null;
                    }

                    @Override
                    public String toString() {
                        if(isUnitType)
                            return super.toString();
                        return getFormattedString();
                    }

                    @Override
                    public EntityDataTypeSource getSourceType() {
                        return sourceType;
                    }
                };
                break;
        }

        return unitEntityDataType;
    }
}
