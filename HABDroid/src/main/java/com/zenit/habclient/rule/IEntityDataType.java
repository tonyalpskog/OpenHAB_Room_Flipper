package com.zenit.habclient.rule;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface IEntityDataType<T> {
    public EntityDataTypeSource getSourceType();
    public String getName();
    public Class<?> getDataType();
    public T getValue();
    public String getFormattedString();
    public T valueOf(String input);
}
