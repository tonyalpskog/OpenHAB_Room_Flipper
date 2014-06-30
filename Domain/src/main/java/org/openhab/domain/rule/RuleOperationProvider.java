package org.openhab.domain.rule;

import org.openhab.domain.model.OpenHABWidget;

import java.util.HashMap;
import java.util.List;

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

    private final double EPSILON = 8E-7f;

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

        RuleOperator equalNum = new NumberRuleOperator<Number>(RuleOperatorType.Equal, false) {
            @Override
            public boolean getOperationResult2(List<Number> args) {
                validateArgumentNumber(args);

                return compareNumbers(args.get(0), args.get(1)) == 0;
            }
        };
        numberOperatorHash.put(equalNum.getType(), equalNum);

        RuleOperator notEqualNum = new NumberRuleOperator<Number>(RuleOperatorType.NotEqual, false) {
            @Override
            public boolean getOperationResult2(List<Number> args) {
                validateArgumentNumber(args);

                return compareNumbers(args.get(0), args.get(1)) != 0;
            }
        };
        numberOperatorHash.put(notEqualNum.getType(), notEqualNum);

        RuleOperator lessThan = new NumberRuleOperator<Number>(RuleOperatorType.LessThan, false) {
            @Override
            public boolean getOperationResult2(List<Number> args) {
                validateArgumentNumber(args);

                return compareNumbers(args.get(0), args.get(1)) < 0;
            }
        };
        numberOperatorHash.put(lessThan.getType(), lessThan);

        RuleOperator moreThan = new NumberRuleOperator<Number>(RuleOperatorType.MoreThan, false) {
            @Override
            public boolean getOperationResult2(List<Number> args) {
                validateArgumentNumber(args);

                return compareNumbers(args.get(0), args.get(1)) > 0;
            }
        };
        numberOperatorHash.put(moreThan.getType(), moreThan);

        RuleOperator lessOrEqual = new NumberRuleOperator<Number>(RuleOperatorType.LessOrEqual, false) {
            @Override
            public boolean getOperationResult2(List<Number> args) {
                validateArgumentNumber(args);

                return compareNumbers(args.get(0), args.get(1)) <= 0;
            }
        };
        numberOperatorHash.put(lessOrEqual.getType(), lessOrEqual);

        RuleOperator moreOrEqual = new NumberRuleOperator<Number>(RuleOperatorType.MoreOrEqual, false) {
            @Override
            public boolean getOperationResult2(List<Number> args) {
                validateArgumentNumber(args);

                return compareNumbers(args.get(0), args.get(1)) >= 0;
            }
        };
        numberOperatorHash.put(moreOrEqual.getType(), moreOrEqual);

        RuleOperator between = new NumberRuleOperator<Number>(RuleOperatorType.Between, false) {
            @Override
            public boolean getOperationResult2(List<Number> args) {
                validateArgumentNumber(args);

                return (compareNumbers(args.get(0), args.get(1)) > 0 && compareNumbers(args.get(0), args.get(2)) < 0);
            }
        };
        numberOperatorHash.put(between.getType(), between);

        RuleOperator within = new NumberRuleOperator<Number>(RuleOperatorType.Within, false) {
            @Override
            public boolean getOperationResult2(List<Number> args) {
                validateArgumentNumber(args);

                return (compareNumbers(args.get(0), args.get(1)) >= 0 && compareNumbers(args.get(0), args.get(2)) <= 0);
            }
        };
        numberOperatorHash.put(within.getType(), within);
        mOperatorHash.put(Number.class, numberOperatorHash);


        // ===============  Boolean  ================

        HashMap<RuleOperatorType, RuleOperator<Boolean>> booleanOperatorHash = new HashMap<RuleOperatorType, RuleOperator<Boolean>>();

        RuleOperator<Boolean> orOperator = new BooleanRuleOperator<Boolean>(RuleOperatorType.Or, false) {
            @Override
            public boolean getOperationResult2(List<Boolean> args) {
                validateArgumentNumber(args);

                int index = 0;
                boolean result = false;

                while (!result && index < args.size()) {
                    result = result || args.get(index++).booleanValue();
                }

                return result;
            }
        };
        booleanOperatorHash.put(orOperator.getType(), orOperator);

        RuleOperator<Boolean> andOperator = new BooleanRuleOperator<Boolean>(RuleOperatorType.And, true) {
            @Override
            public boolean getOperationResult2(List<Boolean> args) {
                validateArgumentNumber(args);

                int index = 0;
                boolean result = true;

                while (result && index < args.size()) {
                    result = result && args.get(index++).booleanValue();
                }

                return result;
            }
        };
        booleanOperatorHash.put(andOperator.getType(), andOperator);

        RuleOperator<Boolean> equalBool = new BooleanRuleOperator<Boolean>(RuleOperatorType.Equal, false) {
            @Override
            public boolean getOperationResult2(List<Boolean> args) {
                validateArgumentNumber(args);

                return args.get(0) == args.get(1);
            }
        };
        booleanOperatorHash.put(equalBool.getType(), equalBool);

        RuleOperator<Boolean> notEqualBool = new BooleanRuleOperator<Boolean>(RuleOperatorType.NotEqual, false) {
            @Override
            public boolean getOperationResult2(List<Boolean> args) {
                validateArgumentNumber(args);

                return args.get(0) != args.get(1);
            }
        };
        booleanOperatorHash.put(notEqualBool.getType(), notEqualBool);

        mOperatorHash.put(Boolean.class, booleanOperatorHash);

        // ===============  String  ================

        HashMap<RuleOperatorType, RuleOperator<String>> stringOperatorHash = new HashMap<RuleOperatorType, RuleOperator<String>>();

        RuleOperator equalString = new DateTimeRuleOperator<String>(RuleOperatorType.Equal, false) {
            @Override
            public boolean getOperationResult2(List<String> args) {
                validateArgumentNumber(args);

                return args.get(0).equalsIgnoreCase(args.get(1));
            }
        };
        stringOperatorHash.put(equalString.getType(), equalString);

        RuleOperator notEqualString = new DateTimeRuleOperator<String>(RuleOperatorType.NotEqual, false) {
            @Override
            public boolean getOperationResult2(List<String> args) {
                validateArgumentNumber(args);

                return !args.get(0).equalsIgnoreCase(args.get(1));
            }
        };
        stringOperatorHash.put(notEqualString.getType(), equalString);

        mOperatorHash.put(String.class, stringOperatorHash);

        // ===============  Date  ================

        HashMap<RuleOperatorType, RuleOperator<java.util.Date>> dateOperatorHash = new HashMap<RuleOperatorType, RuleOperator<java.util.Date>>();

        RuleOperator equalDate = new DateTimeRuleOperator<java.util.Date>(RuleOperatorType.Equal, false) {
            @Override
            public boolean getOperationResult2(List<java.util.Date> args) {
                validateArgumentNumber(args);

                return args.get(0).equals(args.get(1));
            }
        };
        dateOperatorHash.put(equalDate.getType(), equalDate);

        RuleOperator notEqualDate = new DateTimeRuleOperator<java.util.Date>(RuleOperatorType.NotEqual, false) {
            @Override
            public boolean getOperationResult2(List<java.util.Date> args) {
                validateArgumentNumber(args);

                return !args.get(0).equals(args.get(1));
            }
        };
        dateOperatorHash.put(notEqualDate.getType(), notEqualDate);

        RuleOperator beforeDate = new DateTimeRuleOperator<java.util.Date>(RuleOperatorType.Before, false) {
            @Override
            public boolean getOperationResult2(List<java.util.Date> args) {
                validateArgumentNumber(args);

                return args.get(0).before(args.get(1));
            }
        };
        dateOperatorHash.put(beforeDate.getType(), beforeDate);

        RuleOperator afterDate = new DateTimeRuleOperator<java.util.Date>(RuleOperatorType.After, false) {
            @Override
            public boolean getOperationResult2(List<java.util.Date> args) {
                validateArgumentNumber(args);

                return args.get(0).after(args.get(1));
            }
        };
        dateOperatorHash.put(afterDate.getType(), afterDate);

        RuleOperator beforeOrEqualDate = new DateTimeRuleOperator<java.util.Date>(RuleOperatorType.BeforeOrEqual, false) {
            @Override
            public boolean getOperationResult2(List<java.util.Date> args) {
                validateArgumentNumber(args);

                return args.get(0).before(args.get(1)) || args.get(0).equals(args.get(1));
            }
        };
        dateOperatorHash.put(beforeOrEqualDate.getType(), beforeOrEqualDate);

        RuleOperator afterOrEqualDate = new DateTimeRuleOperator<java.util.Date>(RuleOperatorType.AfterOrEqual, false) {
            @Override
            public boolean getOperationResult2(List<java.util.Date> args) {
                validateArgumentNumber(args);

                return args.get(0).after(args.get(1)) || args.get(0).equals(args.get(1));
            }
        };
        dateOperatorHash.put(afterOrEqualDate.getType(), afterOrEqualDate);

        RuleOperator betweenDate = new DateTimeRuleOperator<java.util.Date>(RuleOperatorType.Between, false) {
            @Override
            public boolean getOperationResult2(List<java.util.Date> args) {
                validateArgumentNumber(args);

                return args.get(0).after(args.get(1)) && args.get(0).before(args.get(2));
            }
        };
        dateOperatorHash.put(betweenDate.getType(), betweenDate);

        RuleOperator withinDate = new DateTimeRuleOperator<java.util.Date>(RuleOperatorType.Within, false) {
            @Override
            public boolean getOperationResult2(List<java.util.Date> args) {
                validateArgumentNumber(args);

                return (args.get(0).after(args.get(1)) || args.get(0).equals(args.get(1))) && (args.get(0).before(args.get(2)) || args.get(0).equals(args.get(2)));
            }
        };
        dateOperatorHash.put(withinDate.getType(), withinDate);
        mOperatorHash.put(java.util.Date.class, dateOperatorHash);
    }

    public HashMap<RuleOperatorType, RuleOperator<?>> getUnitRuleOperator(OpenHABWidget openHABWidget) {
        if(openHABWidget == null)
            return null;

        HashMap<RuleOperatorType, RuleOperator<?>> result = null;
        switch(openHABWidget.getItem().getType()) {
            case Contact:
            case Switch:
                result = (HashMap<RuleOperatorType, RuleOperator<?>>) mOperatorHash.get(Boolean.class);
                break;
            case Number:
            case Dimmer:
            case Rollershutter:
                result = (HashMap<RuleOperatorType, RuleOperator<?>>) mOperatorHash.get(Number.class);
                break;
            case String:
            case Color:
                result = (HashMap<RuleOperatorType, RuleOperator<?>>) mOperatorHash.get(String.class);
                break;
        }

        return result;
    }

    public HashMap<RuleOperatorType, RuleOperator<?>> getUnitRuleOperatorHash(Class<?> operandClass) {
        return (HashMap<RuleOperatorType, RuleOperator<?>>) mOperatorHash.get(operandClass);
    }
}
