package com.zenit.habclient;

import com.zenit.habclient.rule.IUnitEntityDataType;

/**
 * Created by Tony Alpskog in 2014.
 */
public abstract class UnitEntityDataType<T> implements IUnitEntityDataType<T> {

    //String mStringFormat;
    //BUSTypeAddress mBusAddress;

    protected String mName;
    protected T mValue;

    public UnitEntityDataType(String name) {
        this(name, null);
    }

    public UnitEntityDataType(String name, T value) {
        this.mName = name;
        this.mValue = value;
    }

    public void setName(String name) {
        mName = name;
    }

    public DataSourceType getSourceType() {
        return DataSourceType.UNIT;
    }

    @Override
    public String getFormattedString(){
        return mValue.toString();
    }
//
//    public boolean isReadable() {
//        return mIsReadable;
//    }
//
//    public boolean isWritable() {
//        return mIsWritable;
//    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public Class<?> getDataType() {
        return mValue.getClass();
    }

    @Override
    public T getValue() {
        return mValue;
    }

    @Override
    public void setValue(T value) {
        mValue = value;
    }

    @Override
    public String toString() {
        return getFormattedString();
    }

    @Override
    public abstract T valueOf(String input);
}
