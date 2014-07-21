package org.openhab.domain.rule;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface IEntityDataType<T> {
    EntityDataTypeSource getSourceType();
    String getName();
    void setName(String name);

    Class<T> getValueType();
    T getValue();

    void setDataSourceId(String value);
    String getDataSourceId();
}
