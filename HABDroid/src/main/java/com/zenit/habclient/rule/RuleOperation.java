package com.zenit.habclient.rule;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleOperation extends EntityDataType<Boolean> implements IRuleChild, IOperationResult, IRuleTree {

    private List<IEntityDataType> mOperands;
    private RuleOperator mRuleOperator;
    private String mName;
    private String mDescription;

    public RuleOperation(RuleOperator ruleOperator, List<IEntityDataType> operands) {
        mRuleOperator = ruleOperator;
        mOperands = operands;
    }

    @Override
    public String toString() {
        String[] operandsAsStringArray = new String[mOperands.size() - 1];
        int index = 0;
        Iterator<IEntityDataType> iterator = mOperands.iterator();
        IEntityDataType mLeftOperand;
        if(iterator.hasNext())
            mLeftOperand = iterator.next();//Just consume the leftmost operand
        else return "Oooops!";//TODO - Change description
        while(iterator.hasNext()) {
            operandsAsStringArray[index++] = iterator.next().toString();
        }
        return mLeftOperand.getName() + getRuleOperator().getType().getFormattedString(operandsAsStringArray);
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
            if(operand instanceof IRuleTree)
                hm.put(integer, new RuleTreeItem(integer, operand.toString(), ((IRuleTree) operand).getRuleTreeItem(0)));
            integer++;
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
}
