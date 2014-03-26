package com.zenit.habclient.rule;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleOperation implements IRuleChild, IOperationResult {

    private List<IUnitEntityDataType> mOperands;
    private RuleOperator mRuleOperator;
    private String mName;
    private String mDescription;

    public RuleOperation(RuleOperator ruleOperator, List<IUnitEntityDataType> operands) {
        mRuleOperator = ruleOperator;
        mOperands = operands;
    }

    @Override
    public String toString() {
        String[] operandsAsStringArray = new String[mOperands.size() - 1];
        int index = 0;
        Iterator<IUnitEntityDataType> iterator = mOperands.iterator();
        IUnitEntityDataType mLeftOperand;
        if(iterator.hasNext())
            mLeftOperand = iterator.next();//Just consume the leftmost operand
        else return "Oooops!";//TODO - Change description
        while(iterator.hasNext()) {
            operandsAsStringArray[index++] = iterator.next().toString();
        }
        return mLeftOperand.getName() + mRuleOperator.getType().getFormattedString(operandsAsStringArray);
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
            return mRuleOperator.getOperationResult(mOperands);
        } catch (Exception e) {
            return false;
        }
    }
}
