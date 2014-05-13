package com.zenit.habclient.rule;

import com.zenit.habclient.OnOperandValueChangedListener;

/**
 * Created by Tony Alpskog in 2014.
 */
public abstract class EntityDataType<T> implements IEntityDataType<T> {

    protected String mName;
    protected T mValue;
    protected String mDataSourceId;
    protected OnOperandValueChangedListener mOnOperandValueChangedListener;


    public EntityDataType() {
        this(null, null);
    }

    public EntityDataType(String name, T value) {
        this.mName = name;
        this.mValue = value;
    }

    public void setName(String name) {
        mName = name;
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
    public String toString() {
        return mName != null? mName : getFormattedString();
    }

    @Override
    public abstract T valueOf(String input);

    @Override
    public RuleTreeItem getRuleTreeItem(int treeIndex) {
        return null;
    }

    public void setOnOperandValueChangedListener(OnOperandValueChangedListener onOperandValueChangedListener) {
        mOnOperandValueChangedListener = onOperandValueChangedListener;
    }
}
