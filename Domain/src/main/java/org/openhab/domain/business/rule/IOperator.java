package org.openhab.domain.business.rule;

import java.text.ParseException;
import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface IOperator<T> {
    public boolean getOperationResult2(List<T> args);
    public boolean getOperationResult(T... args);
    public boolean getOperationResult(List<IEntityDataType> operands);
    public boolean supportsMultipleOperations();
    public String getName();
    public T parseValue(String valueAsString) throws ParseException;
}
