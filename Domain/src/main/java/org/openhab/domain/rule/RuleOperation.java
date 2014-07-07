package org.openhab.domain.rule;

import org.openhab.domain.rule.operators.RuleOperator;
import org.openhab.domain.user.AccessModifier;
import org.openhab.domain.util.StringHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Tony Alpskog in 2014.
 */
public class RuleOperation extends EntityDataType<LogicBoolean> implements IRuleChild, OnOperandValueChangedListener {
    private List<IEntityDataType> mOperands;
    private RuleOperator mRuleOperator;
    private String mDescription;
    private boolean mIsActive;
    private AccessModifier mAccessModifier;

    public RuleOperation() {
        mOperands = new ArrayList<IEntityDataType>();
        mOperands.add(null);
        mDataSourceId = UUID.randomUUID().toString();
        mAccessModifier = mAccessModifier.ReadOnly;
    }

    public RuleOperation(String name) {
        this();
        setName(name);
    }

    public RuleOperation(RuleOperator ruleOperator, List<IEntityDataType> operands) {
        setActive(true);
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

    public boolean isActive() {
        return mIsActive;
    }

    public void setActive(boolean isActive) {
        mIsActive = isActive;
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

        String rightOperandString;
        if(getRuleOperator() == null)
            rightOperandString = " " + RuleOperator.MISSING_OPERATOR;
        else {
            try {
                rightOperandString = getRuleOperator().getType().getFormattedString(operandsAsStringArray);
            } catch(IllegalArgumentException ex) {
                rightOperandString = " " + getRuleOperator().getName() + " <Missing operand(s)>";//TODO - TA: Language independent
            }
        }
        return String.format(format, mLeftOperand == null? RuleOperatorType.MISSING_OPERAND : mLeftOperand.toString()
                , rightOperandString);
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

    public boolean isValid() {
        if(mRuleOperator == null)
            return false;

        if(mOperands.size() < mRuleOperator.getType().getMinimumNumberOfSupportedOperationArgs()
                || mOperands.size() > mRuleOperator.getType().getMaximumNumberOfSupportedOperationArgs())
            return false;

        for(IEntityDataType operand : mOperands) {
            if(operand. getValue() == null)
                return false;
        }

        return true;
    }
    public void runCalculation() {
        if(!isActive() || !isValid()) return;

        LogicBoolean oldValue = mValue;

        if(getRuleOperator() == null) {
            if(mValue == null)
                mValue = new LogicBoolean(Boolean.FALSE);
            else
                mValue.setValue(false);//Missing operator shall result as FALSE.
        } else
            mValue = new LogicBoolean(getRuleOperator().getOperationResult(mOperands));

        if(!mValue.equals(oldValue) && mOnOperandValueChangedListener != null)
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

        return getValue().getValue()? "Sant": "Falskt";//TODO - TA: Language independent
    }

    @Override
    public EntityDataTypeSource getSourceType() {
        return EntityDataTypeSource.OPERATION;
    }

    @Override
    public Class<LogicBoolean> getDataType() {
        return LogicBoolean.class;
    }

    @Override
    public LogicBoolean valueOf(String input) {
        return new LogicBoolean(new Boolean(input));
    }

    @Override
    /**
     * The latest resulting value OR null.
     */
    public LogicBoolean getValue() {
        //Returns mValue instead of calling runCalculation() to prevent from multiple calls to getResult().
        return mValue == null? new LogicBoolean(false): mValue;
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

    public Map<String, LogicBoolean> getStaticValues() {
        Map<String, LogicBoolean> nameValueMap = new HashMap<String, LogicBoolean>(2);
        nameValueMap.put("TRUE", new LogicBoolean(Boolean.TRUE));
        nameValueMap.put("FALSE", new LogicBoolean(Boolean.FALSE));
        return nameValueMap;
    }

    public static RuleOperation getStaticEntityDataType(String staticValue) {
        RuleOperation staticEntityDataType = null;

        //TODO - TA: replace static values with constants (TRUE, FALSE, Undefined)
        LogicBoolean aLogicBoolean;

        if(staticValue == null || staticValue.equalsIgnoreCase("Undefined"))
            aLogicBoolean = null;
        else
            aLogicBoolean = new LogicBoolean(staticValue.equalsIgnoreCase("TRUE"));

        staticEntityDataType = new RuleOperation()
        {
            @Override
            public String getFormattedString(){
                return mValue.getValue()? "TRUE": "FALSE";
            }

            @Override
            public LogicBoolean valueOf(String input) {
                if(input.equalsIgnoreCase("TRUE"))
                    return new LogicBoolean(Boolean.valueOf(true));

                if(input.equalsIgnoreCase("FALSE"))
                    return new LogicBoolean(Boolean.valueOf(false));

                return null;
            }

            @Override
            public String toString() {
                return getFormattedString();
            }

            @Override
            public String toString(boolean addResultAsPrefix, boolean addResultAsPostfix) {
                return toString();
            }

            @Override
            public LogicBoolean getValue() {
                return mValue;
            }
        };

        staticEntityDataType.mValue = aLogicBoolean;

        return staticEntityDataType;
    }

    public AccessModifier getAccessModifier() {
        return mAccessModifier;
    }

    public void setAccessModifier(AccessModifier accessModifier) {
        mAccessModifier = accessModifier;
    }


}
