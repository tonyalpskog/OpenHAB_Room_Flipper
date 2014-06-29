package org.openhab.rule;

import org.openhab.util.StringHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleOperation extends EntityDataType<Boolean> implements IRuleChild, OnOperandValueChangedListener {
    private List<IEntityDataType> mOperands;
    private RuleOperator mRuleOperator;
    private String mDescription;

    public RuleOperation(String name) {
        setName(name);
        mOperands = new ArrayList<IEntityDataType>();
        mOperands.add(null);
        mDataSourceId = UUID.randomUUID().toString();
    }

    public RuleOperation(RuleOperator ruleOperator, List<IEntityDataType> operands) {
        mRuleOperator = ruleOperator;
        mOperands = operands;
        mDataSourceId = UUID.randomUUID().toString();
        for (IEntityDataType operand : operands) {
            if(operand != null)
                operand.setOnOperandValueChangedListener(this);
        }
        runCalculation();
    }

    public int getNumberOfOperands() {
        return mOperands.size();
    }

    public void setOperand(int index, IEntityDataType operand) {
        //TODO - TA: Investigate if OnOperandValueChangedListener should be unregistered.
//        if(mOperands.get(index) != null)
            //Unregister OnOperandValueChangedListener ???

        if(mOperands.size() > index)
            mOperands.set(index, operand);
        else
            mOperands.add(index, operand);

        if(operand != null)
            operand.setOnOperandValueChangedListener(this);

        runCalculation();
    }

    public IEntityDataType getOperand(int index) {
        if(mOperands.size() > index)
            return mOperands.get(index);
        return null;
    }

    public IEntityDataType getOperandBySourceId(String operandSourceId) {
        for(IEntityDataType operand : mOperands)
            if(operand!= null && !StringHandler.isNullOrEmpty(operand.getDataSourceId()) && operand.getDataSourceId().equalsIgnoreCase(operandSourceId))
                return operand;
        return null;
    }

    public int getOperandIndexBySourceId(String operandSourceId) {
        IEntityDataType operand = getOperandBySourceId(operandSourceId);
        return operand != null? mOperands.indexOf(operand) : -1;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public boolean isIncomplete() {
        if(getRuleOperator() == null)
            return true;

        for(IEntityDataType operand : mOperands.toArray(new IEntityDataType[0]))
            if(operand == null)
                return true;

        return false;
    }

    private String toString(boolean addResultAsPostfixToNonGeneratedStrings) {
        if(!StringHandler.isNullOrEmpty(getName())) {
            return getName()
                    + (isIncomplete()? " <Incomplete>" : "")
                    + (addResultAsPostfixToNonGeneratedStrings? " [" + getFormattedString() + "]" : "");
        }

        String[] operandsAsStringArray = new String[mOperands.size() - 1];
        int index = 0;
        Iterator<IEntityDataType> iterator = mOperands.iterator();
        IEntityDataType mLeftOperand;
        if(iterator.hasNext())
            mLeftOperand = iterator.next();//Save the the leftmost operand for later.
        else return "Oooops!";//TODO - TA: Throw exception or change description

        if(mLeftOperand == null && getRuleOperator() == null)
            return RuleOperatorType.MISSING_OPERAND;

        while(iterator.hasNext()) {
            IEntityDataType operand = iterator.next();
            boolean isRuleAndUseGeneratedString = getIsRuleAndUseGeneratedString(operand);
            String format = isRuleAndUseGeneratedString? "(%s)" : "%s";
            operandsAsStringArray[index++] = String.format(format, operand == null? RuleOperatorType.MISSING_OPERAND : operand.toString());
        }
        boolean isRuleAndUseGeneratedString = getIsRuleAndUseGeneratedString(mLeftOperand);
        String format = isRuleAndUseGeneratedString? "(%s)%s" : "%s%s";

        return String.format(format, mLeftOperand == null? RuleOperatorType.MISSING_OPERAND : mLeftOperand.toString()
                , getRuleOperator() == null? " " + RuleOperator.MISSING_OPERATOR : getRuleOperator().getType().getFormattedString(operandsAsStringArray));
    }

    public String toString(boolean addResultAsPrefix, boolean addResultAsPostfix) {
        StringBuilder result = new StringBuilder();

        if(addResultAsPrefix)
            result.append("[" + getFormattedString() + "] ");

        result.append(toString(false));

        if(addResultAsPostfix)
            result.append(" [" + getFormattedString() + "]");

        return result.toString();
    }

    private boolean getIsRuleAndUseGeneratedString(IEntityDataType operand) {
        return operand == null? false : operand.getSourceType() == EntityDataTypeSource.OPERATION && StringHandler.isNullOrEmpty(operand.getName());
    }

    private void runCalculation() {
        Boolean oldValue = getValue();

        if(getRuleOperator() == null) {
            mValue = false;//Missing operator shall result as FALSE.
        } else
            mValue = getRuleOperator().getOperationResult(mOperands);

        if(!oldValue.equals(mValue) && mOnOperandValueChangedListener != null)
            mOnOperandValueChangedListener.onOperandValueChanged(this);
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public void setName(String name) {
        mName = name;
    }

    @Override
    public String getDescription() { return mDescription; }

    @Override
    public void setDescription(String description) { mDescription = description; }

    @Override
    public RuleTreeItem getRuleTreeItem(int treeIndex) {
        HashMap<Integer, RuleTreeItem> hm = new HashMap<Integer, RuleTreeItem>();

        Integer treeItemIndex = 0;
        for(IEntityDataType operand : mOperands.toArray(new IEntityDataType[0])) {
            RuleTreeItem rti = operand == null? null : operand.getRuleTreeItem(treeItemIndex);
            if(rti != null) {
                if(!hm.isEmpty()) {
                    RuleTreeItem operatorTreeItem = new RuleTreeItem(treeItemIndex
                            , getRuleOperator() != null? getRuleOperator().getType().getName() : RuleOperator.MISSING_OPERATOR
                            , RuleTreeItem.ItemType.OPERATOR);
                    hm.put(treeItemIndex++, operatorTreeItem);
                    rti.setPosition(treeItemIndex);
                }
                hm.put(treeItemIndex++, rti);
            }
        }

        RuleTreeItem result = hm.isEmpty()? new RuleTreeItem(treeIndex, toString(true, false), RuleTreeItem.ItemType.OPERAND) : new RuleTreeItem(treeIndex, toString(true, false), RuleTreeItem.ItemType.OPERAND, hm);
        result.setItemId(getDataSourceId());
        return result;
    }

    @Override
    public String getFormattedString(){
        if(getRuleOperator() == null)
            return "Falskt";

        for(IEntityDataType operand : mOperands)
            if(operand == null)
                return "Falskt";

        return getValue()? "Sant": "Falskt";//TODO - Language independent
    }

    @Override
    public EntityDataTypeSource getSourceType() {
        return EntityDataTypeSource.OPERATION;
    }

    @Override
    public Class<Boolean> getDataType() {
        return Boolean.class;
    }

    @Override
    public Boolean valueOf(String input) {
        return Boolean.valueOf(input);
    }

    @Override
    /**
     * The latest resulting value OR null.
     */
    public Boolean getValue() {
        //Returns mValue instead of calling runCalculation() to prevent from multiple calls to getResult().
        return mValue == null? false: mValue;
    }

    public RuleOperator getRuleOperator() { return mRuleOperator; }

    public void setRuleOperator(RuleOperator ruleOperator) {
        mRuleOperator = ruleOperator;
        runCalculation();
    }

    @Override
    public void onOperandValueChanged(IEntityDataType operand) {
        runCalculation();
    }

    public HashMap<String, RuleOperation> getRuleOperationHash() {
        HashMap<String, RuleOperation> operationIdHash = new HashMap<String, RuleOperation>();
        operationIdHash.put(getDataSourceId(), this);
        for(IEntityDataType operand : mOperands) {
            if(operand.getSourceType() == EntityDataTypeSource.OPERATION)
                operationIdHash.putAll(((RuleOperation) operand).getRuleOperationHash());
            else
                operationIdHash.put(operand.getDataSourceId(), this);
        }
        return operationIdHash;
    }
}
