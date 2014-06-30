package org.openhab.test.habclient.rule;

import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.rule.IEntityDataType;
import org.openhab.domain.rule.RuleOperation;
import org.openhab.domain.rule.RuleOperationProvider;
import org.openhab.domain.rule.RuleOperator;
import org.openhab.domain.rule.RuleOperatorType;
import org.openhab.domain.rule.RuleTreeItem;
import org.openhab.domain.rule.UnitEntityDataType;
import org.openhab.domain.util.IColorParser;
import org.openhab.domain.util.ILogger;
import org.openhab.habclient.HABApplication;
import org.openhab.habclient.UnitEntityDataTypeProvider;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

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

    public void setUp() throws Exception {
        super.setUp();

        createApplication();
        mHABApplication = getApplication();

        final ILogger logger = mock(ILogger.class);
        final IColorParser colorParser = mock(IColorParser.class);
        mHttpDataSetup = new HttpDataSetup(logger, colorParser, mHABApplication.getOpenHABWidgetProvider());
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

        switch (operandPairNumber) {
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

    public RuleOperation mock_getRuleOperation(int operationNumber) {
        RuleOperation result = null;
        OpenHABWidget widget;

        switch (operationNumber) {
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
        assertEquals("[Sant] Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF]", rt.toString());
    }

    public void test_toString_from_a_two_operation_operands_operation() {
        RuleOperation ron1 = mock_getRuleOperation(1);
        RuleTreeItem rti1 = new RuleTreeItem(0, ron1.getRuleTreeItem(0).toString(), RuleTreeItem.ItemType.OPERAND);
        assertEquals("[Sant] Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF]", rti1.toString());
        assertEquals(0, rti1.getPosition());
        assertEquals(0, rti1.getChildren().size());
        assertEquals(true, ron1.getValue().booleanValue());

        RuleOperation ron2 = mock_getRuleOperation(4);
        RuleTreeItem rti2 = new RuleTreeItem(1, ron2.getRuleTreeItem(1).toString(), RuleTreeItem.ItemType.OPERAND);
        assertEquals("[Falskt] Temperature_FF_Bed [19.2] > Temperature_GF_Toilet [21.5]", rti2.toString());
        assertEquals(1, rti2.getPosition());
        assertEquals(0, rti2.getChildren().size());
        assertEquals(false, ron2.getValue().booleanValue());

        RuleOperator ror = mHABApplication.getRuleOperationProvider().getUnitRuleOperatorHash(Boolean.class).get(RuleOperatorType.And);
        List<IEntityDataType> operandList = new ArrayList<IEntityDataType>();
        operandList.add(ron1);
        operandList.add(ron2);
        RuleOperation mainOperation = new RuleOperation(ror, operandList);
        RuleTreeItem mainRuleTreeItem = mainOperation.getRuleTreeItem(3);
        assertEquals(3, mainRuleTreeItem.getPosition());
        assertEquals(false, mainOperation.getValue().booleanValue());
        assertEquals("(Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF]) AND (Temperature_FF_Bed [19.2] > Temperature_GF_Toilet [21.5])", mainOperation.toString());
        assertEquals("[Falskt] (Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF]) AND (Temperature_FF_Bed [19.2] > Temperature_GF_Toilet [21.5])", mainRuleTreeItem.toString());
        assertEquals(3, mainRuleTreeItem.getChildren().size());
        assertEquals("[Sant] Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF]", mainRuleTreeItem.getChildren().get(0).toString());
        assertEquals(0, mainRuleTreeItem.getChildren().get(0).getPosition());
        assertEquals(0, mainRuleTreeItem.getChildren().get(0).getChildren().size());
        assertEquals("AND", mainRuleTreeItem.getChildren().get(1).toString());
        assertEquals(1, mainRuleTreeItem.getChildren().get(1).getPosition());
        assertEquals(0, mainRuleTreeItem.getChildren().get(1).getChildren().size());
        assertEquals("[Falskt] Temperature_FF_Bed [19.2] > Temperature_GF_Toilet [21.5]", mainRuleTreeItem.getChildren().get(2).toString());
        assertEquals(2, mainRuleTreeItem.getChildren().get(2).getPosition());
        assertEquals(0, mainRuleTreeItem.getChildren().get(2).getChildren().size());

        operandList.clear();
        ron1.setName("First_Operation");
        ron2.setName("Second_Operation");
        operandList.add(ron1);
        operandList.add(ron2);
        mainOperation = new RuleOperation(ror, operandList);
        mainRuleTreeItem = mainOperation.getRuleTreeItem(3);
        assertEquals(3, mainRuleTreeItem.getPosition());
        assertEquals(false, mainOperation.getValue().booleanValue());
        assertEquals("First_Operation [Sant] AND Second_Operation [Falskt]", mainOperation.toString());
        assertEquals("[Falskt] First_Operation [Sant] AND Second_Operation [Falskt]", mainRuleTreeItem.toString());
        assertEquals(3, mainRuleTreeItem.getChildren().size());
        assertEquals("[Sant] First_Operation", mainRuleTreeItem.getChildren().get(0).toString());
        assertEquals(0, mainRuleTreeItem.getChildren().get(0).getPosition());
        assertEquals(0, mainRuleTreeItem.getChildren().get(0).getChildren().size());
        assertEquals("AND", mainRuleTreeItem.getChildren().get(1).toString());
        assertEquals(1, mainRuleTreeItem.getChildren().get(1).getPosition());
        assertEquals(0, mainRuleTreeItem.getChildren().get(1).getChildren().size());
        assertEquals("[Falskt] Second_Operation", mainRuleTreeItem.getChildren().get(2).toString());
        assertEquals(2, mainRuleTreeItem.getChildren().get(2).getPosition());
        assertEquals(0, mainRuleTreeItem.getChildren().get(2).getChildren().size());
    }

    public void test_toString_with_nested_incomplete_operations() {
        RuleOperation ron1 = mock_getRuleOperation(1);
        ron1.setOperand(0, null);
        RuleTreeItem rti1 = new RuleTreeItem(0, ron1.getRuleTreeItem(0).toString(), RuleTreeItem.ItemType.OPERAND);
        String rti1String = RuleOperatorType.MISSING_OPERAND + " = Light_FF_Bath_Mirror [OFF]";
        assertEquals("[Falskt] " + rti1String, rti1.toString());
        assertEquals(false, ron1.getValue().booleanValue());

        RuleOperation ron2 = mock_getRuleOperation(4);
        ron2.setRuleOperator(null);
        RuleTreeItem rti2 = new RuleTreeItem(1, ron2.getRuleTreeItem(1).toString(), RuleTreeItem.ItemType.OPERAND);
        String rti2String = "Temperature_FF_Bed [19.2] " + RuleOperator.MISSING_OPERATOR;
        assertEquals("[Falskt] " + rti2String, rti2.toString());
        assertEquals(false, ron2.getValue().booleanValue());

        RuleOperation ron3 = new RuleOperation("Empty operation");
        RuleTreeItem rti3 = new RuleTreeItem(1, ron3.getRuleTreeItem(1).toString(), RuleTreeItem.ItemType.OPERAND);
        String rti3String = "Empty operation";
        assertEquals("[Falskt] " + rti3String + " <Incomplete>", rti3.toString());
        assertEquals(false, ron3.getValue().booleanValue());

        RuleOperator ror = mHABApplication.getRuleOperationProvider().getUnitRuleOperatorHash(Boolean.class).get(RuleOperatorType.And);
        List<IEntityDataType> operandList = new ArrayList<IEntityDataType>();
        operandList.add(ron1);
        operandList.add(ron2);
        operandList.add(ron3);
        RuleOperation mainOperation = new RuleOperation(ror, operandList);
        RuleTreeItem mainRuleTreeItem = mainOperation.getRuleTreeItem(3);
        assertEquals(3, mainRuleTreeItem.getPosition());
        assertEquals(false, mainOperation.getValue().booleanValue());
        assertEquals("(" + rti1String + ") AND (" + rti2String + ") AND " + rti3String + " <Incomplete> [Falskt]", mainOperation.toString());
        assertEquals("[Falskt] (" + rti1String + ") AND (" + rti2String + ") AND " + rti3String + " <Incomplete> [Falskt]", mainRuleTreeItem.toString());
        assertEquals(5, mainRuleTreeItem.getChildren().size());
        assertEquals("[Falskt] " + rti1String, mainRuleTreeItem.getChildren().get(0).toString());
        assertEquals(0, mainRuleTreeItem.getChildren().get(0).getPosition());
        assertEquals(0, mainRuleTreeItem.getChildren().get(0).getChildren().size());
        assertEquals("AND", mainRuleTreeItem.getChildren().get(1).toString());
        assertEquals(1, mainRuleTreeItem.getChildren().get(1).getPosition());
        assertEquals(0, mainRuleTreeItem.getChildren().get(1).getChildren().size());
        assertEquals("[Falskt] " + rti2String, mainRuleTreeItem.getChildren().get(2).toString());
        assertEquals(2, mainRuleTreeItem.getChildren().get(2).getPosition());
        assertEquals(0, mainRuleTreeItem.getChildren().get(2).getChildren().size());
        assertEquals("AND", mainRuleTreeItem.getChildren().get(3).toString());
        assertEquals(3, mainRuleTreeItem.getChildren().get(3).getPosition());
        assertEquals(0, mainRuleTreeItem.getChildren().get(3).getChildren().size());
        assertEquals("[Falskt] " + rti3String + " <Incomplete>", mainRuleTreeItem.getChildren().get(4).toString());
        assertEquals(4, mainRuleTreeItem.getChildren().get(4).getPosition());
        assertEquals(0, mainRuleTreeItem.getChildren().get(4).getChildren().size());

        mainOperation.setRuleOperator(mHABApplication.getRuleOperationProvider().getUnitRuleOperatorHash(Boolean.class).get(RuleOperatorType.Or));
        mainRuleTreeItem = mainOperation.getRuleTreeItem(3);
        assertEquals(false, mainOperation.getValue().booleanValue());
        assertEquals("(" + rti1String + ") OR (" + rti2String + ") OR " + rti3String + " <Incomplete> [Falskt]", mainOperation.toString());
        assertEquals("[Falskt] (" + rti1String + ") OR (" + rti2String + ") OR " + rti3String + " <Incomplete> [Falskt]", mainRuleTreeItem.toString());
        assertEquals(5, mainRuleTreeItem.getChildren().size());
        assertEquals("[Falskt] " + rti1String, mainRuleTreeItem.getChildren().get(0).toString());
        assertEquals(0, mainRuleTreeItem.getChildren().get(0).getPosition());
        assertEquals(0, mainRuleTreeItem.getChildren().get(0).getChildren().size());
        assertEquals("OR", mainRuleTreeItem.getChildren().get(1).toString());
        assertEquals(1, mainRuleTreeItem.getChildren().get(1).getPosition());
        assertEquals(0, mainRuleTreeItem.getChildren().get(1).getChildren().size());
        assertEquals("[Falskt] " + rti2String, mainRuleTreeItem.getChildren().get(2).toString());
        assertEquals(2, mainRuleTreeItem.getChildren().get(2).getPosition());
        assertEquals(0, mainRuleTreeItem.getChildren().get(2).getChildren().size());
        assertEquals("OR", mainRuleTreeItem.getChildren().get(3).toString());
        assertEquals(3, mainRuleTreeItem.getChildren().get(3).getPosition());
        assertEquals(0, mainRuleTreeItem.getChildren().get(3).getChildren().size());
        assertEquals("[Falskt] " + rti3String + " <Incomplete>", mainRuleTreeItem.getChildren().get(4).toString());
        assertEquals(4, mainRuleTreeItem.getChildren().get(4).getPosition());
        assertEquals(0, mainRuleTreeItem.getChildren().get(4).getChildren().size());

        ror = mainOperation.getRuleOperator();
        operandList.add(mock_getRuleOperation(1));
        mainOperation = new RuleOperation(ror, operandList);
        mainRuleTreeItem = mainOperation.getRuleTreeItem(3);
        assertEquals(true, mainOperation.getValue().booleanValue());
        assertEquals("(" + rti1String + ") OR (" + rti2String + ") OR " + rti3String + " <Incomplete> [Falskt] OR (Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF])", mainOperation.toString());
        assertEquals("[Sant] (" + rti1String + ") OR (" + rti2String + ") OR " + rti3String + " <Incomplete> [Falskt] OR (Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF])", mainRuleTreeItem.toString());
        assertEquals(7, mainRuleTreeItem.getChildren().size());
        assertEquals("[Falskt] " + rti1String, mainRuleTreeItem.getChildren().get(0).toString());
        assertEquals(0, mainRuleTreeItem.getChildren().get(0).getPosition());
        assertEquals(0, mainRuleTreeItem.getChildren().get(0).getChildren().size());
        assertEquals("OR", mainRuleTreeItem.getChildren().get(1).toString());
        assertEquals(1, mainRuleTreeItem.getChildren().get(1).getPosition());
        assertEquals(0, mainRuleTreeItem.getChildren().get(1).getChildren().size());
        assertEquals("[Falskt] " + rti2String, mainRuleTreeItem.getChildren().get(2).toString());
        assertEquals(2, mainRuleTreeItem.getChildren().get(2).getPosition());
        assertEquals(0, mainRuleTreeItem.getChildren().get(2).getChildren().size());
        assertEquals("OR", mainRuleTreeItem.getChildren().get(3).toString());
        assertEquals(3, mainRuleTreeItem.getChildren().get(3).getPosition());
        assertEquals(0, mainRuleTreeItem.getChildren().get(3).getChildren().size());
        assertEquals("[Falskt] " + rti3String + " <Incomplete>", mainRuleTreeItem.getChildren().get(4).toString());
        assertEquals(4, mainRuleTreeItem.getChildren().get(4).getPosition());
        assertEquals(0, mainRuleTreeItem.getChildren().get(4).getChildren().size());
        assertEquals("OR", mainRuleTreeItem.getChildren().get(5).toString());
        assertEquals(5, mainRuleTreeItem.getChildren().get(5).getPosition());
        assertEquals(0, mainRuleTreeItem.getChildren().get(5).getChildren().size());
        assertEquals("[Sant] Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF]", mainRuleTreeItem.getChildren().get(6).toString());
        assertEquals(6, mainRuleTreeItem.getChildren().get(6).getPosition());
        assertEquals(0, mainRuleTreeItem.getChildren().get(6).getChildren().size());
    }

    public void test_toString_with_nested_empty_operations() {
        RuleOperation ro1 = new RuleOperation("Hello");
        RuleOperation ro2 = new RuleOperation("There");
        ro1.setOperand(0, ro2);
        RuleTreeItem ruleTreeItem = ro1.getRuleTreeItem(0);

        assertEquals(1, ruleTreeItem.getChildren().size());
        assertEquals("Hello <Incomplete> [Falskt]", ro1.toString());
        assertEquals("There <Incomplete> [Falskt]", ro2.toString());
        assertEquals("[Falskt] Hello <Incomplete>", ruleTreeItem.toString());
        assertEquals(0, ruleTreeItem.getChildren().get(0).getChildren().size());
        assertEquals("[Falskt] There <Incomplete>", ruleTreeItem.getChildren().get(0).toString());
        assertEquals(0, ruleTreeItem.getChildren().get(0).getPosition());
        assertEquals(0, ruleTreeItem.getChildren().get(0).getChildren().size());

    }
}