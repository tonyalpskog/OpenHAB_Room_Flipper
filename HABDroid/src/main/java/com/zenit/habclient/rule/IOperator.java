package com.zenit.habclient.rule;

import java.text.ParseException;
import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface IOperator<T> {
    public boolean getOperationResult2(List<T> args);
    public boolean getOperationResult(T... args);
    public boolean getOperationResult(List<IUnitEntityDataType> operands);
    public boolean supportsMultipleOperations();
    public String getName();
    public T parseValue(String valueAsString) throws ParseException;
}