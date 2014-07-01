package org.openhab.habclient.rule2.values;

import org.openhab.habclient.rule2.Node;

public abstract class ValueNode<T> implements Node {
    private T mValue;

    @Override
    public boolean isComplete() {
        return mValue != null;
    }

    public T getValue() {
        return mValue;
    }

    public void setValue(T value) {
        mValue = value;
    }
}
