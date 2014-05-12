package com.zenit.habclient.rule;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleOperation extends EntityDataType<Boolean> implements IRuleChild, IOperationResult {
    private List<? extends IEntityDataType> mOperands;
    private RuleOperator mRuleOperator;
    private String mName;
    private String mDescription;

    public RuleOperation(RuleOperator ruleOperator, List<? extends IEntityDataType> operands) {
        mRuleOperator = ruleOperator;
        mOperands = operands;
    }

//    public RuleOperation(RuleOperator ruleOperator, List<RuleOperation> operands) {
//        mRuleOperator = ruleOperator;
//        mOperands = operands;
//    }

    @Override
    public String toString() {
        if(getName() != null && getName().length() > 0)
            return getName();

        String[] operandsAsStringArray = new String[mOperands.size() - 1];
        int index = 0;
        Iterator<? extends IEntityDataType> iterator = mOperands.iterator();
        IEntityDataType mLeftOperand;
        if(iterator.hasNext())
            mLeftOperand = iterator.next();//Save the the leftmost operand for later.
        else return "Oooops!";//TODO - Throw exception or change description
        while(iterator.hasNext()) {
            IEntityDataType operand = iterator.next();
            boolean isRuleAndUseGeneratedString = getIsRuleAndUseGeneratedString(operand);
            String format = isRuleAndUseGeneratedString? "(%s)" : "%s";
            operandsAsStringArray[index++] = String.format(format, isRuleAndUseGeneratedString? operand.toString() : operand.getName());
        }
        boolean isRuleAndUseGeneratedString = getIsRuleAndUseGeneratedString(mLeftOperand);
        String format = isRuleAndUseGeneratedString? "(%s)%s" : "%s%s";
        return String.format(format, isRuleAndUseGeneratedString? mLeftOperand.toString() : mLeftOperand.getName(), getRuleOperator().getType().getFormattedString(operandsAsStringArray));
    }

    private boolean getIsRuleAndUseGeneratedString(IEntityDataType operand) {
        boolean hasName = operand.getName() != null && operand.getName().length() > 0;
        boolean isRuleAndUseGeneratedString = operand.getSourceType() == EntityDataTypeSource.RULE && !hasName;
        return isRuleAndUseGeneratedString;
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
    public boolean getResult() {
        try {
            return getRuleOperator().getOperationResult(mOperands);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public RuleTreeItem getRuleTreeItem(int treeIndex) {
        HashMap<Integer, RuleTreeItem> hm = new HashMap<Integer, RuleTreeItem>();

        Integer integer = 0;

        for(IEntityDataType operand : mOperands.toArray(new IEntityDataType[0])) {
            RuleTreeItem rti = operand.getRuleTreeItem(integer);
            if(rti != null) {
                hm.put(integer, rti);
                integer++;
            }
        }

        return hm.isEmpty()? new RuleTreeItem(treeIndex, toString()) : new RuleTreeItem(treeIndex, toString(), hm);
    }

    @Override
    public String getFormattedString(){
        return getValue()? "Sant": "Falskt";//TODO - Language independent
    }

    @Override
    public EntityDataTypeSource getSourceType() {
        return EntityDataTypeSource.RULE;
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
    public Boolean getValue() {
        Boolean currentValue = Boolean.valueOf(getResult());
        if(mValue != currentValue) {
            mValue = currentValue;
            //TODO - Send broadcast intent
        }

        return mValue;
    }

    public RuleOperator getRuleOperator() { return mRuleOperator; }

    public void setRuleOperator(RuleOperator ruleOperator) { mRuleOperator = ruleOperator; }


//    public List<IEntityDataType> getOperands() {
//        return mOperands;
//    }
//
//    public void setOperands(List<IEntityDataType> operands) {
//        mOperands = operands;
//    }
}
