package com.zenit.habclient.rule;

import com.zenit.habclient.OnValueChangedListener;
import com.zenit.habclient.UnitValueChangedListener;
import com.zenit.habclient.util.StringHandler;

/**
 * Created by Tony Alpskog in 2014.
 */
public abstract class UnitEntityDataType<T> extends EntityDataType<T> implements OnValueChangedListener {

    protected UnitValueChangedListener mUnitValueChangedListener;
    protected boolean mIsRegistered = false;

    public UnitEntityDataType() {
        super();
    }

    public UnitEntityDataType(String name, T value) { super(name, value); }

    public UnitEntityDataType(String name, T value, UnitValueChangedListener unitValueChangedListener) {
        super(name, value);
        mUnitValueChangedListener = unitValueChangedListener;
    }

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
    public String getDataSourceId() { return mDataSourceId; }

    @Override
    public void onValueChanged(String sourceID, String value) {
        setValue(valueOf(value));
    }
}
