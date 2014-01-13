package com.zenit.habclient;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface IUnitEntityDataType<T> {
//    public DataSourceType getSourceType();
    public String getName();
    public Class<?> getDataType();
    public T getValue();
    public void setValue(T value);
    public String getFormattedString();
    public T valueOf(String input);
}
