package org.openhab.domain.rule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openhab.domain.DocumentFactory;
import org.openhab.domain.IDocumentFactory;
import org.openhab.domain.IOpenHABWidgetProvider;
import org.openhab.domain.IPopularNameProvider;
import org.openhab.domain.OpenHABWidgetProvider;
import org.openhab.domain.PopularNameProvider;
import org.openhab.domain.model.OpenHABWidget;
import org.openhab.domain.rule.operators.RuleOperator;
import org.openhab.domain.util.IColorParser;
import org.openhab.domain.util.ILogger;
import org.openhab.domain.util.RegularExpression;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleTreeTest {
    private IRuleOperationProvider mRop;
    private IOpenHABWidgetProvider mWidgetProvider;

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

        mRop = new RuleOperationProvider();
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
        Float f = Float.valueOf(34.7f);
        Double d = Double.valueOf(34.7);
        Assert.assertEquals(true, Math.abs(f.doubleValue() - d.doubleValue()) < 8E-7f);
        Assert.assertEquals(true, Math.abs(d.doubleValue() - f.doubleValue()) < 8E-7f);
    }

    private List<IEntityDataType> mock_getOperandsAsList(int operandPairNumber) {
        List<IEntityDataType> operands = new ArrayList<IEntityDataType>();

        switch (operandPairNumber) {
            case 1:
                //Switch
                operands.add(UnitEntityDataType.getUnitEntityDataType(mWidgetProvider.getWidgetByID("GF_Kitchen_0")));
                operands.add(UnitEntityDataType.getUnitEntityDataType(mWidgetProvider.getWidgetByID("FF_Bath_1")));
                break;

            case 2:
                //Number
                operands.add(UnitEntityDataType.getUnitEntityDataType(mWidgetProvider.getWidgetByID("FF_Bed_3")));
                operands.add(UnitEntityDataType.getUnitEntityDataType(mWidgetProvider.getWidgetByID("GF_Toilet_4")));
                break;
        }

        return operands;
    }

    public RuleOperation mock_getRuleOperation(int operationNumber) {
        RuleOperation result = null;
        OpenHABWidget widget;

        switch (operationNumber) {
            case 1:
                widget = mWidgetProvider.getWidgetByID("GF_Kitchen_0");
                result = new RuleOperation(mRop.getRuleOperator(widget, RuleOperatorType.Equal), mock_getOperandsAsList(1));
                // Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF]
                break;
            case 2:
                widget = mWidgetProvider.getWidgetByID("FF_Bath_1");
                result = new RuleOperation(mRop.getRuleOperator(widget, RuleOperatorType.NotEqual), mock_getOperandsAsList(1));
                // Light_GF_Kitchen_Ceiling [OFF] != Light_FF_Bath_Mirror [OFF]
                break;
            case 3:
                widget = mWidgetProvider.getWidgetByID("FF_Bed_3");
                result = new RuleOperation(mRop.getRuleOperator(widget, RuleOperatorType.LessThan), mock_getOperandsAsList(2));
                // Temperature_FF_Bed [19.20] < Temperature_GF_Toilet [21.50]
                break;
            case 4:
                widget = mWidgetProvider.getWidgetByID("GF_Toilet_4");
                result = new RuleOperation(mRop.getRuleOperator(widget, RuleOperatorType.MoreThan), mock_getOperandsAsList(2));
                // Temperature_FF_Bed [19.20] > Temperature_GF_Toilet [21.50]
                break;
        }

        return result;
    }

    @Test
    public void test_toString() {
        RuleTreeItem rt = mock_getRuleOperation(1).getRuleTreeItem(0);
        Assert.assertEquals("[Sant] Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF]", rt.toString());
    }

    @Test
    public void test_toString_from_a_two_operation_operands_operation() {
        RuleOperation ron1 = mock_getRuleOperation(1);
        RuleTreeItem rti1 = new RuleTreeItem(0, ron1.getRuleTreeItem(0).toString(), RuleTreeItem.ItemType.OPERAND);
        Assert.assertEquals("[Sant] Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF]", rti1.toString());
        Assert.assertEquals(0, rti1.getPosition());
        Assert.assertEquals(0, rti1.getChildren().size());
        Assert.assertEquals(true, ron1.getValue().getValue());

        RuleOperation ron2 = mock_getRuleOperation(4);
        RuleTreeItem rti2 = new RuleTreeItem(1, ron2.getRuleTreeItem(1).toString(), RuleTreeItem.ItemType.OPERAND);
        Assert.assertEquals("[Falskt] Temperature_FF_Bed [19.2] > Temperature_GF_Toilet [21.5]", rti2.toString());
        Assert.assertEquals(1, rti2.getPosition());
        Assert.assertEquals(0, rti2.getChildren().size());
        Assert.assertEquals(false, ron2.getValue().getValue());

        RuleOperator ror = mRop.getRuleOperator(LogicBoolean.class, RuleOperatorType.And);
        
        List<IEntityDataType> operandList = new ArrayList<IEntityDataType>();
        operandList.add(ron1);
        operandList.add(ron2);
        RuleOperation mainOperation = new RuleOperation(ror, operandList);
        RuleTreeItem mainRuleTreeItem = mainOperation.getRuleTreeItem(3);
        Assert.assertEquals(3, mainRuleTreeItem.getPosition());
        Assert.assertEquals(false, mainOperation.getValue().getValue());
        Assert.assertEquals("(Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF]) AND (Temperature_FF_Bed [19.2] > Temperature_GF_Toilet [21.5])", mainOperation.toString());
        Assert.assertEquals("[Falskt] (Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF]) AND (Temperature_FF_Bed [19.2] > Temperature_GF_Toilet [21.5])", mainRuleTreeItem.toString());
        Assert.assertEquals(3, mainRuleTreeItem.getChildren().size());
        Assert.assertEquals("[Sant] Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF]", mainRuleTreeItem.getChildren().get(0).toString());
        Assert.assertEquals(0, mainRuleTreeItem.getChildren().get(0).getPosition());
        Assert.assertEquals(0, mainRuleTreeItem.getChildren().get(0).getChildren().size());
        Assert.assertEquals("AND", mainRuleTreeItem.getChildren().get(1).toString());
        Assert.assertEquals(1, mainRuleTreeItem.getChildren().get(1).getPosition());
        Assert.assertEquals(0, mainRuleTreeItem.getChildren().get(1).getChildren().size());
        Assert.assertEquals("[Falskt] Temperature_FF_Bed [19.2] > Temperature_GF_Toilet [21.5]", mainRuleTreeItem.getChildren().get(2).toString());
        Assert.assertEquals(2, mainRuleTreeItem.getChildren().get(2).getPosition());
        Assert.assertEquals(0, mainRuleTreeItem.getChildren().get(2).getChildren().size());

        operandList.clear();
        ron1.setName("First_Operation");
        ron2.setName("Second_Operation");
        operandList.add(ron1);
        operandList.add(ron2);
        mainOperation = new RuleOperation(ror, operandList);
        mainRuleTreeItem = mainOperation.getRuleTreeItem(3);
        Assert.assertEquals(3, mainRuleTreeItem.getPosition());
        Assert.assertEquals(false, mainOperation.getValue().getValue());
        Assert.assertEquals("First_Operation [Sant] AND Second_Operation [Falskt]", mainOperation.toString());
        Assert.assertEquals("[Falskt] First_Operation [Sant] AND Second_Operation [Falskt]", mainRuleTreeItem.toString());
        Assert.assertEquals(3, mainRuleTreeItem.getChildren().size());
        Assert.assertEquals("[Sant] First_Operation", mainRuleTreeItem.getChildren().get(0).toString());
        Assert.assertEquals(0, mainRuleTreeItem.getChildren().get(0).getPosition());
        Assert.assertEquals(0, mainRuleTreeItem.getChildren().get(0).getChildren().size());
        Assert.assertEquals("AND", mainRuleTreeItem.getChildren().get(1).toString());
        Assert.assertEquals(1, mainRuleTreeItem.getChildren().get(1).getPosition());
        Assert.assertEquals(0, mainRuleTreeItem.getChildren().get(1).getChildren().size());
        Assert.assertEquals("[Falskt] Second_Operation", mainRuleTreeItem.getChildren().get(2).toString());
        Assert.assertEquals(2, mainRuleTreeItem.getChildren().get(2).getPosition());
        Assert.assertEquals(0, mainRuleTreeItem.getChildren().get(2).getChildren().size());
    }

    @Test
    public void test_toString_with_nested_incomplete_operations() {
        RuleOperation ron1 = mock_getRuleOperation(1);
        ron1.setOperand(0, null);
        RuleTreeItem rti1 = new RuleTreeItem(0, ron1.getRuleTreeItem(0).toString(), RuleTreeItem.ItemType.OPERAND);
        String rti1String = RuleOperatorType.MISSING_OPERAND + " = Light_FF_Bath_Mirror [OFF]";
        Assert.assertEquals("[Falskt] " + rti1String, rti1.toString());
        Assert.assertEquals(false, ron1.getValue().getValue());

        RuleOperation ron2 = mock_getRuleOperation(4);
        ron2.setRuleOperator(null);
        RuleTreeItem rti2 = new RuleTreeItem(1, ron2.getRuleTreeItem(1).toString(), RuleTreeItem.ItemType.OPERAND);
        String rti2String = "Temperature_FF_Bed [19.2] " + RuleOperator.MISSING_OPERATOR;
        Assert.assertEquals("[Falskt] " + rti2String, rti2.toString());
        Assert.assertEquals(false, ron2.getValue().getValue());

        RuleOperation ron3 = new RuleOperation("Empty operation");
        RuleTreeItem rti3 = new RuleTreeItem(1, ron3.getRuleTreeItem(1).toString(), RuleTreeItem.ItemType.OPERAND);
        String rti3String = "Empty operation";
        Assert.assertEquals("[Falskt] " + rti3String + " <Incomplete>", rti3.toString());
        Assert.assertEquals(false, ron3.getValue().getValue());

        RuleOperator ror = mRop.getRuleOperator(LogicBoolean.class, RuleOperatorType.And);

        List<IEntityDataType> operandList = new ArrayList<IEntityDataType>();
        operandList.add(ron1);
        operandList.add(ron2);
        operandList.add(ron3);
        RuleOperation mainOperation = new RuleOperation(ror, operandList);
        RuleTreeItem mainRuleTreeItem = mainOperation.getRuleTreeItem(3);
        Assert.assertEquals(3, mainRuleTreeItem.getPosition());
        Assert.assertEquals(false, mainOperation.getValue().getValue());
        Assert.assertEquals("(" + rti1String + ") AND (" + rti2String + ") AND " + rti3String + " <Incomplete> [Falskt]", mainOperation.toString());
        Assert.assertEquals("[Falskt] (" + rti1String + ") AND (" + rti2String + ") AND " + rti3String + " <Incomplete> [Falskt]", mainRuleTreeItem.toString());
        Assert.assertEquals(5, mainRuleTreeItem.getChildren().size());
        Assert.assertEquals("[Falskt] " + rti1String, mainRuleTreeItem.getChildren().get(0).toString());
        Assert.assertEquals(0, mainRuleTreeItem.getChildren().get(0).getPosition());
        Assert.assertEquals(0, mainRuleTreeItem.getChildren().get(0).getChildren().size());
        Assert.assertEquals("AND", mainRuleTreeItem.getChildren().get(1).toString());
        Assert.assertEquals(1, mainRuleTreeItem.getChildren().get(1).getPosition());
        Assert.assertEquals(0, mainRuleTreeItem.getChildren().get(1).getChildren().size());
        Assert.assertEquals("[Falskt] " + rti2String, mainRuleTreeItem.getChildren().get(2).toString());
        Assert.assertEquals(2, mainRuleTreeItem.getChildren().get(2).getPosition());
        Assert.assertEquals(0, mainRuleTreeItem.getChildren().get(2).getChildren().size());
        Assert.assertEquals("AND", mainRuleTreeItem.getChildren().get(3).toString());
        Assert.assertEquals(3, mainRuleTreeItem.getChildren().get(3).getPosition());
        Assert.assertEquals(0, mainRuleTreeItem.getChildren().get(3).getChildren().size());
        Assert.assertEquals("[Falskt] " + rti3String + " <Incomplete>", mainRuleTreeItem.getChildren().get(4).toString());
        Assert.assertEquals(4, mainRuleTreeItem.getChildren().get(4).getPosition());
        Assert.assertEquals(0, mainRuleTreeItem.getChildren().get(4).getChildren().size());

        mainOperation.setRuleOperator(mRop.getRuleOperator(LogicBoolean.class, RuleOperatorType.Or));

        mainRuleTreeItem = mainOperation.getRuleTreeItem(3);
        Assert.assertEquals(false, mainOperation.getValue().getValue());
        Assert.assertEquals("(" + rti1String + ") OR (" + rti2String + ") OR " + rti3String + " <Incomplete> [Falskt]", mainOperation.toString());
        Assert.assertEquals("[Falskt] (" + rti1String + ") OR (" + rti2String + ") OR " + rti3String + " <Incomplete> [Falskt]", mainRuleTreeItem.toString());
        Assert.assertEquals(5, mainRuleTreeItem.getChildren().size());
        Assert.assertEquals("[Falskt] " + rti1String, mainRuleTreeItem.getChildren().get(0).toString());
        Assert.assertEquals(0, mainRuleTreeItem.getChildren().get(0).getPosition());
        Assert.assertEquals(0, mainRuleTreeItem.getChildren().get(0).getChildren().size());
        Assert.assertEquals("OR", mainRuleTreeItem.getChildren().get(1).toString());
        Assert.assertEquals(1, mainRuleTreeItem.getChildren().get(1).getPosition());
        Assert.assertEquals(0, mainRuleTreeItem.getChildren().get(1).getChildren().size());
        Assert.assertEquals("[Falskt] " + rti2String, mainRuleTreeItem.getChildren().get(2).toString());
        Assert.assertEquals(2, mainRuleTreeItem.getChildren().get(2).getPosition());
        Assert.assertEquals(0, mainRuleTreeItem.getChildren().get(2).getChildren().size());
        Assert.assertEquals("OR", mainRuleTreeItem.getChildren().get(3).toString());
        Assert.assertEquals(3, mainRuleTreeItem.getChildren().get(3).getPosition());
        Assert.assertEquals(0, mainRuleTreeItem.getChildren().get(3).getChildren().size());
        Assert.assertEquals("[Falskt] " + rti3String + " <Incomplete>", mainRuleTreeItem.getChildren().get(4).toString());
        Assert.assertEquals(4, mainRuleTreeItem.getChildren().get(4).getPosition());
        Assert.assertEquals(0, mainRuleTreeItem.getChildren().get(4).getChildren().size());

        ror = mainOperation.getRuleOperator();
        operandList.add(mock_getRuleOperation(1));
        mainOperation = new RuleOperation(ror, operandList);
        mainRuleTreeItem = mainOperation.getRuleTreeItem(3);
        Assert.assertEquals(true, mainOperation.getValue().getValue());
        Assert.assertEquals("(" + rti1String + ") OR (" + rti2String + ") OR " + rti3String + " <Incomplete> [Falskt] OR (Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF])", mainOperation.toString());
        Assert.assertEquals("[Sant] (" + rti1String + ") OR (" + rti2String + ") OR " + rti3String + " <Incomplete> [Falskt] OR (Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF])", mainRuleTreeItem.toString());
        Assert.assertEquals(7, mainRuleTreeItem.getChildren().size());
        Assert.assertEquals("[Falskt] " + rti1String, mainRuleTreeItem.getChildren().get(0).toString());
        Assert.assertEquals(0, mainRuleTreeItem.getChildren().get(0).getPosition());
        Assert.assertEquals(0, mainRuleTreeItem.getChildren().get(0).getChildren().size());
        Assert.assertEquals("OR", mainRuleTreeItem.getChildren().get(1).toString());
        Assert.assertEquals(1, mainRuleTreeItem.getChildren().get(1).getPosition());
        Assert.assertEquals(0, mainRuleTreeItem.getChildren().get(1).getChildren().size());
        Assert.assertEquals("[Falskt] " + rti2String, mainRuleTreeItem.getChildren().get(2).toString());
        Assert.assertEquals(2, mainRuleTreeItem.getChildren().get(2).getPosition());
        Assert.assertEquals(0, mainRuleTreeItem.getChildren().get(2).getChildren().size());
        Assert.assertEquals("OR", mainRuleTreeItem.getChildren().get(3).toString());
        Assert.assertEquals(3, mainRuleTreeItem.getChildren().get(3).getPosition());
        Assert.assertEquals(0, mainRuleTreeItem.getChildren().get(3).getChildren().size());
        Assert.assertEquals("[Falskt] " + rti3String + " <Incomplete>", mainRuleTreeItem.getChildren().get(4).toString());
        Assert.assertEquals(4, mainRuleTreeItem.getChildren().get(4).getPosition());
        Assert.assertEquals(0, mainRuleTreeItem.getChildren().get(4).getChildren().size());
        Assert.assertEquals("OR", mainRuleTreeItem.getChildren().get(5).toString());
        Assert.assertEquals(5, mainRuleTreeItem.getChildren().get(5).getPosition());
        Assert.assertEquals(0, mainRuleTreeItem.getChildren().get(5).getChildren().size());
        Assert.assertEquals("[Sant] Light_GF_Kitchen_Ceiling [OFF] = Light_FF_Bath_Mirror [OFF]", mainRuleTreeItem.getChildren().get(6).toString());
        Assert.assertEquals(6, mainRuleTreeItem.getChildren().get(6).getPosition());
        Assert.assertEquals(0, mainRuleTreeItem.getChildren().get(6).getChildren().size());
    }

    @Test
    public void test_toString_with_nested_empty_operations() {
        RuleOperation ro1 = new RuleOperation("Hello");
        RuleOperation ro2 = new RuleOperation("There");
        ro1.setOperand(0, ro2);
        RuleTreeItem ruleTreeItem = ro1.getRuleTreeItem(0);

        Assert.assertEquals(1, ruleTreeItem.getChildren().size());
        Assert.assertEquals("Hello <Incomplete> [Falskt]", ro1.toString());
        Assert.assertEquals("There <Incomplete> [Falskt]", ro2.toString());
        Assert.assertEquals("[Falskt] Hello <Incomplete>", ruleTreeItem.toString());
        Assert.assertEquals(0, ruleTreeItem.getChildren().get(0).getChildren().size());
        Assert.assertEquals("[Falskt] There <Incomplete>", ruleTreeItem.getChildren().get(0).toString());
        Assert.assertEquals(0, ruleTreeItem.getChildren().get(0).getPosition());
        Assert.assertEquals(0, ruleTreeItem.getChildren().get(0).getChildren().size());

    }
}