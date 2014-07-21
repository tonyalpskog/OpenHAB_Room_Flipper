package org.openhab.domain.rule.operations;

import org.openhab.domain.rule.IEntityDataType;
import org.openhab.domain.rule.operations.bool.BooleanAndRuleOperation;

public abstract class CompositeRuleOperation<T> extends RuleOperation<T> {
    private BooleanAndRuleOperation mBooleanAndRuleOperation;
    private RuleOperation<T> mLeft;
    private RuleOperation<T> mRight;

    public CompositeRuleOperation(String name) {
        super(name);

        mBooleanAndRuleOperation = new BooleanAndRuleOperation();
        mLeft = createLeft();
        mRight = createRight();
        mBooleanAndRuleOperation.setLeft(mLeft);
        mBooleanAndRuleOperation.setRight(mRight);
    }

    protected abstract RuleOperation<T> createLeft();
    protected abstract RuleOperation<T> createRight();

    @Override
    public Boolean getValue() {
        return mBooleanAndRuleOperation.getValue();
    }

    public void setCompareValue(IEntityDataType<T> value) {
        mRight.setLeft(value);
        mLeft.setLeft(value);
    }

    @Override
    public void setLeft(IEntityDataType<T> left) {
        super.setLeft(left);

        mLeft.setRight(left);
    }

    @Override
    public void setRight(IEntityDataType<T> right) {
        super.setRight(right);

        mRight.setRight(right);
    }
}
