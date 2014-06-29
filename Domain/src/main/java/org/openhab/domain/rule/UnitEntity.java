package org.openhab.domain.rule;

/**
 * Created by Tony Alpskog in 2014.
 */
public class UnitEntity {

    private boolean mIsReadable;
    private boolean mIsWritable;
    private UnitEntityDataType<?> mDataType;

    public void setReadable(boolean status) {
        mIsReadable = status;
    }

    public void setWritable(boolean status) {
        mIsWritable = status;
    }

    public UnitEntityDataType<?> getDataType() {
        return mDataType;
    }
}
