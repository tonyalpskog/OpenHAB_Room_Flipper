package com.zenit.habclient.rule;

/**
 * Created by Tony Alpskog in 2014.
 */
public abstract class UnitEntityDataType<T> extends EntityDataType<T> implements IValueEntityDataType<T> {

    //String mStringFormat;
    //BUSTypeAddress mBusAddress;


    public UnitEntityDataType() {
        super();
    }

    public UnitEntityDataType(String name, T value) {
        super(name, value);
    }

    protected String mName;
    protected T mValue;

    @Override
    public EntityDataTypeSource getSourceType() {
        return EntityDataTypeSource.UNIT;
    }

    @Override
    public void setValue(T value) {
        mValue = value;
    }
}
