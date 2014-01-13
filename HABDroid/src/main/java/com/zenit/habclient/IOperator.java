package com.zenit.habclient;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface IOperator<T> {
    public boolean getOperationResult(T... args);
    public boolean supportsMultipleOperations();
    public String getName();
}
