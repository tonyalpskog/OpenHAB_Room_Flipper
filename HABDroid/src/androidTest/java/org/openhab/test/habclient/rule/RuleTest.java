package org.openhab.test.habclient.rule;

import org.openhab.habclient.HABApplication;
import org.openhab.rule.OnValueChangedListener;
import org.openhab.habclient.UnitEntityDataTypeProvider;
import org.openhab.habclient.rule.IEntityDataType;
import org.openhab.habclient.rule.IOperator;
import org.openhab.habclient.rule.RuleOperation;
import org.openhab.habclient.rule.RuleOperationProvider;
import org.openhab.habclient.rule.RuleOperator;
import org.openhab.habclient.rule.RuleOperatorType;
import org.openhab.habclient.rule.UnitEntityDataType;

import org.openhab.domain.model.OpenHABWidget;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
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
    private HashMap<RuleOperatorType, RuleOperator<Boolean>> ruleOperatorsBoolean;
    private HashMap<RuleOperatorType, RuleOperator<java.util.Date>> ruleOperatorsDate;
    private UnitEntityDataTypeProvider _unitEntityDataTypeProvider;
//    @Mock
    private HABApplication mHABApplication;
//    @Mock
//    private OpenHABWidgetControl mOpenHABWidgetControl_Mock;

    public RuleTest() {
        super(HABApplication.class);
    }

    public void setUp() {
        try {
            super.setUp();
        } catch (Exception e) {
        }
        initMocks(this);
        createApplication();
        mHABApplication = getApplication();
//        mOpenHABWidgetControl_Mock = new OpenHABWidgetControl(mContext);
//        doAnswer(new Answer() {
//            @Override
//            public Object answer(InvocationOnMock invocation) throws Throwable {
//                Object[] args = invocation.getArguments();
//                mHABApplication.getOpenHABWidgetProvider().setOpenHABItem(
//                        mHABApplication.getOpenHABWidgetProvider().getWidgetByItemName(((OpenHABItem) args[0]).getName()).getItem()
//                );
//                return null;
//            }
//        }).when(mOpenHABWidgetControl_Mock).sendItemCommand((OpenHABItem)anyObject(), anyString());

//        when(mHABApplication.getOpenHABWidgetProvider()).thenReturn(mOpenHABWidgetControl_Mock);

//        when(mHABApplication.toString()).thenReturn("Mockoto_string");

        HttpDataSetup httpDataSetup = new HttpDataSetup(mHABApplication);
        httpDataSetup.loadHttpDataFromString();

        RuleOperationProvider rop = mHABApplication.getRuleOperationProvider();

        ruleOperatorsNumeric = (HashMap<RuleOperatorType, RuleOperator<Number>>) rop.mOperatorHash.get(Number.class);
        ruleOperatorsBoolean = (HashMap<RuleOperatorType, RuleOperator<Boolean>>) rop.mOperatorHash.get(Boolean.class);
        ruleOperatorsDate = (HashMap<RuleOperatorType, RuleOperator<java.util.Date>>) rop.mOperatorHash.get(java.util.Date.class);

        _unitEntityDataTypeProvider = new UnitEntityDataTypeProvider();
    }

    //================================= UNITS ===================================


//    ===================================================================================================

    public void testSimple1() throws Exception {
        final int expected = 3;
        final int reality = 3;
        assertEquals(expected, reality);
    }

    public void testSimple2() {
        assertTrue(true);
    }

    public void testGetWidgetById() {
        OpenHABWidget unit = mHABApplication.getOpenHABWidgetProvider2().getWidgetByID("GF_Kitchen_0");
        assertEquals("Ceiling", unit.getLabel());
        assertEquals("Light_GF_Kitchen_Ceiling", unit.getItem().getName());
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
            assertTrue(true);
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

        //Boolean true OR false OR true = True
        assertTrue(roOr.getOperationResult(Boolean.valueOf(true), Boolean.valueOf(false), Boolean.valueOf(true)));

        //Boolean false OR true OR false = True
        assertTrue(roOr.getOperationResult(Boolean.valueOf(false), Boolean.valueOf(true), Boolean.valueOf(false)));

        //Boolean false OR false OR false = False
        assertFalse(roOr.getOperationResult(Boolean.valueOf(false), Boolean.valueOf(false), Boolean.valueOf(false)));
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

        List<IEntityDataType> operands = getOperandsAsList();

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

        List<IEntityDataType> operands = getOperandsAsList();

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

        List<IEntityDataType> operands = getOperandsAsList();

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

        assertEquals(false, roA.getValue().booleanValue() && roB.getValue().booleanValue());

        //Test if a value change on first operator in second operation will update itself and the sub-operations
        UnitEntityDataType operand1 = ((UnitEntityDataType)operands2.get(0));
        assertEquals(50.7d, operand1.getValue());
        operand1.setValue(59.2d);
        assertEquals(59.2d, operand1.getValue());
        ((OnValueChangedListener)operands2.get(0)).onValueChanged(((OnValueChangedListener) operands2.get(0)).getDataSourceId(), "43.5");
        assertEquals(43.5d, operand1.getValue());
        assertEquals(true, roA.getValue().booleanValue());
        assertEquals(true, roB.getValue().booleanValue());
        assertEquals(true, roA.getValue().booleanValue() && roB.getValue().booleanValue());
    }

    private List<IEntityDataType> getOperandsAsList3(int operandPairNumber) {
        List<IEntityDataType> operands = new ArrayList<IEntityDataType>();

        switch(operandPairNumber) {
            case 1:
                //Switch
                operands.add(UnitEntityDataType.getUnitEntityDataType(mHABApplication.getOpenHABWidgetProvider2().getWidgetByID("GF_Kitchen_0")));
                operands.add(UnitEntityDataType.getUnitEntityDataType(mHABApplication.getOpenHABWidgetProvider2().getWidgetByID("FF_Bath_1")));
                break;

            case 2:
                //Number
                operands.add(UnitEntityDataType.getUnitEntityDataType(mHABApplication.getOpenHABWidgetProvider2().getWidgetByID("FF_Bed_3")));
                operands.add(UnitEntityDataType.getUnitEntityDataType(mHABApplication.getOpenHABWidgetProvider2().getWidgetByID("GF_Toilet_4")));
                break;
        }

        return operands;
    }

    public void test_Create_RuleOperation_object_from_provider_units_and_validate_operation_result() {
        RuleOperationProvider rop = mHABApplication.getRuleOperationProvider();

        OpenHABWidget widget = mHABApplication.getOpenHABWidgetProvider2().getWidgetByID("GF_Kitchen_0");
        RuleOperation roA = new RuleOperation(rop.getUnitRuleOperator(widget).get(RuleOperatorType.Equal), getOperandsAsList3(1));
        assertEquals("Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF]", roA.toString());
        assertEquals(true, roA.getValue().booleanValue());

        widget = mHABApplication.getOpenHABWidgetProvider2().getWidgetByID("FF_Bath_1");
        roA = new RuleOperation(rop.getUnitRuleOperator(widget).get(RuleOperatorType.NotEqual), getOperandsAsList3(1));
        assertEquals("Light_GF_Kitchen_Ceiling [OFF] != Light_FF_Bath_Mirror [OFF]", roA.toString());
        assertEquals(false, roA.getValue().booleanValue());

        widget = mHABApplication.getOpenHABWidgetProvider2().getWidgetByID("FF_Bed_3");
        RuleOperation roB = new RuleOperation(rop.getUnitRuleOperator(widget).get(RuleOperatorType.LessThan), getOperandsAsList3(2));
        assertEquals("Temperature_FF_Bed [19.2] < Temperature_GF_Toilet [21.5]", roB.toString());
        assertEquals(true, roB.getValue().booleanValue());

        widget = mHABApplication.getOpenHABWidgetProvider2().getWidgetByID("GF_Toilet_4");
        roB = new RuleOperation(rop.getUnitRuleOperator(widget).get(RuleOperatorType.MoreThan), getOperandsAsList3(2));
        assertEquals("Temperature_FF_Bed [19.2] > Temperature_GF_Toilet [21.5]", roB.toString());
        assertEquals(false, roB.getValue().booleanValue());

        assertEquals(false, roA.getValue() && roB.getValue());
    }

    private List<IEntityDataType> getListOfRuleOperationsForTest() {
        RuleOperationProvider rop = mHABApplication.getRuleOperationProvider();

        OpenHABWidget widget = mHABApplication.getOpenHABWidgetProvider2().getWidgetByID("GF_Kitchen_0");
        RuleOperation roA = new RuleOperation(rop.getUnitRuleOperator(widget).get(RuleOperatorType.Equal), getOperandsAsList3(1));

        widget = mHABApplication.getOpenHABWidgetProvider2().getWidgetByID("FF_Bed_3");
        RuleOperation roB = new RuleOperation(rop.getUnitRuleOperator(widget).get(RuleOperatorType.LessThan), getOperandsAsList3(2));

        List<IEntityDataType> operandList = new ArrayList<IEntityDataType>();
        operandList.add(roA);
        operandList.add(roB);

        return operandList;
    }

    private final String LEFT_OPERAND_NAME = "Operation as left operand";
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
        assertTrue(roWithin.getOperationResult(Float.valueOf(12), Float.valueOf(10), Float.valueOf(13)));
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
            assertFalse(roAfter.getOperationResult(roAfter.parseValue("11:30 2014-03-24"), roAfter.parseValue("10:15 2014-03-25")));
        } catch (Exception e) {
            assertEquals("Should have returned an instance of java.util.Date", e.toString()/*getClass().getSimpleName()*/);
        }

        //Date Sun 11:30 after Mon 10:15 = False
        try {
            assertFalse(roAfter.getOperationResult(roAfter.parseValue("11:30 2014-03-23"), roAfter.parseValue("10:15 2014-03-24")));
        } catch (Exception e) {
            assertEquals("Should have returned an instance of java.util.Date", e.toString()/*getClass().getSimpleName()*/);
        }

        //Date Sat 11:30 after Mon 10:15 = False
        try {
            assertFalse(roAfter.getOperationResult(roAfter.parseValue("11:30 2014-03-22"), roAfter.parseValue("10:15 2014-03-24")));
        } catch (Exception e) {
            assertEquals("Should have returned an instance of java.util.Date", e.toString()/*getClass().getSimpleName()*/);
        }

        //Date Mon 10:30 after Sun 11:15 = False
        try {
            assertFalse(roAfter.getOperationResult(roAfter.parseValue("10:30 2014-03-24"), roAfter.parseValue("11:15 2014-03-23")));
        } catch (Exception e) {
            assertEquals("Should have returned an instance of java.util.Date", e.toString()/*getClass().getSimpleName()*/);
        }
    }

//    public void test_XXX() {
//        assertEquals("ON", mHABApplication.getOpenHABWidgetProvider().getWidgetByItemName("Light_FF_Child_Ceiling").getItem().getState());
//        mOpenHABWidgetControl_Mock.sendItemCommand("Light_FF_Child_Ceiling", "OFF");
//        assertEquals("OFF", mHABApplication.getOpenHABWidgetProvider().getWidgetByItemName("Light_FF_Child_Ceiling").getItem().getState());
//    }

//    public void test_YYY() {
//        assertEquals("Mockoto_string", mHABApplication.toString());
//    }
}
