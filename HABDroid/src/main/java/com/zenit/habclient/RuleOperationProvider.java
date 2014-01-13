package com.zenit.habclient;

import android.util.Log;

import java.util.HashMap;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleOperationProvider {
//    private List<IOperatorValue> mValueTypes;
    public HashMap<Class<?>, HashMap<RuleOperatorType, ?>> mOperatorHash;

    public RuleOperationProvider() {
        mOperatorHash = new HashMap<Class<?>, HashMap<RuleOperatorType, ?>>();
        createLogicOperators();
    }

    private final double EPSILON = 0.000000000000001d;

    private int compareNumbers(Number a, Number b)
    {
        if (a == null)
        {
            return b == null? 0: -1;
        }
        else if (b == null)
        {
            return 1;
        }
        else
        {
            if(Math.abs(a.doubleValue() - b.doubleValue()) < EPSILON)
                return 0;
            else
                return a.doubleValue() > b.doubleValue()? 1: -1;
        }
    }

    private void createLogicOperators() {

        // ===============  Numeric  ================

        HashMap<RuleOperatorType, RuleOperator<Number>> numberOperatorHash = new HashMap<RuleOperatorType, RuleOperator<Number>>();

        RuleOperator<Number> equal = new RuleOperator<Number>(RuleOperatorType.Equal, false) {
            @Override
            public boolean getOperationResult(Number... args) {
                validateArgumentNumber(args);

                return compareNumbers(args[0], args[1]) == 0;
            }
        };
        Log.d("Calc", "compareNumbers(Integer.valueOf(10), Integer.valueOf(10)) = " + (compareNumbers(Integer.valueOf(10), Integer.valueOf(10)) == 0));
        Log.d("Calc", "compareNumbers(Integer.valueOf(13), Integer.valueOf(10)) = " + (compareNumbers(Integer.valueOf(13), Integer.valueOf(10)) == 0));
        Log.d("Calc", "equal.getOperationResult(Integer.valueOf(10), Integer.valueOf(10)) = " + equal.getOperationResult(Integer.valueOf(10), Integer.valueOf(10)));
        Log.d("Calc", "equal.getOperationResult(Integer.valueOf(13), Integer.valueOf(10)) = " + equal.getOperationResult(Integer.valueOf(13), Integer.valueOf(10)));
        numberOperatorHash.put(equal.getType(), equal);

        RuleOperator<Number> notEqual = new RuleOperator<Number>(RuleOperatorType.NotEqual, false) {
            @Override
            public boolean getOperationResult(Number... args) {
                validateArgumentNumber(args);

                return compareNumbers(args[0], args[1]) != 0;
            }
        };
        numberOperatorHash.put(notEqual.getType(), notEqual);

        RuleOperator<Number> lessThan = new RuleOperator<Number>(RuleOperatorType.LessThan, false) {
            @Override
            public boolean getOperationResult(Number... args) {
                validateArgumentNumber(args);

                return compareNumbers(args[0], args[1]) < 0;
            }
        };
        numberOperatorHash.put(lessThan.getType(), lessThan);

        RuleOperator<Number> moreThan = new RuleOperator<Number>(RuleOperatorType.MoreThan, false) {
            @Override
            public boolean getOperationResult(Number... args) {
                validateArgumentNumber(args);

                return compareNumbers(args[0], args[1]) > 0;
            }
        };
        numberOperatorHash.put(moreThan.getType(), moreThan);

        RuleOperator<Number> lessOrEqual = new RuleOperator<Number>(RuleOperatorType.LessOrEqual, false) {
            @Override
            public boolean getOperationResult(Number... args) {
                validateArgumentNumber(args);

                return compareNumbers(args[0], args[1]) <= 0;
            }
        };
        numberOperatorHash.put(lessOrEqual.getType(), lessOrEqual);

        RuleOperator<Number> moreOrEqual = new RuleOperator<Number>(RuleOperatorType.MoreOrEqual, false) {
            @Override
            public boolean getOperationResult(Number... args) {
                validateArgumentNumber(args);

                return compareNumbers(args[0], args[1]) >= 0;
            }
        };
        numberOperatorHash.put(moreOrEqual.getType(), moreOrEqual);

        RuleOperator<Number> between = new RuleOperator<Number>(RuleOperatorType.Between, false) {
            @Override
            public boolean getOperationResult(Number... args) {
                validateArgumentNumber(args);

                return (compareNumbers(args[0], args[1]) > 0 && compareNumbers(args[0], args[2]) < 0);
            }
        };
        numberOperatorHash.put(between.getType(), between);

        RuleOperator<Number> within = new RuleOperator<Number>(RuleOperatorType.Within, false) {
            @Override
            public boolean getOperationResult(Number... args) {
                validateArgumentNumber(args);

                return (compareNumbers(args[0], args[1]) >= 0 && compareNumbers(args[0], args[2]) <= 0);
            }
        };
        numberOperatorHash.put(within.getType(), within);
        mOperatorHash.put(Number.class, numberOperatorHash);


        // ===============  Boolean  ================

        HashMap<RuleOperatorType, RuleOperator<Boolean>> booleanOperatorHash = new HashMap<RuleOperatorType, RuleOperator<Boolean>>();

        RuleOperator<Boolean> orOperator = new RuleOperator<Boolean>(RuleOperatorType.Or, false) {
            @Override
            public boolean getOperationResult(Boolean... args) {
                validateArgumentNumber(args);

                return args[0].booleanValue() || args[1].booleanValue();
            }
        };
        booleanOperatorHash.put(orOperator.getType(), orOperator);

        RuleOperator<Boolean> andOperator = new RuleOperator<Boolean>(RuleOperatorType.And, true) {
            @Override
            public boolean getOperationResult(Boolean... args) {
                validateArgumentNumber(args);

                int index = 0;
                boolean result = true;

                while (result) {
                    result = result && args[index++].booleanValue();
                }

                return result;
            }
        };
        booleanOperatorHash.put(andOperator.getType(), andOperator);

        mOperatorHash.put(Boolean.class, booleanOperatorHash);


//        NumberPicker joo;
//        joo.
//
//        Number percentage;
//
////        NumberFormat.Field.PERCENT
//
//        Date date = new Date();
//
    }
}
