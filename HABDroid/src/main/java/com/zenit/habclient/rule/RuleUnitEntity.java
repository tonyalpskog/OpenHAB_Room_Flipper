package com.zenit.habclient.rule;

import android.util.Log;

import com.zenit.habclient.DataSourceType;
import com.zenit.habclient.UnitEntityDataType;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleUnitEntity extends UnitEntityDataType<Boolean> {

    protected RuleOperation mOperation;

    public RuleUnitEntity(String name) {
        super(name);
    }

    @Override
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        Log.d("RuleUnitEntity.class", "setName(" + name + ")");
        mName = name;
    }

    @Override
    public String getFormattedString(){
        return getValue()? "Sant": "Falskt";//TODO - Language independent
    }

    @Override
    public DataSourceType getSourceType() {
        return DataSourceType.RULE;
    }

    @Override
    public Class<Boolean> getDataType() {
        return Boolean.class;
    }

    @Override
    public Boolean valueOf(String input) {
        return Boolean.valueOf(input);
    }

    @Override
    public Boolean getValue() {
        Boolean currentValue = Boolean.valueOf(getOperation().getResult());
        if(mValue != currentValue) {
            setValue(currentValue);
            //TODO - Send broadcast intent
        }

        return mValue;
    }

    public RuleOperation getOperation() {
        return mOperation;
    }

    public void setOperation(RuleOperation operation) {
        this.mOperation = operation;
    }
}
