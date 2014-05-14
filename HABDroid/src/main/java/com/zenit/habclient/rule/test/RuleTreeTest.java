package com.zenit.habclient.rule.test;

import com.zenit.habclient.HABApplication;
import com.zenit.habclient.UnitEntityDataTypeProvider;
import com.zenit.habclient.rule.IEntityDataType;
import com.zenit.habclient.rule.RuleOperation;
import com.zenit.habclient.rule.RuleOperationProvider;
import com.zenit.habclient.rule.RuleOperator;
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
    HttpDataSetup mHttpDataSetup;

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

        mHttpDataSetup = new HttpDataSetup(mHABApplication);
        mHttpDataSetup.loadHttpDataFromString();

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

    private List<IEntityDataType> mock_getOperandsAsList(int operandPairNumber) {
        List<IEntityDataType> operands = new ArrayList<IEntityDataType>();

        switch(operandPairNumber) {
            case 1:
                //Switch
                operands.add(mHttpDataSetup.getUnitEntityDataType(mHABApplication.getOpenHABWidgetProvider().getWidgetByID("GF_Kitchen_0")));
                operands.add(mHttpDataSetup.getUnitEntityDataType(mHABApplication.getOpenHABWidgetProvider().getWidgetByID("FF_Bath_1")));
                break;

            case 2:
                //Number
                operands.add(mHttpDataSetup.getUnitEntityDataType(mHABApplication.getOpenHABWidgetProvider().getWidgetByID("FF_Bed_3")));
                operands.add(mHttpDataSetup.getUnitEntityDataType(mHABApplication.getOpenHABWidgetProvider().getWidgetByID("GF_Toilet_4")));
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

    public void test_toString_from_a_two_operation_operands_operation() {
        RuleOperation ron1 = mock_getRuleOperation(1);
        RuleTreeItem rti1 = new RuleTreeItem(0, ron1.getRuleTreeItem(0).toString());
        assertEquals("Light_GF_Kitchen_Ceiling = Light_FF_Bath_Mirror", rti1.toString());
        assertEquals(0, rti1.getPosition());
        assertEquals(0, rti1.getChildren().size());
        assertEquals(true, ron1.getValue().booleanValue());

        RuleOperation ron2 = mock_getRuleOperation(4);
        RuleTreeItem rti2 = new RuleTreeItem(1, ron2.getRuleTreeItem(1).toString());
        assertEquals("Temperature_FF_Bed > Temperature_GF_Toilet", rti2.toString());
        assertEquals(1, rti2.getPosition());
        assertEquals(0, rti2.getChildren().size());
        assertEquals(false, ron2.getValue().booleanValue());

        RuleOperator ror =  mHABApplication.getRuleOperationProvider().getUnitRuleOperatorHash(Boolean.class).get(RuleOperatorType.And);
        List<RuleOperation> operandList = new ArrayList<RuleOperation>();
        operandList.add(ron1);
        operandList.add(ron2);
        RuleOperation mainOperation = new RuleOperation(ror, operandList);
        RuleTreeItem mainRuleTreeItem = mainOperation.getRuleTreeItem(3);
        assertEquals(3, mainRuleTreeItem.getPosition());
        assertEquals(false, mainOperation.getValue().booleanValue());
        assertEquals("(Light_GF_Kitchen_Ceiling = Light_FF_Bath_Mirror) AND (Temperature_FF_Bed > Temperature_GF_Toilet)", mainOperation.toString());
        assertEquals("(Light_GF_Kitchen_Ceiling = Light_FF_Bath_Mirror) AND (Temperature_FF_Bed > Temperature_GF_Toilet)", mainRuleTreeItem.toString());
        assertEquals(2, mainRuleTreeItem.getChildren().size());
        assertEquals("Light_GF_Kitchen_Ceiling = Light_FF_Bath_Mirror", mainRuleTreeItem.getChildren().get(0).toString());
        assertEquals(0, mainRuleTreeItem.getChildren().get(0).getPosition());
        assertEquals(0, mainRuleTreeItem.getChildren().get(0).getChildren().size());
        assertEquals("Temperature_FF_Bed > Temperature_GF_Toilet", mainRuleTreeItem.getChildren().get(1).toString());
        assertEquals(1, mainRuleTreeItem.getChildren().get(1).getPosition());
        assertEquals(0, mainRuleTreeItem.getChildren().get(1).getChildren().size());

        operandList.clear();
        ron1.setName("First_Operation");
        ron2.setName("Second_Operation");
        operandList.add(ron1);
        operandList.add(ron2);
        mainOperation = new RuleOperation(ror, operandList);
        mainRuleTreeItem = mainOperation.getRuleTreeItem(3);
        assertEquals(3, mainRuleTreeItem.getPosition());
        assertEquals(false, mainOperation.getValue().booleanValue());
        assertEquals("First_Operation AND Second_Operation", mainOperation.toString());
        assertEquals("First_Operation AND Second_Operation", mainRuleTreeItem.toString());
        assertEquals(2, mainRuleTreeItem.getChildren().size());
        assertEquals("First_Operation", mainRuleTreeItem.getChildren().get(0).toString());
        assertEquals(0, mainRuleTreeItem.getChildren().get(0).getPosition());
        assertEquals(0, mainRuleTreeItem.getChildren().get(0).getChildren().size());
        assertEquals("Second_Operation", mainRuleTreeItem.getChildren().get(1).toString());
        assertEquals(1, mainRuleTreeItem.getChildren().get(1).getPosition());
        assertEquals(0, mainRuleTreeItem.getChildren().get(1).getChildren().size());
    }
}
