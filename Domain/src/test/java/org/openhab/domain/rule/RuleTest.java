package org.openhab.domain.rule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openhab.domain.DocumentFactory;
import org.openhab.domain.IDocumentFactory;
import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.IPopularNameProvider;
import org.openhab.domain.IUnitEntityDataTypeProvider;
import org.openhab.domain.OpenHABWidgetProvider;
import org.openhab.domain.PopularNameProvider;
import org.openhab.domain.UnitEntityDataTypeProvider;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.rule.operators.AfterDateTimeRuleOperator;
import org.openhab.domain.rule.operators.EqualNumberRuleOperator;
import org.openhab.domain.rule.operators.LessThanNumberRuleOperator;
import org.openhab.domain.rule.operators.RuleOperator;
import org.openhab.domain.rule.operators.WithinNumberRuleOperator;
import org.openhab.domain.util.IColorParser;
import org.openhab.domain.util.ILogger;
import org.openhab.domain.util.RegularExpression;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleTest {
    private IRuleOperationProvider mRuleOperationProvider;
    private IOpenHABWidgetProvider mWidgetProvider;
    private IUnitEntityDataTypeProvider mIUnitEntityDataTypeProvider;

    @Before
    public void setUp() throws Exception {

        final ILogger logger = mock(ILogger.class);
        final IColorParser colorParser = mock(IColorParser.class);
        final RegularExpression regularExpression = new RegularExpression();
        final IPopularNameProvider popularNameProvider = new PopularNameProvider();
        mWidgetProvider = new OpenHABWidgetProvider(regularExpression, logger, popularNameProvider);
        final IDocumentFactory documentFactory = new DocumentFactory();
        final HttpDataSetup httpDataSetup = new HttpDataSetup(logger, colorParser, documentFactory);
        mWidgetProvider.setOpenHABWidgets(httpDataSetup.loadTestData());
        mIUnitEntityDataTypeProvider = new UnitEntityDataTypeProvider(mWidgetProvider);
        mRuleOperationProvider = new RuleOperationProvider();
    }

    //================================= UNITS ===================================

    @Test
    public void testSimple1() throws Exception {
        final int expected = 3;
        final int reality = 3;
        Assert.assertEquals(expected, reality);
    }

    @Test
    public void testSimple2() {
        Assert.assertTrue(true);
    }

    @Test
    public void testGetWidgetById() {
        OpenHABWidget unit = mWidgetProvider.getWidgetByID("GF_Kitchen_0");
        Assert.assertEquals("Ceiling", unit.getLabel());
        Assert.assertEquals("Light_GF_Kitchen_Ceiling", unit.getItem().getName());
    }

    @Test
    public void testMathEpsilonDiff() {
        Float f = 34.7f;
        Double d = 34.7;
        Assert.assertEquals(true, Math.abs(f.doubleValue() - d) < 8E-7f);
        Assert.assertEquals(true, Math.abs(d - f.doubleValue()) < 8E-7f);
    }

//    Calculate EPSILON
//    public void testSimple4() {
//        Float f = Float.valueOf(34.7f);
//        Double d = Double.valueOf(34.7);
//        assertEquals(0, Math.abs(f.doubleValue() - d.doubleValue()));
//    }

    @Test
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

        Assert.assertTrue(rue.getValue() != null);
        Assert.assertEquals(50.7, rue.getValue());
        Assert.assertEquals("50.7", rue.getFormattedString());
        Assert.assertEquals("Test Value", rue.getName());

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

        Assert.assertTrue(rue.getValue() != null);
        Assert.assertEquals(50.7, rue.getValue());
        Assert.assertEquals("50.7%Rh", rue.getFormattedString());
        Assert.assertEquals("Test Value", rue.getName());
    }

    private List<IEntityDataType> getOperandsAsList() {
        List<IEntityDataType> operands = new ArrayList<IEntityDataType>();
        operands.add(mIUnitEntityDataTypeProvider.getUnitDataTypeList().get(2));

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

    @Test
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
            Assert.assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertEquals("This should not happen", e.toString()/*getClass().getDescription()*/);
            //assertFalse(e instanceof IllegalArgumentException);
        }
    }

    @Test
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

                Assert.assertEquals(101.4f, result);
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
            Assert.assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertEquals("This should not happen", e.toString()/*getClass().getDescription()*/);
            //assertFalse(e instanceof IllegalArgumentException);
        }
    }

    @Test
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
            Assert.assertTrue(false);
        } catch (Exception e) {
            Assert.assertEquals("java.lang.ClassCastException", e.getClass().getName());
        }
    }

    @Test
    public void test_RuleOperator_should_accept_both_values_in_list_as_operands() {
        IOperator<Number> roEqual = new EqualNumberRuleOperator();

        List<IEntityDataType> operands = getOperandsAsList();

        Assert.assertTrue(operands.get(0) != null);
        Assert.assertTrue(operands.get(1) != null);

        Assert.assertTrue(operands.get(0).getValue() != null);
        Assert.assertTrue(operands.get(1).getValue() != null);

        //IllegalArgumentException shall be thrown if there isn´t exactly 3 numbers of operation values.
        try {
            roEqual.getOperationResult(Double.valueOf(operands.get(0).getValue().toString()), Double.valueOf(operands.get(1).getValue().toString()));
            roEqual.getOperationResult(roEqual.parseValue(operands.get(0).getValue().toString()), roEqual.parseValue(operands.get(1).getValue().toString()));
            roEqual.getOperationResult(operands);
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.assertEquals("Something whent wrong", e.toString());
        }

        //IllegalArgumentException shall be thrown if there isn´t exactly 3 numbers of operation values.
        try {
            roEqual.getOperationResult(Double.valueOf(operands.get(0).getValue().toString()), Double.valueOf(operands.get(1).getValue().toString()));
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.assertEquals("This should not happen", e.getClass().getName());
        }
    }

    @Test
    public void test_Create_RuleOperation_object_and_validate_operation_result() {
        Assert.assertEquals("Humidity percentage", mIUnitEntityDataTypeProvider.getUnitDataTypeList().get(2).getName());
        Assert.assertEquals(50.7, mIUnitEntityDataTypeProvider.getUnitDataTypeList().get(2).getValue());

        List<IEntityDataType> operands = getOperandsAsList();

        //First operation (Rule A)
        RuleOperator<Number> operator =  new EqualNumberRuleOperator();

        RuleOperation roA = new RuleOperation(operator, operands);
        Assert.assertEquals("Humidity percentage [50.7%Rh] = Test Value [50.7]", roA.toString());
        Assert.assertEquals(true, roA.getValue().getValue());

        //Second operation (Rule B)
        RuleOperator<Number> operator2 =  new LessThanNumberRuleOperator();

        List<IEntityDataType> operands2 = getOperandsAsList();
        RuleOperation roB = new RuleOperation(operator2, operands2);
        Assert.assertEquals("Humidity percentage [50.7%Rh] < Test Value [50.7]", roB.toString());
        Assert.assertEquals(false, roB.getValue().getValue());

        Assert.assertEquals(false, roA.getValue().getValue() && roB.getValue().getValue());

        //Test if a value change on first operator in second operation will update itself and the sub-operations
        UnitEntityDataType operand1 = ((UnitEntityDataType)operands2.get(0));
        Assert.assertEquals(50.7d, operand1.getValue());
        operand1.setValue(59.2d);
        Assert.assertEquals(59.2d, operand1.getValue());
        ((OnValueChangedListener)operands2.get(0)).onValueChanged(operands2.get(0).getDataSourceId(), "43.5");
        Assert.assertEquals(43.5d, operand1.getValue());
        Assert.assertEquals(true, roA.getValue().getValue());
        Assert.assertEquals(true, roB.getValue().getValue());
        Assert.assertEquals(true, roA.getValue().getValue() && roB.getValue().getValue());
    }

    private List<IEntityDataType> getOperandsAsList3(int operandPairNumber) {
        List<IEntityDataType> operands = new ArrayList<IEntityDataType>();

        switch(operandPairNumber) {
            case 1:
                //Switch
                operands.add(mIUnitEntityDataTypeProvider.getUnitEntityDataType(mWidgetProvider.getWidgetByID("GF_Kitchen_0")));
                operands.add(mIUnitEntityDataTypeProvider.getUnitEntityDataType(mWidgetProvider.getWidgetByID("FF_Bath_1")));
                break;

            case 2:
                //Number
                operands.add(mIUnitEntityDataTypeProvider.getUnitEntityDataType(mWidgetProvider.getWidgetByID("FF_Bed_3")));
                operands.add(mIUnitEntityDataTypeProvider.getUnitEntityDataType(mWidgetProvider.getWidgetByID("GF_Toilet_4")));
                break;
        }

        return operands;
    }

    @Test
    public void test_Create_RuleOperation_object_from_provider_units_and_validate_operation_result() {
        OpenHABWidget widget = mWidgetProvider.getWidgetByID("GF_Kitchen_0");
        RuleOperation roA = new RuleOperation(mRuleOperationProvider.getRuleOperator(widget, RuleOperatorType.Equal), getOperandsAsList3(1));
        Assert.assertEquals("Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF]", roA.toString());
        Assert.assertEquals(true, roA.getValue().getValue());

        widget = mWidgetProvider.getWidgetByID("FF_Bath_1");
        roA = new RuleOperation(mRuleOperationProvider.getRuleOperator(widget, RuleOperatorType.NotEqual), getOperandsAsList3(1));
        Assert.assertEquals("Light_GF_Kitchen_Ceiling [OFF] != Light_FF_Bath_Mirror [OFF]", roA.toString());
        Assert.assertEquals(false, roA.getValue().getValue());

        widget = mWidgetProvider.getWidgetByID("FF_Bed_3");
        RuleOperation roB = new RuleOperation(mRuleOperationProvider.getRuleOperator(widget, RuleOperatorType.LessThan), getOperandsAsList3(2));
        Assert.assertEquals("Temperature_FF_Bed [19.2] < Temperature_GF_Toilet [21.5]", roB.toString());
        Assert.assertEquals(true, roB.getValue().getValue());

        widget = mWidgetProvider.getWidgetByID("GF_Toilet_4");
        roB = new RuleOperation(mRuleOperationProvider.getRuleOperator(widget, RuleOperatorType.MoreThan), getOperandsAsList3(2));
        Assert.assertEquals("Temperature_FF_Bed [19.2] > Temperature_GF_Toilet [21.5]", roB.toString());
        Assert.assertEquals(false, roB.getValue().getValue());

        Assert.assertEquals(false, roA.getValue().getValue() && roB.getValue().getValue());
    }

    private List<IEntityDataType> getListOfRuleOperationsForTest() {
        final IRuleOperationProvider rop = new RuleOperationProvider();

        OpenHABWidget widget = mWidgetProvider.getWidgetByID("GF_Kitchen_0");
        RuleOperation roA = new RuleOperation(rop.getRuleOperator(widget, RuleOperatorType.Equal), getOperandsAsList3(1));

        widget = mWidgetProvider.getWidgetByID("FF_Bed_3");
        RuleOperation roB = new RuleOperation(rop.getRuleOperator(widget, RuleOperatorType.LessThan), getOperandsAsList3(2));

        List<IEntityDataType> operandList = new ArrayList<IEntityDataType>();
        operandList.add(roA);
        operandList.add(roB);

        return operandList;
    }

    private static final String LEFT_OPERAND_NAME = "Operation as left operand";
    private RuleOperation getNestedRuleOperationForTest(boolean nameTheLeftOperand, RuleOperatorType ruleType) {
        RuleOperationProvider rop = new RuleOperationProvider();

        List<IEntityDataType> operandList = getListOfRuleOperationsForTest();
        if(nameTheLeftOperand) ((RuleOperation) operandList.get(0)).setName("Operation as left operand");

        return new RuleOperation(rop.getRuleOperator(operandList.get(0).getDataType(), ruleType), operandList);
    }

    @Test
    public void test_RuleOperation_toString_methods_on_nameless_operation_and_sub_operations() {
        RuleOperation ro = getNestedRuleOperationForTest(false, RuleOperatorType.And);

        Assert.assertEquals("(Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF]) AND (Temperature_FF_Bed [19.2] < Temperature_GF_Toilet [21.5])", ro.toString());
        Assert.assertEquals("[Sant] (Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF]) AND (Temperature_FF_Bed [19.2] < Temperature_GF_Toilet [21.5])", ro.toString(true, false));
        Assert.assertEquals("(Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF]) AND (Temperature_FF_Bed [19.2] < Temperature_GF_Toilet [21.5]) [Sant]", ro.toString(false, true));
        Assert.assertEquals("(Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF]) AND (Temperature_FF_Bed [19.2] < Temperature_GF_Toilet [21.5])", ro.toString(false, false));
        Assert.assertEquals("[Sant] (Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF]) AND (Temperature_FF_Bed [19.2] < Temperature_GF_Toilet [21.5]) [Sant]", ro.toString(true, true));
    }

    @Test
    public void test_RuleOperation_toString_methods_on_named_operation_and_mixed_sub_operations() {
        RuleOperation ro = getNestedRuleOperationForTest(true, RuleOperatorType.NotEqual);

        Assert.assertEquals(LEFT_OPERAND_NAME + " [Sant] != (Temperature_FF_Bed [19.2] < Temperature_GF_Toilet [21.5])", ro.toString());
        Assert.assertEquals("[Falskt] " + LEFT_OPERAND_NAME + " [Sant] != (Temperature_FF_Bed [19.2] < Temperature_GF_Toilet [21.5])", ro.toString(true, false));

        //Name the main operation
        final String OPERATION_NAME = "My sweet operation name";
        ro.setName(OPERATION_NAME);
        Assert.assertEquals("[Falskt] " + OPERATION_NAME, ro.toString(true, false));
        Assert.assertEquals(OPERATION_NAME + " [Falskt]", ro.toString(false, true));
        Assert.assertEquals(OPERATION_NAME, ro.toString(false, false));
        Assert.assertEquals(OPERATION_NAME + " [Falskt]", ro.toString());
    }

    @Test
    public void test_RuleOperation_toString_methods_on_null_objects() {
        List<IEntityDataType> operandList = getListOfRuleOperationsForTest();
        ((RuleOperation) operandList.get(0)).setRuleOperator(null);
        ((RuleOperation) operandList.get(1)).setOperand(1, null);
        RuleOperation ro = new RuleOperation(mRuleOperationProvider.getRuleOperator(operandList.get(0).getDataType(), RuleOperatorType.Or), operandList);

        Assert.assertEquals("(Light_GF_Kitchen_Ceiling [OFF] " + RuleOperator.MISSING_OPERATOR + ") OR (Temperature_FF_Bed [19.2] < " + RuleOperatorType.MISSING_OPERAND + ")", ro.toString());
        Assert.assertEquals(Boolean.FALSE, ((RuleOperation) operandList.get(0)).getValue().getValue());
        Assert.assertEquals(Boolean.FALSE, ((RuleOperation) operandList.get(1)).getValue().getValue());

        ((RuleOperation) operandList.get(0)).setName("Operation as left operand");
        ro = new RuleOperation(mRuleOperationProvider.getRuleOperator(operandList.get(0).getDataType(), RuleOperatorType.Or), operandList);

        Assert.assertEquals("Operation as left operand <Incomplete> [Falskt] OR (Temperature_FF_Bed [19.2] < " + RuleOperatorType.MISSING_OPERAND + ")", ro.toString());
        Assert.assertEquals(Boolean.FALSE, ((RuleOperation) operandList.get(0)).getValue().getValue());
        Assert.assertEquals(Boolean.FALSE, ((RuleOperation) operandList.get(1)).getValue().getValue());

        ((RuleOperation) operandList.get(0)).setOperand(1, null);
        ro = new RuleOperation(mRuleOperationProvider.getRuleOperator(operandList.get(0).getDataType(), RuleOperatorType.Or), operandList);

        Assert.assertEquals("Operation as left operand <Incomplete> [Falskt] OR (Temperature_FF_Bed [19.2] < " + RuleOperatorType.MISSING_OPERAND + ")", ro.toString());
        Assert.assertEquals(Boolean.FALSE, ((RuleOperation) operandList.get(0)).getValue().getValue());
        Assert.assertEquals(Boolean.FALSE, ((RuleOperation) operandList.get(1)).getValue().getValue());

        ((RuleOperation) operandList.get(0)).setOperand(0, null);
        ro = new RuleOperation(mRuleOperationProvider.getRuleOperator(operandList.get(0).getDataType(), RuleOperatorType.Or), operandList);

        Assert.assertEquals("Operation as left operand <Incomplete> [Falskt] OR (Temperature_FF_Bed [19.2] < " + RuleOperatorType.MISSING_OPERAND + ")", ro.toString());
        Assert.assertEquals(Boolean.FALSE, ((RuleOperation) operandList.get(0)).getValue().getValue());
        Assert.assertEquals(Boolean.FALSE, ((RuleOperation) operandList.get(1)).getValue().getValue());

        //Name the main operation
        final String OPERATION_NAME = "My sweet operation name";
        ro.setName(OPERATION_NAME);
        Assert.assertEquals("[Falskt] " + OPERATION_NAME, ro.toString(true, false));
        Assert.assertEquals(OPERATION_NAME + " [Falskt]", ro.toString(false, true));
        Assert.assertEquals(OPERATION_NAME, ro.toString(false, false));
        Assert.assertEquals(OPERATION_NAME + " [Falskt]", ro.toString());
    }

    //mRuleOperator.getOperationResult(mOperands.get(0).getValue(), mRuleOperator.parseValue("10"));

    private List<IEntityDataType> getOperandsAsList2() {
        List<IEntityDataType> operands = new ArrayList<IEntityDataType>();
        operands.add(mIUnitEntityDataTypeProvider.getUnitDataTypeList().get(2));

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

    @Test
    public void test_RuleOperator_should_accept_all_3_values_in_list_as_operands() {
        RuleOperator<Number> roWithin =  new WithinNumberRuleOperator();

        List<IEntityDataType> operands = getOperandsAsList2();

        //IllegalArgumentException shall be thrown if there isn´t exactly 3 numbers of operation values.
        try {
            roWithin.getOperationResult((Number)operands.get(0).getValue(), (Number)operands.get(1).getValue(), (Number)operands.get(2).getValue());
            roWithin.getOperationResult(operands);
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.assertEquals("This should not happen", e.getClass().getName());
            //assertFalse(e instanceof IllegalArgumentException);
        }

        //Float 12 within 10 and 13 = True
        Assert.assertTrue(roWithin.getOperationResult(12f, 10f, 13f));
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

    @Test
    public void testDateTimeParse() {
        RuleOperator<java.util.Date> roAfter = new AfterDateTimeRuleOperator();

        //ParseException shall be thrown if the value cannot be parsed.
        try {
            roAfter.parseValue("12.15");
            Assert.assertFalse(true);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ParseException);
        }

        //ParseException shall NOT be thrown.
        try {
            java.util.Date parsedDate = roAfter.parseValue("12:15");
            Assert.assertEquals(getCalendar(1970, 0, 1, 12, 15, 0, 0).getTime(), parsedDate);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ParseException);
        }

        //ParseException shall be thrown if the value cannot be parsed.
        try {
            roAfter.parseValue("2014/03/02");
            Assert.assertFalse(true);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ParseException);
        }

        //ParseException shall NOT be thrown.
        try {
            java.util.Date parsedDate = roAfter.parseValue("2014-03-02");
            Assert.assertEquals(getCalendar(2014, 2, 2, 0, 0, 0, 0).getTime(), parsedDate);
        } catch (Exception e) {
            Assert.assertFalse(e instanceof ParseException);
        }

        //ParseException shall NOT be thrown.
        try {
            java.util.Date parsedDate = roAfter.parseValue("20:39 2014-03-02");
            Assert.assertEquals(getCalendar(2014, 2, 2, 20, 39, 0, 0).getTime(), parsedDate);
        } catch (Exception e) {
            Assert.assertFalse(e instanceof ParseException);
        }

    }
}
