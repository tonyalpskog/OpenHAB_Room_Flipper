package org.openhab.habclient.rule;

import org.openhab.habclient.OnOperandValueChangedListener;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface IRuleOperationOperand {
    public void setOnOperandValueChangedListener(OnOperandValueChangedListener onOperandValueChangedListener);
}
