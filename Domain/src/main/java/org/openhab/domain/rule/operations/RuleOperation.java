package org.openhab.domain.rule.operations;

import org.openhab.domain.rule.EntityDataType;
import org.openhab.domain.rule.EntityDataTypeSource;
import org.openhab.domain.rule.IEntityDataType;

import java.util.UUID;

/**
 * Created by Tony Alpskog in 2014.
 */
public abstract class RuleOperation<T> extends EntityDataType<Boolean> {
    private IEntityDataType<T> mLeft;
    private IEntityDataType<T> mRight;

    public RuleOperation() {
        super(Boolean.class);

        setDataSourceId(UUID.randomUUID().toString());
    }

    public RuleOperation(String name) {
        this();
        setName(name);
    }

    @Override
    public EntityDataTypeSource getSourceType() {
        return EntityDataTypeSource.OPERATION;
    }


    @Override
    public abstract Boolean getValue();

    public IEntityDataType<T> getLeft() {
        return mLeft;
    }

    public IEntityDataType<T> getRight() {
        return mRight;
    }

    public void setLeft(IEntityDataType<T> left) {
        mLeft = left;
    }

    public void setRight(IEntityDataType<T> right) {
        mRight = right;
    }

    protected T getLeftValue() {
        if(mLeft == null)
            return null;

        return mLeft.getValue();
    }

    protected T getRightValue() {
        if(mRight == null)
            return null;

        return mRight.getValue();
    }
}
