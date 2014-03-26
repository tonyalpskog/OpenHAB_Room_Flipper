package com.zenit.habclient.rule;

import android.util.Log;

import com.zenit.habclient.DataSourceType;
import com.zenit.habclient.UnitEntityDataType;

/**
 * Created by Tony Alpskog in 2014.
 */
public abstract class RuleUnitEntity<T> extends UnitEntityDataType<T> {

    private RuleOperation mOperation;

    public RuleUnitEntity(String name) {
        super(name);
    }

    public RuleUnitEntity(String name, T value) {
        super(name, value);
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
    public DataSourceType getSourceType() {
        return DataSourceType.RULE;
    }

    @Override
    public Class<?> getDataType() {
        return mValue.getClass();
    }

    @Override
    public T getValue() { return mValue; }

    @Override
    public void setValue(T value) {
        mValue = value;
    }

    public RuleOperation getOperation() {
        return mOperation;
    }

    public void setOperation(RuleOperation operation) {
        this.mOperation = operation;
    }
}
