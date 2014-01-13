package com.zenit.habclient;

import java.lang.reflect.ParameterizedType;
import java.text.ParseException;

/**
 * Created by Tony Alpskog in 2014.
 */
public abstract class RuleOperator<T> implements IOperator<T> {

    private boolean mSupportsMultipleOperations;
    private RuleOperatorType mType;
//    private Class<?> mOperatorValueType;

    public RuleOperator(RuleOperatorType type, Boolean supportsMultipleOperations) {
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

    public RuleOperatorType getType() {
        return mType;
    }

    T getInstanceOfT()
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

    public T parseValue(String valueAsString) throws ParseException, InstantiationException {
        T type = getInstanceOfT();
        if(type.getClass().getComponentType() == Integer.TYPE)
                return ((Class<T>)type.getClass()).cast(Integer.valueOf(valueAsString));
        if(type.getClass().getComponentType() == Long.TYPE)
                return ((Class<T>)type.getClass()).cast(Long.valueOf(valueAsString));
        if(type.getClass().getComponentType() == Double.TYPE)
                return ((Class<T>)type.getClass()).cast(Double.valueOf(valueAsString));
        if(type.getClass().getComponentType() == Float.TYPE)
                return ((Class<T>)type.getClass()).cast(Float.valueOf(valueAsString));
        if(type.getClass().getComponentType() == Boolean.TYPE)
                return ((Class<T>)type.getClass()).cast(Boolean.valueOf(valueAsString));

        throw new ParseException("Cannot parse '" + valueAsString + "' to type '" + type.getClass().getComponentType().toString() + "'", 0);
    }

    protected void validateArgumentNumber(T... args) throws IllegalArgumentException {
        //TODO - This is all trash, add usage of numberOfSupportedOperationValues
        if(args.length < mType.getMinimumNumberOfSupportedOperationArgs())
            throw new IllegalArgumentException("LogicOperator: " + args.length + " arguments passed when minimum supported number of arguments is " + mType.getMinimumNumberOfSupportedOperationArgs());

        if(args.length > mType.getMaximumNumberOfSupportedOperationArgs()) {
            throw new IllegalArgumentException(args.length + " arguments passed when only " + mType.getMaximumNumberOfSupportedOperationArgs() + " arguments are supported!");
        }
//
//        for(int i = 0; i < args.length; i++) {
//            if(args[i].getValue().getClass() != getOperatorValueType().getType())
//                throw new IllegalArgumentException("Wrong argument type for argument index " + i + ". Got " + args[i].getValue().getClass()  + ", expected " + getOperatorValueType().getType());
//        }
    }
}
