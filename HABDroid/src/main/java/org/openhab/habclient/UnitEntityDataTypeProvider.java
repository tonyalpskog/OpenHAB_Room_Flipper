package org.openhab.habclient;

import org.openhab.habclient.rule.UnitEntityDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Tony Alpskog in 2014.
 */
public class UnitEntityDataTypeProvider {
    List<UnitEntityDataType> mUnitDataTypeList;

    public List<UnitEntityDataType> getUnitDataTypeList() {
        if(mUnitDataTypeList == null)
            createUnitDataTypes();
        return mUnitDataTypeList;
    }

    private void createUnitDataTypes() {
        mUnitDataTypeList = new ArrayList<UnitEntityDataType>();

        mUnitDataTypeList.add(new UnitEntityDataType<Boolean>("Switch", false)
        {
            public String getFormattedString(){
                return mValue.booleanValue()? "On": "Off";//TODO - Language independent
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

        mUnitDataTypeList.add(new UnitEntityDataType<Integer>("Dimmer percentage", 75)
        {
            public String getFormattedString(){
                return mValue.toString() + "%";
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

        mUnitDataTypeList.add(new UnitEntityDataType<Double>("Humidity percentage", 50.7)
        {
            public String getFormattedString(){
                return mValue.toString() + "%Rh";
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

        mUnitDataTypeList.add(new UnitEntityDataType<Double>("Temperature", 8.4)
        {
            public String getFormattedString(){
                return mValue.toString() + "°C";//TODO - Fahrenheit, (Kelvin)
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
    }
}
