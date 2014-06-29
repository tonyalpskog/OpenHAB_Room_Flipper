package org.openhab.domain.business.rule;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface OnValueChangedListener {
    public String  getDataSourceId();
    public void onValueChanged(String sourceID, String Value);
}
