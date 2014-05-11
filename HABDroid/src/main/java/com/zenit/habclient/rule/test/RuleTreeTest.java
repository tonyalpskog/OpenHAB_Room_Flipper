package com.zenit.habclient.rule.test;

import com.zenit.habclient.HABApplication;
import com.zenit.habclient.rule.UnitEntityDataType;
import com.zenit.habclient.UnitEntityDataTypeProvider;
import com.zenit.habclient.rule.IEntityDataType;
import com.zenit.habclient.rule.RuleOperation;
import com.zenit.habclient.rule.RuleOperationProvider;
import com.zenit.habclient.rule.RuleOperatorType;
import com.zenit.habclient.rule.RuleTreeItem;

import org.openhab.habdroid.model.OpenHABWidget;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleTreeTest extends android.test.ApplicationTestCase<HABApplication> {
    private UnitEntityDataTypeProvider _unitEntityDataTypeProvider;
    private HABApplication mHABApplication;
    RuleOperationProvider rop;

    public RuleTreeTest() {
        super(HABApplication.class);
    }

    public void setUp() {
        try {
            super.setUp();
        } catch (Exception e) {
        }
        createApplication();
        mHABApplication = getApplication();

        HttpDataSetup httpDataSetup = new HttpDataSetup();
        httpDataSetup.loadHttpDataFromString(mHABApplication);

        rop = mHABApplication.getRuleOperationProvider();

        _unitEntityDataTypeProvider = new UnitEntityDataTypeProvider();
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
        Float f = Float.valueOf(34.7f);
        Double d = Double.valueOf(34.7);
        assertEquals(true, Math.abs(f.doubleValue() - d.doubleValue()) < 8E-7f);
        assertEquals(true, Math.abs(d.doubleValue() - f.doubleValue()) < 8E-7f);
    }

    private UnitEntityDataType mock_getUnitEntityDataType(OpenHABWidget openHABWidget) {
        UnitEntityDataType rue = null;

        switch(openHABWidget.getItem().getType()) {
            case Switch:
                Boolean aBoolean;
                if(openHABWidget.getItem().getState().equalsIgnoreCase("Undefined"))
                    aBoolean = null;
                else
                    aBoolean = openHABWidget.getItem().getState().equalsIgnoreCase("On");

                rue = new UnitEntityDataType<Boolean>(openHABWidget.getItem().getName(), aBoolean)
                {
                    public String getFormattedString(){
                        return mValue.booleanValue()? "On": "Off";//TODO - Language independent
                    }

                    @Override
                    public Boolean valueOf(String input) {
                        return Boolean.valueOf(input);
                    }
                };
                break;
            case Number:
                Double aNumber;
                if(openHABWidget.getItem().getState().equalsIgnoreCase("Undefined"))
                    aNumber = null;
                else
                    aNumber = Double.valueOf(openHABWidget.getItem().getState());

                rue = new UnitEntityDataType<Double>(openHABWidget.getItem().getName(), aNumber)
                {
                    public String getFormattedString(){
                        return mValue.toString();
                    }

                    @Override
                    public Double valueOf(String input) {
                        return Double.valueOf(input);
                    }
                };
                break;
        }

        return rue;
    }

    private List<IEntityDataType> mock_getOperandsAsList(int operandPairNumber) {
        List<IEntityDataType> operands = new ArrayList<IEntityDataType>();

        switch(operandPairNumber) {
            case 1:
                //Switch
                operands.add(mock_getUnitEntityDataType(mHABApplication.getOpenHABWidgetProvider().getWidgetByID("GF_Kitchen_0")));
                operands.add(mock_getUnitEntityDataType(mHABApplication.getOpenHABWidgetProvider().getWidgetByID("FF_Bath_1")));
                break;

            case 2:
                //Number
                operands.add(mock_getUnitEntityDataType(mHABApplication.getOpenHABWidgetProvider().getWidgetByID("FF_Bed_3")));
                operands.add(mock_getUnitEntityDataType(mHABApplication.getOpenHABWidgetProvider().getWidgetByID("GF_Toilet_4")));
                break;
        }

        return operands;
    }

    public RuleOperation mock_getRuleOperation(int operationNumber) {
        RuleOperation result = null;
        OpenHABWidget widget;

        switch(operationNumber) {
            case 1:
                widget = mHABApplication.getOpenHABWidgetProvider().getWidgetByID("GF_Kitchen_0");
                result = new RuleOperation(rop.getUnitRuleOperator(widget).get(RuleOperatorType.Equal), mock_getOperandsAsList(1));
                // Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF]
                break;
            case 2:
                widget = mHABApplication.getOpenHABWidgetProvider().getWidgetByID("FF_Bath_1");
                result = new RuleOperation(rop.getUnitRuleOperator(widget).get(RuleOperatorType.NotEqual), mock_getOperandsAsList(1));
                // Light_GF_Kitchen_Ceiling [OFF] != Light_FF_Bath_Mirror [OFF]
                break;
            case 3:
                widget = mHABApplication.getOpenHABWidgetProvider().getWidgetByID("FF_Bed_3");
                result = new RuleOperation(rop.getUnitRuleOperator(widget).get(RuleOperatorType.LessThan), mock_getOperandsAsList(2));
                // Temperature_FF_Bed [19.20] < Temperature_GF_Toilet [21.50]
                break;
            case 4:
                widget = mHABApplication.getOpenHABWidgetProvider().getWidgetByID("GF_Toilet_4");
                result = new RuleOperation(rop.getUnitRuleOperator(widget).get(RuleOperatorType.MoreThan), mock_getOperandsAsList(2));
                // Temperature_FF_Bed [19.20] > Temperature_GF_Toilet [21.50]
                break;
        }

        return result;
    }

    public void test_toString() {
        RuleTreeItem rt = mock_getRuleOperation(1).getRuleTreeItem(0);
        assertEquals("Light_GF_Kitchen_Ceiling = Light_FF_Bath_Mirror", rt.toString());
    }

//    public void test_toString() {
//        RuleTreeItem rt = new RuleTreeItem(0, mock_getRuleOperation(1));
//        assertEquals("Light_GF_Kitchen_Ceiling = Off", rt.toString());
//    }
}
