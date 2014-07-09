package org.openhab.domain.rule.operators;

import org.openhab.domain.rule.IEntityDataType;
import org.openhab.domain.rule.IOperator;
import org.openhab.domain.rule.RuleOperatorType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public abstract class RuleOperator<T> implements IOperator<T> {
    public static final String MISSING_OPERATOR = "<Missing operator>";//TODO - TA:Language

    protected boolean mSupportsMultipleOperations;
    protected RuleOperatorType mType;
//    private Class<?> mOperatorValueType;

    public RuleOperator(RuleOperatorType type, boolean supportsMultipleOperations) {
        mType = type;
        mSupportsMultipleOperations = supportsMultipleOperations;
    }

    @Override
    public boolean supportsMultipleOperations() {
        return mSupportsMultipleOperations;
    }

    @Override
    public String getName() {
        return mType.getName();
    }

    @Override
    public boolean getOperationResult(T... args) {
        validateArgumentNumber(args);

        List<T> argsList = new ArrayList<T>();

        for (T arg : args) {
            if (arg == null)
                return false;//Unfinished operation initialization will result in FALSE as operation result.
            argsList.add(arg);
        }

        return getOperationResult2(argsList);
    }

    @Override
    public boolean getOperationResult(List<IEntityDataType> operands) {
        List<T> operandList = new ArrayList<T>(operands.size());
        for (IEntityDataType operand : operands) {
            if(operand == null)
                return false;//Unfinished operation initialization will result in FALSE as operation result.

            operandList.add((T) operand.getValue());
        }

        return getOperationResult2(operandList);
    }

    public RuleOperatorType getType() {
        return mType;
    }

    protected void validateArgumentNumber(T... args) throws IllegalArgumentException {
        validateArgumentNumber(args.length);
    }

    protected void validateArgumentNumber(List<T> args) throws IllegalArgumentException {
        validateArgumentNumber(args.size());
    }

    protected void validateArgumentNumber(int numberOfArguments) throws IllegalArgumentException {
        //TODO - This is all trash, add usage of numberOfSupportedOperationValues
        if(numberOfArguments < mType.getMinimumNumberOfSupportedOperationArgs())
            throw new IllegalArgumentException("LogicOperator: " + numberOfArguments + " arguments passed when minimum supported number of arguments is " + mType.getMinimumNumberOfSupportedOperationArgs());

        if(numberOfArguments > mType.getMaximumNumberOfSupportedOperationArgs()) {
            throw new IllegalArgumentException(numberOfArguments + " arguments passed when only " + mType.getMaximumNumberOfSupportedOperationArgs() + " arguments are supported!");
        }
    }
}
