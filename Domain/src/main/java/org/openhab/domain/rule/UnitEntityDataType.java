package org.openhab.domain.rule;

import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.util.StringHandler;

import java.util.HashMap;
import java.util.Map;

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
        boolean changed = !mValue.equals(value);
        mValue = value;
        if(changed && mOnOperandValueChangedListener != null)
            mOnOperandValueChangedListener.onOperandValueChanged(this);
    }

    @Override
    public void onValueChanged(String sourceID, String value) {
        setValue(valueOf(value));
    }

    public static UnitEntityDataType getUnitEntityDataType(OpenHABWidget openHABWidget) {
        UnitEntityDataType unitEntityDataType = null;

        switch(openHABWidget.getItem().getType()) {
            case Switch:
                Boolean aBoolean;
                if(openHABWidget.getItem().getState().equalsIgnoreCase("Undefined"))
                    aBoolean = null;
                else
                    aBoolean = openHABWidget.getItem().getState().equalsIgnoreCase("ON");

                unitEntityDataType = new UnitEntityDataType<Boolean>(openHABWidget.getItem().getName(), aBoolean)
                {
                    public String getFormattedString(){
                        return mValue.booleanValue()? "ON": "OFF";//TODO - TA: Translate value
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
                };
                break;

            case Contact:
                if(openHABWidget.getItem().getState().equalsIgnoreCase("Undefined"))
                    aBoolean = null;
                else
                    aBoolean = openHABWidget.getItem().getState().equalsIgnoreCase("CLOSED");

                unitEntityDataType = new UnitEntityDataType<Boolean>(openHABWidget.getItem().getName(), aBoolean)
                {
                    public String getFormattedString(){
                        return mValue.booleanValue()? "CLOSED": "OPEN";//TODO - TA: Translate value
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
                };
                break;

            case Rollershutter:
            case Dimmer:
            case Number:
                Double aNumber;
                if(openHABWidget.getItem().getState().equalsIgnoreCase("Undefined"))
                    aNumber = null;
                else
                    aNumber = Double.valueOf(openHABWidget.getItem().getState());

                unitEntityDataType = new UnitEntityDataType<Double>(openHABWidget.getItem().getName(), aNumber)
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
                };
                break;
        }

        return unitEntityDataType;
    }
}
