package com.zenit.habclient;

import com.zenit.habclient.rule.IEntityDataType;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface OnOperandValueChangedListener {
    public void onOperandValueChanged(IEntityDataType operand);
}
