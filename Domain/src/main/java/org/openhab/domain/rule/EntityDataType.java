package org.openhab.domain.rule;

/**
 * Created by Tony Alpskog in 2014.
 */
public abstract class EntityDataType<T> implements IEntityDataType<T> {
    private String mName;
    private String mDataSourceId;
    private Class<T> mValueType;

    public EntityDataType(Class<T> valueType) {
        mValueType = valueType;
    }

    public EntityDataType(Class<T> valueType, String name) {
        this(valueType);

        mName = name;
    }

    @Override
    public Class<T> getValueType() {
        return mValueType;
    }

    @Override
    public final void setName(String name) {
        mName = name;
    }

    @Override
    public final String getName() {
        return mName;
    }

    @Override
    public String toString()
    {
        return String.format("%s. %s", mName, mDataSourceId);
    }

    @Override
    public void setDataSourceId(String value) { mDataSourceId = value; }

    @Override
    public String getDataSourceId() { return mDataSourceId; }
}
