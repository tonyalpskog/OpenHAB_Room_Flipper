package org.openhab.domain.rule;

/**
 * Created by Tony Alpskog in 2014.
 */
public class UnitEntityDataType<T> extends EntityDataType<T> {
    private T mValue;

    public UnitEntityDataType(T value, Class<T> valueType) {
        super(valueType);

        mValue = value;
    }

    public UnitEntityDataType(String name, T value, Class<T> valueType) {
        super(valueType, name);

        mValue = value;
    }

    public void setValue(T value) {
        mValue = value;
    }

    @Override
    public EntityDataTypeSource getSourceType() {
        return EntityDataTypeSource.UNIT;
    }

    @Override
    public T getValue() {
        return mValue;
    }
}
