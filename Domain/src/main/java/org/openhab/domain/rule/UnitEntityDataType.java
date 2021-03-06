package org.openhab.domain.rule;

import org.openhab.domain.UnitEntityDataTypeProvider;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.util.StringHandler;

import java.util.Map;

/**
 * Created by Tony Alpskog in 2014.
 */
public abstract class UnitEntityDataType<T> extends EntityDataType<T> implements OnValueChangedListener {

    protected UnitValueChangedListener mUnitValueChangedListener;
    protected boolean mIsRegistered = false;
    private boolean mLatestSetWasAValueChange;

    public UnitEntityDataType(String name, T value) {
        super(name, value);
        setDataSourceId(name);
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
        setValue(value, true);
    }

    public void setValue(T value, boolean recalculateOnValueChanged) {
        mLatestSetWasAValueChange = mValue != null? !mValue.equals(value) : value != null;
        mValue = value;
        if(recalculateOnValueChanged)
            resumeOnValueChangedEvent();
    }

    /**
     *
     * @return true if onOperandValueChanged was invoked.
     */
    public boolean resumeOnValueChangedEvent() {
        if(mLatestSetWasAValueChange && mOnOperandValueChangedListener != null) {
            mOnOperandValueChangedListener.onOperandValueChanged(this);
            mLatestSetWasAValueChange = false;//Consumed
            return true;
        }
        return false;
    }

    @Override
    public void onValueChanged(String sourceID, String value) {
        setValue(valueOf(value));
    }

}
