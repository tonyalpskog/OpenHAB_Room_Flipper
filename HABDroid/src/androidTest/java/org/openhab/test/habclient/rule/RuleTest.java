package org.openhab.test.habclient.rule;

import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.rule.IEntityDataType;
import org.openhab.domain.rule.IOperator;
import org.openhab.domain.rule.OnValueChangedListener;
import org.openhab.domain.rule.RuleOperation;
import org.openhab.domain.rule.RuleOperationProvider;
import org.openhab.domain.rule.operators.RuleOperator;
import org.openhab.domain.rule.RuleOperatorType;
import org.openhab.domain.rule.UnitEntityDataType;
import org.openhab.habclient.HABApplication;
import org.openhab.habclient.UnitEntityDataTypeProvider;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleTest extends android.test.ApplicationTestCase<HABApplication> {
    private HashMap<RuleOperatorType, RuleOperator<Number>> ruleOperatorsNumeric;
    private HashMap<RuleOperatorType, RuleOperator<Date>> ruleOperatorsDate;
    private UnitEntityDataTypeProvider _unitEntityDataTypeProvider;
    private HABApplication mHABApplication;

    public RuleTest() {
        super(HABApplication.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        initMocks(this);
        createApplication();
        mHABApplication = getApplication();

        HttpDataSetup httpDataSetup = new HttpDataSetup(mHABApplication.getLogger(),
                mHABApplication.getColorParser(),
                mHABApplication.getOpenHABWidgetProvider());
        httpDataSetup.loadHttpDataFromString();

        RuleOperationProvider rop = mHABApplication.getRuleOperationProvider();

        ruleOperatorsNumeric = (HashMap<RuleOperatorType, RuleOperator<Number>>) rop.mOperatorHash.get(Number.class);
        ruleOperatorsDate = (HashMap<RuleOperatorType, RuleOperator<java.util.Date>>) rop.mOperatorHash.get(java.util.Date.class);

        _unitEntityDataTypeProvider = new UnitEntityDataTypeProvider();
    }

    //================================= UNITS ===================================

    public void testSimple1() throws Exception {
        final int expected = 3;
        final int reality = 3;
        assertEquals(expected, reality);
    }

    public void testSimple2() {
        assertTrue(true);
    }

    public void testGetWidgetById() {
        OpenHABWidget unit = mHABApplication.getOpenHABWidgetProvider().getWidgetByID("GF_Kitchen_0");
        assertEquals("Ceiling", unit.getLabel());
        assertEquals("Light_GF_Kitchen_Ceiling", unit.getItem().getName());
    }

    public void testMathEpsilonDiff() {
        Float f = 34.7f;
        Double d = 34.7;
        assertEquals(true, Math.abs(f.doubleValue() - d) < 8E-7f);
        assertEquals(true, Math.abs(d - f.doubleValue()) < 8E-7f);
    }

//    Calculate EPSILON
//    public void testSimple4() {
//        Float f = Float.valueOf(34.7f);
//        Double d = Double.valueOf(34.7);
//        assertEquals(0, Math.abs(f.doubleValue() - d.doubleValue()));
//    }

    public void test_Verify_value_and_name_in_UnitEntityDataType() {
        UnitEntityDataType rue = new UnitEntityDataType("Test Value", 50.7) {
            @Override
            public Double valueOf(String input) {
                return Double.valueOf(input);
            }

            @Override
            public Map getStaticValues() {
                return null;
            }
        };

        assertTrue(rue.getValue() != null);
        assertEquals(50.7, rue.getValue());
        assertEquals("50.7", rue.getFormattedString());
        assertEquals("Test Value", rue.getName());

        //Now with a custom getFormattedString() method.
        rue = new UnitEntityDataType("Test Value", 50.7) {
            @Override
            public Double valueOf(String input) {
                return Double.valueOf(input);
            }
            @Override
            public String getFormattedString(){
                return mValue.toString() + "%Rh";
            }

            @Override
            public Map getStaticValues() {
                return null;
            }
        };

        assertTrue(rue.getValue() != null);
        assertEquals(50.7, rue.getValue());
        assertEquals("50.7%Rh", rue.getFormattedString());
        assertEquals("Test Value", rue.getName());
    }

    private List<IEntityDataType> getOperandsAsList() {
        List<IEntityDataType> operands = new ArrayList<IEntityDataType>();
        operands.add(_unitEntityDataTypeProvider.getUnitDataTypeList().get(2));

        UnitEntityDataType rue = new UnitEntityDataType<Double>("Test Value", 50.7) {
            @Override
            public Double valueOf(String input) {
                return Double.valueOf(input);
            }

            @Override
            public Map<String, Double> getStaticValues() {
                return null;
            }
        };
        operands.add(rue);

        return operands;
    }

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

        List<IEntityDataType> operands = getOperandsAsList();

        //IllegalArgumentException shall be thrown if there isn´t exactly 3 numbers of operation values.
        try {
            testOperatorNum.getOperationResult(operands);
            assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            assertEquals("This should not happen", e.toString()/*getClass().getDescription()*/);
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
                return Float.valueOf(valueAsString);
            }
        };

        List<IEntityDataType> operands = getOperandsAsList();

        //IllegalArgumentException shall be thrown if there isn´t exactly 3 numbers of operation values.
        try {
            testOperatorNum.getOperationResult(operands);
            assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            assertEquals("This should not happen", e.toString()/*getClass().getDescription()*/);
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
                return Boolean.valueOf(valueAsString);
            }
        };

        List<IEntityDataType> operands = getOperandsAsList();

        //IllegalArgumentException shall be thrown if there isn´t exactly 3 numbers of operation values.
        try {
            testOperatorNum.getOperationResult(operands);
            assertTrue(false);
        } catch (Exception e) {
            assertEquals("java.lang.ClassCastException", e.getClass().getName());
        }
    }

    public void test_RuleOperator_should_accept_both_values_in_list_as_operands() {
        IOperator<Number> roEqual =  ruleOperatorsNumeric.get(RuleOperatorType.Equal);

        List<IEntityDataType> operands = getOperandsAsList();

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
        }

        //IllegalArgumentException shall be thrown if there isn´t exactly 3 numbers of operation values.
        try {
            roEqual.getOperationResult(Double.valueOf(operands.get(0).getValue().toString()), Double.valueOf(operands.get(1).getValue().toString()));
            assertTrue(true);
        } catch (Exception e) {
            assertEquals("This should not happen", e.getClass().getName());
        }
    }

    public void test_Create_RuleOperation_object_and_validate_operation_result() {
        assertEquals("Humidity percentage", _unitEntityDataTypeProvider.getUnitDataTypeList().get(2).getName());
        assertEquals(50.7, _unitEntityDataTypeProvider.getUnitDataTypeList().get(2).getValue());

        List<IEntityDataType> operands = getOperandsAsList();

        //First operation (Rule A)
        RuleOperator<Number> operator =  ruleOperatorsNumeric.get(RuleOperatorType.Equal);

        RuleOperation roA = new RuleOperation(operator, operands);
        assertEquals("Humidity percentage [50.7%Rh] = Test Value [50.7]", roA.toString());
        assertEquals(true, roA.getValue().booleanValue());

        //Second operation (Rule B)
        RuleOperator<Number> operator2 =  ruleOperatorsNumeric.get(RuleOperatorType.LessThan);

        List<IEntityDataType> operands2 = getOperandsAsList();
        RuleOperation roB = new RuleOperation(operator2, operands2);
        assertEquals("Humidity percentage [50.7%Rh] < Test Value [50.7]", roB.toString());
        assertEquals(false, roB.getValue().booleanValue());

        assertEquals(false, roA.getValue() && roB.getValue());

        //Test if a value change on first operator in second operation will update itself and the sub-operations
        UnitEntityDataType operand1 = ((UnitEntityDataType)operands2.get(0));
        assertEquals(50.7d, operand1.getValue());
        operand1.setValue(59.2d);
        assertEquals(59.2d, operand1.getValue());
        ((OnValueChangedListener)operands2.get(0)).onValueChanged(operands2.get(0).getDataSourceId(), "43.5");
        assertEquals(43.5d, operand1.getValue());
        assertEquals(true, roA.getValue().booleanValue());
        assertEquals(true, roB.getValue().booleanValue());
        assertEquals(true, roA.getValue() && roB.getValue());
    }

    private List<IEntityDataType> getOperandsAsList3(int operandPairNumber) {
        List<IEntityDataType> operands = new ArrayList<IEntityDataType>();

        switch(operandPairNumber) {
            case 1:
                //Switch
                operands.add(UnitEntityDataType.getUnitEntityDataType(mHABApplication.getOpenHABWidgetProvider().getWidgetByID("GF_Kitchen_0")));
                operands.add(UnitEntityDataType.getUnitEntityDataType(mHABApplication.getOpenHABWidgetProvider().getWidgetByID("FF_Bath_1")));
                break;

            case 2:
                //Number
                operands.add(UnitEntityDataType.getUnitEntityDataType(mHABApplication.getOpenHABWidgetProvider().getWidgetByID("FF_Bed_3")));
                operands.add(UnitEntityDataType.getUnitEntityDataType(mHABApplication.getOpenHABWidgetProvider().getWidgetByID("GF_Toilet_4")));
                break;
        }

        return operands;
    }

    public void test_Create_RuleOperation_object_from_provider_units_and_validate_operation_result() {
        RuleOperationProvider rop = mHABApplication.getRuleOperationProvider();

        OpenHABWidget widget = mHABApplication.getOpenHABWidgetProvider().getWidgetByID("GF_Kitchen_0");
        RuleOperation roA = new RuleOperation(rop.getUnitRuleOperator(widget).get(RuleOperatorType.Equal), getOperandsAsList3(1));
        assertEquals("Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF]", roA.toString());
        assertEquals(true, roA.getValue().booleanValue());

        widget = mHABApplication.getOpenHABWidgetProvider().getWidgetByID("FF_Bath_1");
        roA = new RuleOperation(rop.getUnitRuleOperator(widget).get(RuleOperatorType.NotEqual), getOperandsAsList3(1));
        assertEquals("Light_GF_Kitchen_Ceiling [OFF] != Light_FF_Bath_Mirror [OFF]", roA.toString());
        assertEquals(false, roA.getValue().booleanValue());

        widget = mHABApplication.getOpenHABWidgetProvider().getWidgetByID("FF_Bed_3");
        RuleOperation roB = new RuleOperation(rop.getUnitRuleOperator(widget).get(RuleOperatorType.LessThan), getOperandsAsList3(2));
        assertEquals("Temperature_FF_Bed [19.2] < Temperature_GF_Toilet [21.5]", roB.toString());
        assertEquals(true, roB.getValue().booleanValue());

        widget = mHABApplication.getOpenHABWidgetProvider().getWidgetByID("GF_Toilet_4");
        roB = new RuleOperation(rop.getUnitRuleOperator(widget).get(RuleOperatorType.MoreThan), getOperandsAsList3(2));
        assertEquals("Temperature_FF_Bed [19.2] > Temperature_GF_Toilet [21.5]", roB.toString());
        assertEquals(false, roB.getValue().booleanValue());

        assertEquals(false, roA.getValue() && roB.getValue());
    }

    private List<IEntityDataType> getListOfRuleOperationsForTest() {
        RuleOperationProvider rop = mHABApplication.getRuleOperationProvider();

        OpenHABWidget widget = mHABApplication.getOpenHABWidgetProvider().getWidgetByID("GF_Kitchen_0");
        RuleOperation roA = new RuleOperation(rop.getUnitRuleOperator(widget).get(RuleOperatorType.Equal), getOperandsAsList3(1));

        widget = mHABApplication.getOpenHABWidgetProvider().getWidgetByID("FF_Bed_3");
        RuleOperation roB = new RuleOperation(rop.getUnitRuleOperator(widget).get(RuleOperatorType.LessThan), getOperandsAsList3(2));

        List<IEntityDataType> operandList = new ArrayList<IEntityDataType>();
        operandList.add(roA);
        operandList.add(roB);

        return operandList;
    }

    private static final String LEFT_OPERAND_NAME = "Operation as left operand";
    private RuleOperation getNestedRuleOperationForTest(boolean nameTheLeftOperand, RuleOperatorType ruleType) {
        RuleOperationProvider rop = mHABApplication.getRuleOperationProvider();

        List<IEntityDataType> operandList = getListOfRuleOperationsForTest();
        if(nameTheLeftOperand) ((RuleOperation) operandList.get(0)).setName("Operation as left operand");
        RuleOperation ro = new RuleOperation((RuleOperator<Boolean>) rop.mOperatorHash.get(operandList.get(0).getDataType()).get(ruleType), operandList);

        return ro;
    }

    public void test_RuleOperation_toString_methods_on_nameless_operation_and_sub_operations() {
        RuleOperation ro = getNestedRuleOperationForTest(false, RuleOperatorType.And);

        assertEquals("(Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF]) AND (Temperature_FF_Bed [19.2] < Temperature_GF_Toilet [21.5])", ro.toString());
        assertEquals("[Sant] (Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF]) AND (Temperature_FF_Bed [19.2] < Temperature_GF_Toilet [21.5])", ro.toString(true, false));
        assertEquals("(Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF]) AND (Temperature_FF_Bed [19.2] < Temperature_GF_Toilet [21.5]) [Sant]", ro.toString(false, true));
        assertEquals("(Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF]) AND (Temperature_FF_Bed [19.2] < Temperature_GF_Toilet [21.5])", ro.toString(false, false));
        assertEquals("[Sant] (Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF]) AND (Temperature_FF_Bed [19.2] < Temperature_GF_Toilet [21.5]) [Sant]", ro.toString(true, true));
    }

    public void test_RuleOperation_toString_methods_on_named_operation_and_mixed_sub_operations() {
        RuleOperation ro = getNestedRuleOperationForTest(true, RuleOperatorType.NotEqual);

        assertEquals(LEFT_OPERAND_NAME + " [Sant] != (Temperature_FF_Bed [19.2] < Temperature_GF_Toilet [21.5])", ro.toString());
        assertEquals("[Falskt] " + LEFT_OPERAND_NAME + " [Sant] != (Temperature_FF_Bed [19.2] < Temperature_GF_Toilet [21.5])", ro.toString(true, false));

        //Name the main operation
        final String OPERATION_NAME = "My sweet operation name";
        ro.setName(OPERATION_NAME);
        assertEquals("[Falskt] " + OPERATION_NAME, ro.toString(true, false));
        assertEquals(OPERATION_NAME + " [Falskt]", ro.toString(false, true));
        assertEquals(OPERATION_NAME, ro.toString(false, false));
        assertEquals(OPERATION_NAME + " [Falskt]", ro.toString());
    }

    public void test_RuleOperation_toString_methods_on_null_objects() {
        RuleOperationProvider rop = mHABApplication.getRuleOperationProvider();

        List<IEntityDataType> operandList = getListOfRuleOperationsForTest();
        ((RuleOperation) operandList.get(0)).setRuleOperator(null);
        ((RuleOperation) operandList.get(1)).setOperand(1, null);
        RuleOperation ro = new RuleOperation((RuleOperator<Boolean>) rop.mOperatorHash.get(operandList.get(0).getDataType()).get(RuleOperatorType.Or), operandList);

        assertEquals("(Light_GF_Kitchen_Ceiling [OFF] " + RuleOperator.MISSING_OPERATOR + ") OR (Temperature_FF_Bed [19.2] < " + RuleOperatorType.MISSING_OPERAND + ")", ro.toString());
        assertEquals(Boolean.FALSE, ((RuleOperation) operandList.get(0)).getValue());
        assertEquals(Boolean.FALSE, ((RuleOperation) operandList.get(1)).getValue());

        ((RuleOperation) operandList.get(0)).setName("Operation as left operand");
        ro = new RuleOperation((RuleOperator<Boolean>) rop.mOperatorHash.get(operandList.get(0).getDataType()).get(RuleOperatorType.Or), operandList);

        assertEquals("Operation as left operand <Incomplete> [Falskt] OR (Temperature_FF_Bed [19.2] < " + RuleOperatorType.MISSING_OPERAND + ")", ro.toString());
        assertEquals(Boolean.FALSE, ((RuleOperation) operandList.get(0)).getValue());
        assertEquals(Boolean.FALSE, ((RuleOperation) operandList.get(1)).getValue());

        ((RuleOperation) operandList.get(0)).setOperand(1, null);
        ro = new RuleOperation((RuleOperator<Boolean>) rop.mOperatorHash.get(operandList.get(0).getDataType()).get(RuleOperatorType.Or), operandList);

        assertEquals("Operation as left operand <Incomplete> [Falskt] OR (Temperature_FF_Bed [19.2] < " + RuleOperatorType.MISSING_OPERAND + ")", ro.toString());
        assertEquals(Boolean.FALSE, ((RuleOperation) operandList.get(0)).getValue());
        assertEquals(Boolean.FALSE, ((RuleOperation) operandList.get(1)).getValue());

        ((RuleOperation) operandList.get(0)).setOperand(0, null);
        ro = new RuleOperation((RuleOperator<Boolean>) rop.mOperatorHash.get(operandList.get(0).getDataType()).get(RuleOperatorType.Or), operandList);

        assertEquals("Operation as left operand <Incomplete> [Falskt] OR (Temperature_FF_Bed [19.2] < " + RuleOperatorType.MISSING_OPERAND + ")", ro.toString());
        assertEquals(Boolean.FALSE, ((RuleOperation) operandList.get(0)).getValue());
        assertEquals(Boolean.FALSE, ((RuleOperation) operandList.get(1)).getValue());

        //Name the main operation
        final String OPERATION_NAME = "My sweet operation name";
        ro.setName(OPERATION_NAME);
        assertEquals("[Falskt] " + OPERATION_NAME, ro.toString(true, false));
        assertEquals(OPERATION_NAME + " [Falskt]", ro.toString(false, true));
        assertEquals(OPERATION_NAME, ro.toString(false, false));
        assertEquals(OPERATION_NAME + " [Falskt]", ro.toString());
    }

    //mRuleOperator.getOperationResult(mOperands.get(0).getValue(), mRuleOperator.parseValue("10"));

    private List<IEntityDataType> getOperandsAsList2() {
        List<IEntityDataType> operands = new ArrayList<IEntityDataType>();
        operands.add(_unitEntityDataTypeProvider.getUnitDataTypeList().get(2));

        UnitEntityDataType rue = new UnitEntityDataType<Double>("Test Value", 50.7) {
            @Override
            public Double valueOf(String input) {
                return Double.valueOf(input);
            }

            @Override
            public Map<String, Double> getStaticValues() {
                return null;
            }
        };
        operands.add(rue);

        UnitEntityDataType rue2 = new UnitEntityDataType<Float>("Test Value2", 16.8f) {
            @Override
            public Float valueOf(String input) {
                return Float.valueOf(input);
            }

            @Override
            public Map<String, Float> getStaticValues() {
                return null;
            }
        };
        operands.add(rue2);

        return operands;
    }

    public void test_RuleOperator_should_accept_all_3_values_in_list_as_operands() {
        RuleOperator<Number> roWithin =  ruleOperatorsNumeric.get(RuleOperatorType.Within);

        List<IEntityDataType> operands = getOperandsAsList2();

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
        assertTrue(roWithin.getOperationResult(12f, 10f, 13f));
    }

    private Calendar getCalendar(Integer year, Integer month, Integer day, Integer hour, Integer minute, Integer second, Integer ms) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(new java.util.Date());
        if(year != null) cal.set(Calendar.YEAR, year);
        if(month != null) cal.set(Calendar.MONTH, month);
        if(day != null) cal.set(Calendar.DAY_OF_MONTH, day);
        if(hour != null) cal.set(Calendar.HOUR_OF_DAY, hour);
        if(minute != null) cal.set(Calendar.MINUTE, minute);
        if(second != null) cal.set(Calendar.SECOND, second);
        if(ms != null) cal.set(Calendar.MILLISECOND, ms);
        return cal;
    }

    public void testDateTimeParse() {
        RuleOperator<java.util.Date> roAfter = ruleOperatorsDate.get(RuleOperatorType.After);

        //ParseException shall be thrown if the value cannot be parsed.
        try {
            roAfter.parseValue("12.15");
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof ParseException);
        }

        //ParseException shall NOT be thrown.
        try {
            java.util.Date parsedDate = roAfter.parseValue("12:15");
            assertEquals(getCalendar(1970, 0, 1, 12, 15, 0, 0).getTime(), parsedDate);
        } catch (Exception e) {
            assertTrue(e instanceof ParseException);
        }

        //ParseException shall be thrown if the value cannot be parsed.
        try {
            roAfter.parseValue("2014/03/02");
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(e instanceof ParseException);
        }

        //ParseException shall NOT be thrown.
        try {
            java.util.Date parsedDate = roAfter.parseValue("2014-03-02");
            assertEquals(getCalendar(2014, 2, 2, 0, 0, 0, 0).getTime(), parsedDate);
        } catch (Exception e) {
            assertFalse(e instanceof ParseException);
        }

        //ParseException shall NOT be thrown.
        try {
            java.util.Date parsedDate = roAfter.parseValue("20:39 2014-03-02");
            assertEquals(getCalendar(2014, 2, 2, 20, 39, 0, 0).getTime(), parsedDate);
        } catch (Exception e) {
            assertFalse(e instanceof ParseException);
        }

    }
}
