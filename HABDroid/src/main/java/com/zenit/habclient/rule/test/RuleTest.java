package com.zenit.habclient.rule.test;

import android.test.InstrumentationTestCase;

import com.zenit.habclient.HABApplication;
import com.zenit.habclient.UnitEntityDataType;
import com.zenit.habclient.UnitEntityDataTypeProvider;
import com.zenit.habclient.rule.IOperator;
import com.zenit.habclient.rule.IUnitEntityDataType;
import com.zenit.habclient.rule.NumberRuleOperator;
import com.zenit.habclient.rule.RuleOperation;
import com.zenit.habclient.rule.RuleOperationProvider;
import com.zenit.habclient.rule.RuleOperator;
import com.zenit.habclient.rule.RuleOperatorType;
import com.zenit.habclient.rule.RuleUnitEntity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleTest extends InstrumentationTestCase {
    private HashMap<RuleOperatorType, RuleOperator<Number>> ruleOperatorsNumeric;
    private HashMap<RuleOperatorType, RuleOperator<Boolean>> ruleOperatorsBoolean;
    private HashMap<RuleOperatorType, RuleOperator<java.util.Date>> ruleOperatorsDate;
    private UnitEntityDataTypeProvider _unitEntityDataTypeProvider;

    public void setUp() {
        RuleOperationProvider rop = new RuleOperationProvider();

        ruleOperatorsNumeric = (HashMap<RuleOperatorType, RuleOperator<Number>>) rop.mOperatorHash.get(Number.class);
        ruleOperatorsBoolean = (HashMap<RuleOperatorType, RuleOperator<Boolean>>) rop.mOperatorHash.get(Boolean.class);
        ruleOperatorsDate = (HashMap<RuleOperatorType, RuleOperator<java.util.Date>>) rop.mOperatorHash.get(java.util.Date.class);

        _unitEntityDataTypeProvider = new UnitEntityDataTypeProvider();
    }

    public void testSimple1() throws Exception {
        final int expected = 3;
        final int reality = 3;
        assertEquals(expected, reality);
    }

    public void testSimple2() {
        assertTrue(true);
    }

    public void testMathEpsilonDiff() {
        Float f = Float.valueOf(34.7f);
        Double d = Double.valueOf(34.7);
        assertEquals(true, Math.abs(f.doubleValue() - d.doubleValue()) < 8E-7f);
        assertEquals(true, Math.abs(d.doubleValue() - f.doubleValue()) < 8E-7f);
    }

//    Calculate EPSILON
//    public void testSimple4() {
//        Float f = Float.valueOf(34.7f);
//        Double d = Double.valueOf(34.7);
//        assertEquals(0, Math.abs(f.doubleValue() - d.doubleValue()));
//    }

    public void testNumberEqual() {
        RuleOperator<Number> roEqual =  ruleOperatorsNumeric.get(RuleOperatorType.Equal);

        //IllegalArgumentException shall be thrown if only one operation value is used.
        try {
            roEqual.getOperationResult(Integer.valueOf(1));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if more than two operation values is used.
        try {
            roEqual.getOperationResult(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Integer 10 equals 10 = True
        assertTrue(roEqual.getOperationResult(Integer.valueOf(10), Integer.valueOf(10)));

        //Integer 10 equals Float 10 = True
        assertTrue(roEqual.getOperationResult(Integer.valueOf(10), Float.valueOf(10)));

        //Double 10.1 equals Float 10.1 = True
        assertTrue(roEqual.getOperationResult(Double.valueOf(10.1), Float.valueOf(10.1f)));

        //Integer 9 equals 10 = False
        assertFalse(roEqual.getOperationResult(Integer.valueOf(9), Integer.valueOf(10)));

        //Float 9 equals 9 = True
        assertTrue(roEqual.getOperationResult(Float.valueOf(9), Float.valueOf(9)));

        //Float 34.7 equals 9 = False
        assertFalse(roEqual.getOperationResult(Float.valueOf(34.7f), Float.valueOf(9)));

        //Float 34.7 equals 34.7 = True
        assertTrue(roEqual.getOperationResult(Float.valueOf(34.7f), Float.valueOf(34.7f)));

        //Double 10.2 equals 10.2 = True
        assertTrue(roEqual.getOperationResult(Double.valueOf(10.2), Double.valueOf(10.2)));
    }

    public void testNumberNotEqual() {
        RuleOperator<Number> roNotEqual =  ruleOperatorsNumeric.get(RuleOperatorType.NotEqual);

        //IllegalArgumentException shall be thrown if only one operation value is used.
        try {
            roNotEqual.getOperationResult(Integer.valueOf(1));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if more than two operation values is used.
        try {
            roNotEqual.getOperationResult(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Integer 10 != 10 = False
        assertFalse(roNotEqual.getOperationResult(Integer.valueOf(10), Integer.valueOf(10)));

        //Double 10.1 != Float 10.1 = False
        assertFalse(roNotEqual.getOperationResult(Double.valueOf(10.1), Float.valueOf(10.1f)));

        //Integer 9 != 10 = False
        assertTrue(roNotEqual.getOperationResult(Integer.valueOf(9), Integer.valueOf(10)));

        //Float 9 != 9 = False
        assertFalse(roNotEqual.getOperationResult(Float.valueOf(9), Float.valueOf(9)));

        //Float 34.7 != 9 = True
        assertTrue(roNotEqual.getOperationResult(Float.valueOf(34.7f), Float.valueOf(9)));

        //Float 34.7 != 34.7 = False
        assertFalse(roNotEqual.getOperationResult(Float.valueOf(34.7f), Float.valueOf(34.7f)));

        //Double 10.2 != 10.2 = False
        assertFalse(roNotEqual.getOperationResult(Double.valueOf(10.2), Double.valueOf(10.2)));

        //Double 10.2 != 10.1 = True
        assertTrue(roNotEqual.getOperationResult(Double.valueOf(10.2), Double.valueOf(10.1)));
    }

    public void testNumberBetween() {
        RuleOperator<Number> roBetween =  ruleOperatorsNumeric.get(RuleOperatorType.Between);

        //IllegalArgumentException shall be thrown if there isn´t exactly 3 numbers of operation values.
        try {
            roBetween.getOperationResult(Integer.valueOf(1));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        try {
            roBetween.getOperationResult(Integer.valueOf(1), Integer.valueOf(2));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        try {
            roBetween.getOperationResult(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Float 9 between 10 and 13 = False
        assertFalse(roBetween.getOperationResult(Float.valueOf(9), Float.valueOf(10), Float.valueOf(13)));

        //Float 10 between 10 and 13 = False
        assertFalse(roBetween.getOperationResult(Float.valueOf(10), Float.valueOf(10), Float.valueOf(13)));

        //Float 10.1 between 10 and 13 = False
        assertTrue(roBetween.getOperationResult(Float.valueOf(10.1f), Float.valueOf(10), Float.valueOf(13)));

        //Float 11 between 10 and 13 = True
        assertTrue(roBetween.getOperationResult(Float.valueOf(11), Float.valueOf(10), Float.valueOf(13)));

        //Float 12.9 between 10 and 13 = True
        assertTrue(roBetween.getOperationResult(Float.valueOf(12.9f), Float.valueOf(10), Float.valueOf(13)));

        //Float 13 between 10 and 13 = False
        assertFalse(roBetween.getOperationResult(Float.valueOf(13), Float.valueOf(10), Float.valueOf(13)));

        //Float 14 between 10 and 13 = False
        assertFalse(roBetween.getOperationResult(Float.valueOf(14), Float.valueOf(10), Float.valueOf(13)));
    }

    public void testNumberWithin() {
        RuleOperator<Number> roWithin =  ruleOperatorsNumeric.get(RuleOperatorType.Within);

        //IllegalArgumentException shall be thrown if there isn´t exactly 3 numbers of operation values.
        try {
            roWithin.getOperationResult(Integer.valueOf(1));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        try {
            roWithin.getOperationResult(Integer.valueOf(1), Integer.valueOf(2));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        try {
            roWithin.getOperationResult(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Float 9 within 10 and 13 = False
        assertFalse(roWithin.getOperationResult(Float.valueOf(9), Float.valueOf(10), Float.valueOf(13)));

        //Float 10 within 10 and 13 = True
        assertTrue(roWithin.getOperationResult(Float.valueOf(10), Float.valueOf(10), Float.valueOf(13)));

        //Float 11 within 10 and 13 = True
        assertTrue(roWithin.getOperationResult(Float.valueOf(11), Float.valueOf(10), Float.valueOf(13)));

        //Float 12 within 10 and 13 = True
        assertTrue(roWithin.getOperationResult(Float.valueOf(12), Float.valueOf(10), Float.valueOf(13)));

        //Float 13 within 10 and 13 = True
        assertTrue(roWithin.getOperationResult(Float.valueOf(13), Float.valueOf(10), Float.valueOf(13)));

        //Float 14 within 10 and 13 = False
        assertFalse(roWithin.getOperationResult(Float.valueOf(14), Float.valueOf(10), Float.valueOf(13)));
    }

    public void testNumberLessThan() {
        RuleOperator<Number> roLessThan =  ruleOperatorsNumeric.get(RuleOperatorType.LessThan);

        //IllegalArgumentException shall be thrown if only one operation value is used.
        try {
            roLessThan.getOperationResult(Integer.valueOf(1));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if more than two operation values is used.
        try {
            roLessThan.getOperationResult(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Integer 10 < 11 = True
        assertTrue(roLessThan.getOperationResult(Integer.valueOf(10), Integer.valueOf(11)));

        //Integer 11 < 10 = False
        assertFalse(roLessThan.getOperationResult(Integer.valueOf(11), Integer.valueOf(10)));

        //Integer 10 < Float 11 = True
        assertTrue(roLessThan.getOperationResult(Integer.valueOf(10), Float.valueOf(11)));

        //Double 10.1 < Float 10.1 = False
        assertFalse(roLessThan.getOperationResult(Double.valueOf(10.1), Float.valueOf(10.1f)));

        //Double 10.1 < Float 10.2 = True
        assertTrue(roLessThan.getOperationResult(Double.valueOf(10.1), Float.valueOf(10.2f)));

        //Float 8.9 < Float 9 = True
        assertTrue(roLessThan.getOperationResult(Float.valueOf(8.9f), Float.valueOf(9)));

        //Float 34.7 < Float 9 = False
        assertFalse(roLessThan.getOperationResult(Float.valueOf(34.7f), Float.valueOf(9)));

        //Float 34.7 < Float 34.7 = False
        assertFalse(roLessThan.getOperationResult(Float.valueOf(34.7f), Float.valueOf(34.7f)));

        //Double 34.7 < Double 34.7 = False
        assertFalse(roLessThan.getOperationResult(Double.valueOf(34.7), Double.valueOf(34.7)));

        //Double -3 < Double 10 = True
        assertTrue(roLessThan.getOperationResult(Double.valueOf(-3), Double.valueOf(10)));
    }

    public void testNumberMoreThan() {
        RuleOperator<Number> roMoreThan =  ruleOperatorsNumeric.get(RuleOperatorType.MoreThan);

        //IllegalArgumentException shall be thrown if only one operation value is used.
        try {
            roMoreThan.getOperationResult(Integer.valueOf(1));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if more than two operation values is used.
        try {
            roMoreThan.getOperationResult(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Integer 10 > 11 = False
        assertFalse(roMoreThan.getOperationResult(Integer.valueOf(10), Integer.valueOf(11)));

        //Integer 11 > 10 = True
        assertTrue(roMoreThan.getOperationResult(Integer.valueOf(11), Integer.valueOf(10)));

        //Integer 10 > Float 11 = False
        assertFalse(roMoreThan.getOperationResult(Integer.valueOf(10), Float.valueOf(11)));

        //Double 10.1 > Float 10.1 = False
        assertFalse(roMoreThan.getOperationResult(Double.valueOf(10.1), Float.valueOf(10.1f)));

        //Double 10.1 > Float 10.2 = False
        assertFalse(roMoreThan.getOperationResult(Double.valueOf(10.1), Float.valueOf(10.2f)));

        //Float 8.9 > Float 9 = False
        assertFalse(roMoreThan.getOperationResult(Float.valueOf(8.9f), Float.valueOf(9)));

        //Float 34.7 > Float 9 = True
        assertTrue(roMoreThan.getOperationResult(Float.valueOf(34.7f), Float.valueOf(9)));

        //Float 34.7 > Float 34.7 = False
        assertFalse(roMoreThan.getOperationResult(Float.valueOf(34.7f), Float.valueOf(34.7f)));

        //Double 34.7 > Double 34.7 = False
        assertFalse(roMoreThan.getOperationResult(Double.valueOf(34.7), Double.valueOf(34.7)));

        //Double -3 > Double 10 = False
        assertFalse(roMoreThan.getOperationResult(Double.valueOf(-3), Double.valueOf(10)));
    }

    public void testNumberLessOrEqual() {
        RuleOperator<Number> roLessOrEqual =  ruleOperatorsNumeric.get(RuleOperatorType.LessOrEqual);

        //IllegalArgumentException shall be thrown if only one operation value is used.
        try {
            roLessOrEqual.getOperationResult(Integer.valueOf(1));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if more than two operation values is used.
        try {
            roLessOrEqual.getOperationResult(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Integer 10 <= 11 = True
        assertTrue(roLessOrEqual.getOperationResult(Integer.valueOf(10), Integer.valueOf(11)));

        //Integer 11 <= 10 = False
        assertFalse(roLessOrEqual.getOperationResult(Integer.valueOf(11), Integer.valueOf(10)));

        //Integer 10 <= Float 11 = True
        assertTrue(roLessOrEqual.getOperationResult(Integer.valueOf(10), Float.valueOf(11)));

        //Double 10.1 <= Float 10.1 = True
        assertTrue(roLessOrEqual.getOperationResult(Double.valueOf(10.1), Float.valueOf(10.1f)));

        //Double 10.1 <= Float 10.2 = True
        assertTrue(roLessOrEqual.getOperationResult(Double.valueOf(10.1), Float.valueOf(10.2f)));

        //Float 8.9 <= Float 9 = True
        assertTrue(roLessOrEqual.getOperationResult(Float.valueOf(8.9f), Float.valueOf(9)));

        //Float 34.7 <= Float 9 = False
        assertFalse(roLessOrEqual.getOperationResult(Float.valueOf(34.7f), Float.valueOf(9)));

        //Float 34.7 <= Float 34.7 = True
        assertTrue(roLessOrEqual.getOperationResult(Float.valueOf(34.7f), Float.valueOf(34.7f)));

        //Double 34.7 <= Double 34.7 = True
        assertTrue(roLessOrEqual.getOperationResult(Double.valueOf(34.7), Double.valueOf(34.7)));

        //Double -3 <= Double 10 = True
        assertTrue(roLessOrEqual.getOperationResult(Double.valueOf(-3), Double.valueOf(10)));
    }

    public void testNumberMoreOrEqual() {
        RuleOperator<Number> roMoreOrEqual =  ruleOperatorsNumeric.get(RuleOperatorType.MoreOrEqual);

        //IllegalArgumentException shall be thrown if only one operation value is used.
        try {
            roMoreOrEqual.getOperationResult(Integer.valueOf(1));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if more than two operation values is used.
        try {
            roMoreOrEqual.getOperationResult(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Integer 10 >= 11 = False
        assertFalse(roMoreOrEqual.getOperationResult(Integer.valueOf(10), Integer.valueOf(11)));

        //Integer 11 >= 10 = True
        assertTrue(roMoreOrEqual.getOperationResult(Integer.valueOf(11), Integer.valueOf(10)));

        //Integer 10 >= Float 11 = False
        assertFalse(roMoreOrEqual.getOperationResult(Integer.valueOf(10), Float.valueOf(11)));

        //Double 10.1 >= Float 10.1 = True
        assertTrue(roMoreOrEqual.getOperationResult(Double.valueOf(10.1), Float.valueOf(10.1f)));

        //Double 10.1 >= Float 10.2 = False
        assertFalse(roMoreOrEqual.getOperationResult(Double.valueOf(10.1), Float.valueOf(10.2f)));

        //Float 8.9 >= Float 9 = False
        assertFalse(roMoreOrEqual.getOperationResult(Float.valueOf(8.9f), Float.valueOf(9)));

        //Float 34.7 >= Float 9 = True
        assertTrue(roMoreOrEqual.getOperationResult(Float.valueOf(34.7f), Float.valueOf(9)));

        //Float 34.7 >= Float 34.7 = True
        assertTrue(roMoreOrEqual.getOperationResult(Float.valueOf(34.7f), Float.valueOf(34.7f)));

        //Double 34.7 >= Double 34.7 = True
        assertTrue(roMoreOrEqual.getOperationResult(Double.valueOf(34.7), Double.valueOf(34.7)));

        //Double -3 >= Double 10 = False
        assertFalse(roMoreOrEqual.getOperationResult(Double.valueOf(-3), Double.valueOf(10)));
    }

    public void testBooleanAnd() {
        RuleOperator<Boolean> roAnd =  ruleOperatorsBoolean.get(RuleOperatorType.And);

        //IllegalArgumentException shall be thrown if only one operation value is used.
        try {
            assertTrue(roAnd.getOperationResult(Boolean.valueOf(false)));
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if more than two operation values is used.
        try {
            assertTrue(roAnd.getOperationResult(Boolean.valueOf(true), Boolean.valueOf(true), Boolean.valueOf(true)));
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Boolean true AND false = False
        assertFalse(roAnd.getOperationResult(Boolean.valueOf(true), Boolean.valueOf(false)));

        //Boolean false AND true = False
        assertFalse(roAnd.getOperationResult(Boolean.valueOf(false), Boolean.valueOf(true)));

        //Boolean false AND false = False
        assertFalse(roAnd.getOperationResult(Boolean.valueOf(false), Boolean.valueOf(false)));

        //Boolean true AND true = True
        assertTrue(roAnd.getOperationResult(Boolean.valueOf(true), Boolean.valueOf(true)));
    }

    public void testBooleanOr() {
        RuleOperator<Boolean> roOr =  ruleOperatorsBoolean.get(RuleOperatorType.Or);

        //IllegalArgumentException shall be thrown if only one operation value is used.
        try {
            roOr.getOperationResult(Boolean.valueOf(false));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if more than two operation values is used.
        try {
            roOr.getOperationResult(Boolean.valueOf(false), Boolean.valueOf(false), Boolean.valueOf(false));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Boolean true OR false = False
        assertTrue(roOr.getOperationResult(Boolean.valueOf(true), Boolean.valueOf(false)));

        //Boolean false OR true = False
        assertTrue(roOr.getOperationResult(Boolean.valueOf(false), Boolean.valueOf(true)));

        //Boolean false OR false = False
        assertFalse(roOr.getOperationResult(Boolean.valueOf(false), Boolean.valueOf(false)));

        //Boolean true OR true = True
        assertTrue(roOr.getOperationResult(Boolean.valueOf(true), Boolean.valueOf(true)));
    }

    public void testBooleanEqual() {
        RuleOperator<Boolean> roEqual =  ruleOperatorsBoolean.get(RuleOperatorType.Equal);

        //IllegalArgumentException shall be thrown if only one operation value is used.
        try {
            roEqual.getOperationResult(Boolean.valueOf(false));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if more than two operation values is used.
        try {
            roEqual.getOperationResult(Boolean.valueOf(false), Boolean.valueOf(true), Boolean.valueOf(false));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Boolean true Equal false = False
        assertFalse(roEqual.getOperationResult(Boolean.valueOf(true), Boolean.valueOf(false)));

        //Boolean false Equal true = False
        assertFalse(roEqual.getOperationResult(Boolean.valueOf(false), Boolean.valueOf(true)));

        //Boolean false Equal false = False
        assertTrue(roEqual.getOperationResult(Boolean.valueOf(false), Boolean.valueOf(false)));

        //Boolean true Equal true = True
        assertTrue(roEqual.getOperationResult(Boolean.valueOf(true), Boolean.valueOf(true)));
    }

    public void testBooleanNotEqual() {
        RuleOperator<Boolean> roNotEqual =  ruleOperatorsBoolean.get(RuleOperatorType.NotEqual);

        //IllegalArgumentException shall be thrown if only one operation value is used.
        try {
            roNotEqual.getOperationResult(Boolean.valueOf(false));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if more than two operation values is used.
        try {
            roNotEqual.getOperationResult(Boolean.valueOf(false), Boolean.valueOf(true), Boolean.valueOf(false));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Boolean true NotEqual false = False
        assertTrue(roNotEqual.getOperationResult(Boolean.valueOf(true), Boolean.valueOf(false)));

        //Boolean false NotEqual true = False
        assertTrue(roNotEqual.getOperationResult(Boolean.valueOf(false), Boolean.valueOf(true)));

        //Boolean false NotEqual false = False
        assertFalse(roNotEqual.getOperationResult(Boolean.valueOf(false), Boolean.valueOf(false)));

        //Boolean true NotEqual true = True
        assertFalse(roNotEqual.getOperationResult(Boolean.valueOf(true), Boolean.valueOf(true)));
    }

    public void test_Verify_value_in_UnitEntityDataType() {
        RuleUnitEntity rue = new RuleUnitEntity<Double>("Test Value", 50.7) {
            @Override
            public Double valueOf(String input) {
                return Double.valueOf(input);
            }
        };

        assertTrue(rue.getValue() != null);
        assertEquals(50.7, rue.getValue());
    }

    private List<IUnitEntityDataType> getOperandsAsList() {
        List<IUnitEntityDataType> operands = new ArrayList<IUnitEntityDataType>();
        operands.add(_unitEntityDataTypeProvider.getUnitDataTypeList().get(2));

        RuleUnitEntity rue = new RuleUnitEntity<Double>("Test Value", 50.7) {
            @Override
            public Double valueOf(String input) {
                return Double.valueOf(input);
            }
        };
        operands.add(rue);

        return operands;
    }

//    public void test_RuleOperator_parseValue_method() {
//        RuleOperationProvider rop2 = new RuleOperationProvider();
//        HashMap<RuleOperatorType, RuleOperator<Integer>> ruleOperatorsInteger = (HashMap<RuleOperatorType, RuleOperator<Integer>>) rop2.mOperatorHash.get(Number.class);
//
//        RuleOperator<Integer> roEqual =  ruleOperatorsInteger.get(RuleOperatorType.Equal);
//
//        try {
//            assertEquals((Integer)10, roEqual.parseValue("10"));
//        } catch (Exception e) {
//            assertEquals("This should not happen", e.getClass().getName());
//        }
//    }

    public void test_should_be_able_to_use_a_List_of_IUnitEntityDataType_as_RuleOperator_input() {
        RuleOperator<Float> testOperatorNum = new RuleOperator<Float>(RuleOperatorType.Equal, false) {
            @Override
            public boolean getOperationResult2(List<Float> args) {
                return true;
            }

            @Override
            public Float parseValue(String valueAsString) {
                return (Float) Float.valueOf(valueAsString);
            }
        };

        List<IUnitEntityDataType> operands = getOperandsAsList();

        //IllegalArgumentException shall be thrown if there isn´t exactly 3 numbers of operation values.
        try {
            testOperatorNum.getOperationResult(operands);
            assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            assertEquals("This should not happen", e.toString()/*getClass().getName()*/);
            //assertFalse(e instanceof IllegalArgumentException);
        }
    }

    public void test_should_be_able_to_use_a_List_of_IUnitEntityDataType_as_RuleOperator_input2() {
        RuleOperator<Number> testOperatorNum = new RuleOperator<Number>(RuleOperatorType.Equal, false) {
            @Override
            public boolean getOperationResult2(List<Number> args) {
                Number result = 0;
                Iterator iterator = args.iterator();

                if(iterator.hasNext())
                    result = (Number)iterator.next();

                while(iterator.hasNext()) {
                    result = result.floatValue() + ((Number)iterator.next()).floatValue();
                }

                assertEquals(101.4f, result);
                return true;
            }

            @Override
            public Number parseValue(String valueAsString) {
                return (Number) Float.valueOf(valueAsString);
            }
        };

        List<IUnitEntityDataType> operands = getOperandsAsList();

        //IllegalArgumentException shall be thrown if there isn´t exactly 3 numbers of operation values.
        try {
            testOperatorNum.getOperationResult(operands);
            assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            assertEquals("This should not happen", e.toString()/*getClass().getName()*/);
            //assertFalse(e instanceof IllegalArgumentException);
        }
    }

    public void test_should_be_able_to_use_a_List_of_IUnitEntityDataType_as_RuleOperator_input3() {
        RuleOperator<Boolean> testOperatorNum = new RuleOperator<Boolean>(RuleOperatorType.Equal, false) {
            @Override
            public boolean getOperationResult2(List<Boolean> args) {
                Boolean result = false;
                Iterator iterator = args.iterator();

                if(iterator.hasNext())
                    result = (Boolean)iterator.next();

                while(iterator.hasNext()) {
                    result = result && (Boolean)iterator.next();
                }

                return result;
            }

            @Override
            public Boolean parseValue(String valueAsString) {
                return (Boolean) Boolean.valueOf(valueAsString);
            }
        };

        List<IUnitEntityDataType> operands = getOperandsAsList();

        //IllegalArgumentException shall be thrown if there isn´t exactly 3 numbers of operation values.
        try {
            testOperatorNum.getOperationResult(operands);
            assertTrue(false);
        } catch (Exception e) {
            assertEquals("java.lang.ClassCastException", e.getClass().getName());
            //assertFalse(e instanceof IllegalArgumentException);
        }
    }

    public void test_RuleOperator_should_accept_both_values_in_list_as_operands() {
        IOperator<Number> roEqual =  ruleOperatorsNumeric.get(RuleOperatorType.Equal);

        List<IUnitEntityDataType> operands = getOperandsAsList();

        assertTrue(operands.get(0) != null);
        assertTrue(operands.get(1) != null);

        assertTrue(operands.get(0).getValue() != null);
        assertTrue(operands.get(1).getValue() != null);

        //IllegalArgumentException shall be thrown if there isn´t exactly 3 numbers of operation values.
        try {
            roEqual.getOperationResult(Double.valueOf(operands.get(0).getValue().toString()), Double.valueOf(operands.get(1).getValue().toString()));
            roEqual.getOperationResult(roEqual.parseValue(operands.get(0).getValue().toString()), roEqual.parseValue(operands.get(1).getValue().toString()));
            roEqual.getOperationResult(operands);
            assertTrue(true);
        } catch (Exception e) {
            assertEquals("Something whent wrong", e.toString());
//            assertEquals("This should not happen", e.getClass().getName());
            //assertFalse(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if there isn´t exactly 3 numbers of operation values.
        try {
            roEqual.getOperationResult(Double.valueOf(operands.get(0).getValue().toString()), Double.valueOf(operands.get(1).getValue().toString()));
            assertTrue(true);
        } catch (Exception e) {
            assertEquals("This should not happen", e.getClass().getName());
            //assertFalse(e instanceof IllegalArgumentException);
        }

        //Float 12 within 10 and 13 = True
//        assertTrue(roWithin.getOperationResult(Float.valueOf(12), Float.valueOf(10), Float.valueOf(13)));
    }

    public void test_Create_RuleOperation_object_and_validate_operation_result() {
        assertEquals("Humidity percentage", _unitEntityDataTypeProvider.getUnitDataTypeList().get(2).getName());
        assertEquals(50.7, _unitEntityDataTypeProvider.getUnitDataTypeList().get(2).getValue());

        List<IUnitEntityDataType> operands = getOperandsAsList();

        //First operation (Rule A)
        RuleOperator<Number> operator =  ruleOperatorsNumeric.get(RuleOperatorType.Equal);

        RuleOperation roA = new RuleOperation(operator, operands);
        assertEquals("Humidity percentage = 50.7", roA.toString());
        assertEquals(true, roA.getResult());

        //Second operation (Rule B)
        RuleOperator<Number> operator =  ruleOperatorsNumeric.get(RuleOperatorType.LessThan);

        RuleOperation roB = new RuleOperation(operator, operands);
        assertEquals("Humidity percentage < 50.7", roB.toString());
        assertEquals(false, roB.getResult());

        assertEquals(false, );
    }

    //mRuleOperator.getOperationResult(mOperands.get(0).getValue(), mRuleOperator.parseValue("10"));

    private List<IUnitEntityDataType> getOperandsAsList2() {
        List<IUnitEntityDataType> operands = new ArrayList<IUnitEntityDataType>();
        operands.add(_unitEntityDataTypeProvider.getUnitDataTypeList().get(2));

        RuleUnitEntity rue = new RuleUnitEntity<Double>("Test Value", 50.7) {
            @Override
            public Double valueOf(String input) {
                return Double.valueOf(input);
            }
        };
        operands.add(rue);

        RuleUnitEntity rue2 = new RuleUnitEntity<Float>("Test Value2", 16.8f) {
            @Override
            public Float valueOf(String input) {
                return Float.valueOf(input);
            }
        };
        operands.add(rue2);

        return operands;
    }

    public void test_RuleOperator_should_accept_all_3_values_in_list_as_operands() {
        RuleOperator<Number> roWithin =  ruleOperatorsNumeric.get(RuleOperatorType.Within);

        List<IUnitEntityDataType> operands = getOperandsAsList2();

        //IllegalArgumentException shall be thrown if there isn´t exactly 3 numbers of operation values.
        try {
            roWithin.getOperationResult((Number)operands.get(0).getValue(), (Number)operands.get(1).getValue(), (Number)operands.get(2).getValue());
            roWithin.getOperationResult(operands);
            assertTrue(true);
        } catch (Exception e) {
            assertEquals("This should not happen", e.getClass().getName());
            //assertFalse(e instanceof IllegalArgumentException);
        }

        //Float 12 within 10 and 13 = True
        assertTrue(roWithin.getOperationResult(Float.valueOf(12), Float.valueOf(10), Float.valueOf(13)));
    }

    public void testDateAfter() {
        RuleOperator<java.util.Date> roAfter =  ruleOperatorsDate.get(RuleOperatorType.After);

        //IllegalArgumentException shall be thrown if only one operation value is used.
        try {
            roAfter.getOperationResult(roAfter.parseValue("12:15"));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //IllegalArgumentException shall be thrown if more than two operation values is used.
        try {
            roAfter.getOperationResult(roAfter.parseValue("12:15"), roAfter.parseValue("13:30"), roAfter.parseValue("14:45"));
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        //Date 10:15 after 11:30 = False
        try {
            assertFalse(roAfter.getOperationResult(roAfter.parseValue("10:15"), roAfter.parseValue("11:30")));
        } catch (Exception e) {
            assertEquals("Should have returned an instance of java.util.Date", e.toString()/*getClass().getSimpleName()*/);
        }

        //Date 11:30 after 10:15 = True
        try {
            assertTrue(roAfter.getOperationResult(roAfter.parseValue("11:30"), roAfter.parseValue("10:15")));
        } catch (Exception e) {
            assertEquals("Should have returned an instance of java.util.Date", e.toString()/*getClass().getSimpleName()*/);
        }

        //Date Mon 11:30 after Tue 10:15 = False
        try {
            assertFalse(roAfter.getOperationResult(roAfter.parseValue("Mon 11:30"), roAfter.parseValue("Tue 10:15")));
        } catch (Exception e) {
            assertEquals("Should have returned an instance of java.util.Date", e.toString()/*getClass().getSimpleName()*/);
        }

        //Date Sun 11:30 after Mon 10:15 = False
        try {
            assertFalse(roAfter.getOperationResult(roAfter.parseValue("Sun 11:30"), roAfter.parseValue("Mon 10:15")));
        } catch (Exception e) {
            assertEquals("Should have returned an instance of java.util.Date", e.toString()/*getClass().getSimpleName()*/);
        }

        //Date Sat 11:30 after Mon 10:15 = False
        try {
            assertFalse(roAfter.getOperationResult(roAfter.parseValue("Sat 11:30"), roAfter.parseValue("Mon 10:15")));
        } catch (Exception e) {
            assertEquals("Should have returned an instance of java.util.Date", e.toString()/*getClass().getSimpleName()*/);
        }

        //Date Mon 10:30 after Sun 11:15 = False
        try {
            assertFalse(roAfter.getOperationResult(roAfter.parseValue("Mon 10:30"), roAfter.parseValue("Sun 11:15")));
        } catch (Exception e) {
            assertEquals("Should have returned an instance of java.util.Date", e.toString()/*getClass().getSimpleName()*/);
        }
    }

}
