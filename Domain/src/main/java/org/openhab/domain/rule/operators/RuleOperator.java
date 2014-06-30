package org.openhab.domain.rule.operators;

import org.openhab.domain.rule.IEntityDataType;
import org.openhab.domain.rule.IOperator;
import org.openhab.domain.rule.RuleOperatorType;

import java.lang.reflect.ParameterizedType;
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

    protected <T> T convertInstanceOfObject(Object o, Class<T> clazz) {
        try {
            return clazz.cast(o);
        } catch(ClassCastException e) {
            return null;
        }
    }

    public RuleOperatorType getType() {
        return mType;
    }

    public T getInstanceOfT()
    {
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        Class<T> type = (Class<T>) superClass.getActualTypeArguments()[0];
        try
        {
            return type.newInstance();
        }
        catch (Exception e)
        {
            // Oops, no default constructor
            throw new RuntimeException(e);
        }
    }

//    public T parseValue(String valueAsString) throws ParseException {
////        T type = getInstanceOfT();
////        return ((Class<T>)type.getClass()).cast(Float.valueOf(valueAsString));
////
////        T type = getInstanceOfT();
////        if(type.getClass().getComponentType() == Integer.TYPE)
////                return ((Class<T>)type.getClass()).cast(Integer.valueOf(valueAsString));
////        if(type.getClass().getComponentType() == Long.TYPE)
////                return ((Class<T>)type.getClass()).cast(Long.valueOf(valueAsString));
////        if(type.getClass().getComponentType() == Double.TYPE)
////                return ((Class<T>)type.getClass()).cast(Double.valueOf(valueAsString));
////        if(type.getClass().getComponentType() == Float.TYPE)
////                return ((Class<T>)type.getClass()).cast(Float.valueOf(valueAsString));
////        if(type.getClass().getComponentType().equals(Number.class))
////            return (T) Float.valueOf(valueAsString);
////        if(type.getClass().getComponentType() == Boolean.TYPE)
////                return ((Class<T>)type.getClass()).cast(Boolean.valueOf(valueAsString));
////
////        throw new ParseException("Cannot parse '" + valueAsString + "' to type '" + type.getClass().getComponentType().toString() + "'", 0);
//        return getInstanceOfT();
//    }

    protected void validateArgumentNumber(T... args) throws IllegalArgumentException {
        validateArgumentNumber(args.length);
//
//        for(int i = 0; i < args.length; i++) {
//            if(args[i].getValue().getClass() != getOperatorValueType().getType())
//                throw new IllegalArgumentException("Wrong argument type for argument index " + i + ". Got " + args[i].getValue().getClass()  + ", expected " + getOperatorValueType().getType());
//        }
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
