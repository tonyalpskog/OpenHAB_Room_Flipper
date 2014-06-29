package org.openhab.habclient;

import org.openhab.habclient.rule.IEntityDataType;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface OnOperandValueChangedListener {
    public void onOperandValueChanged(IEntityDataType operand);
}
