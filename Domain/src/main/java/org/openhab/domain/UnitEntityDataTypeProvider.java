package org.openhab.domain;

import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.rule.EntityDataTypeSource;
import org.openhab.domain.rule.UnitEntityDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2014.
 */
public class UnitEntityDataTypeProvider implements IUnitEntityDataTypeProvider {
    List<UnitEntityDataType<?>> mUnitDataTypeList;//Only for unit test

    @Inject
    public UnitEntityDataTypeProvider() {

    }

    @Override
    public UnitEntityDataType getEntityDataType(OpenHABWidget openHABWidget, String staticValue, final EntityDataTypeSource sourceType) {
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
                        return getValue() ? "ON": "OFF";
                    }

                    @Override
                    public Boolean valueOf(String input) {
                        if(input.equalsIgnoreCase("ON"))
                                return true;

                        if(input.equalsIgnoreCase("OFF"))
                                return false;

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
                        return getValue() ? "CLOSED": "OPEN";
                    }

                    @Override
                    public Boolean valueOf(String input) {
                        if(input.equalsIgnoreCase("CLOSED"))
                            return true;

                        if(input.equalsIgnoreCase("OPEN"))
                            return false;

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
                        return getValue().toString();
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

    @Override
    public UnitEntityDataType getUnitEntityDataType(OpenHABWidget openHABWidget) {
        return getEntityDataType(openHABWidget, null, EntityDataTypeSource.UNIT);
    }

    @Override
    public UnitEntityDataType getStaticEntityDataType(OpenHABWidget openHABWidget, String staticValue) {
        return getEntityDataType(openHABWidget, staticValue, EntityDataTypeSource.STATIC);
    }

    @Override
    public List<UnitEntityDataType<?>> getUnitDataTypeList() {
        if(mUnitDataTypeList == null)
            mUnitDataTypeList = createUnitDataTypes();

        return mUnitDataTypeList;
    }

    private List<UnitEntityDataType<?>> createUnitDataTypes() {
        List<UnitEntityDataType<?>> dataTypeList = new ArrayList<UnitEntityDataType<?>>();

        dataTypeList.add(new UnitEntityDataType<Boolean>("Switch", false)
        {
            public String getFormattedString(){
                return getValue() ? "On": "Off";//TODO - Language independent
            }

            @Override
            public Boolean valueOf(String input) {
                return Boolean.valueOf(input);
            }

            @Override
            public Map<String, Boolean> getStaticValues() {
                return null;
            }
        });

        dataTypeList.add(new UnitEntityDataType<Integer>("Dimmer percentage", 75)
        {
            public String getFormattedString(){
                return getValue().toString() + "%";
            }

            @Override
            public Integer valueOf(String input) {
                return Integer.valueOf(input);
            }

            @Override
            public Map<String, Integer> getStaticValues() {
                return null;
            }
        });

        dataTypeList.add(new UnitEntityDataType<Double>("Humidity percentage", 50.7)
        {
            public String getFormattedString(){
                return getValue().toString() + "%Rh";
            }

            @Override
            public Double valueOf(String input) {
                return Double.valueOf(input);
            }

            @Override
            public Map<String, Double> getStaticValues() {
                return null;
            }
        });

        dataTypeList.add(new UnitEntityDataType<Double>("Temperature", 8.4)
        {
            public String getFormattedString(){
                return getValue().toString() + "°C";//TODO - Fahrenheit, (Kelvin)
            }

            @Override
            public Double valueOf(String input) {
                return Double.valueOf(input);
            }

            @Override
            public Map<String, Double> getStaticValues() {
                return null;
            }
        });

        return dataTypeList;
    }

}
