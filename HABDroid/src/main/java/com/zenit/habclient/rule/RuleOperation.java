package com.zenit.habclient.rule;

import android.util.Log;

import com.zenit.habclient.HABApplication;
import com.zenit.habclient.OnOperandValueChangedListener;
import com.zenit.habclient.util.StringHandler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleOperation extends EntityDataType<Boolean> implements IRuleChild, OnOperandValueChangedListener {
    private List<? extends IEntityDataType> mOperands;
    private RuleOperator mRuleOperator;
    private String mDescription;

    public RuleOperation(RuleOperator ruleOperator, List<? extends IEntityDataType> operands) {
        mRuleOperator = ruleOperator;
        mOperands = operands;
        for (IEntityDataType operand : operands) {
            operand.setOnOperandValueChangedListener(this);
        }
        runCalculation();
    }

//    public RuleOperation(RuleOperator ruleOperator, List<RuleOperation> operands) {
//        mRuleOperator = ruleOperator;
//        mOperands = operands;
//    }

    @Override
    public String toString() {
        if(!StringHandler.isNullOrEmpty(getName())) {
            return getName() + "[" + getFormattedString() + "]";
        }

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
            operandsAsStringArray[index++] = String.format(format, operand.toString());
        }
        boolean isRuleAndUseGeneratedString = getIsRuleAndUseGeneratedString(mLeftOperand);
        String format = isRuleAndUseGeneratedString? "(%s)%s" : "%s%s";
        return String.format(format, mLeftOperand.toString(), getRuleOperator().getType().getFormattedString(operandsAsStringArray));
    }

    private boolean getIsRuleAndUseGeneratedString(IEntityDataType operand) {
        return operand.getSourceType() == EntityDataTypeSource.RULE && !StringHandler.isNullOrEmpty(operand.getName());
    }

    private void runCalculation() {
        try {
            Boolean oldValue = mValue;
            mValue = getRuleOperator().getOperationResult(mOperands);
            if(!oldValue.equals(mValue) && mOnOperandValueChangedListener != null)
                mOnOperandValueChangedListener.onOperandValueChanged(this);
        } catch (Exception e) {
            Log.e(HABApplication.getLogTag(), e.toString());
        }
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
    /**
     * The latest resulting value OR null.
     */
    public Boolean getValue() {
        //Returns mValue instead of calling runCalculation() to prevent multiple calls to getResult().
        return mValue;
    }

    public RuleOperator getRuleOperator() { return mRuleOperator; }

    public void setRuleOperator(RuleOperator ruleOperator) { mRuleOperator = ruleOperator; }

    @Override
    public void onOperandValueChanged(IEntityDataType operand) {
        runCalculation();
    }
}
