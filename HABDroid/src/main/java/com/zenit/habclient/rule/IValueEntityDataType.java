package com.zenit.habclient.rule;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface IValueEntityDataType<T> extends IEntityDataType<T>{
    public void setValue(T value);
}
