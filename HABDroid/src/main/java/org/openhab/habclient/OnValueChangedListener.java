package org.openhab.habclient;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface OnValueChangedListener {
    public String  getDataSourceId();
    public void onValueChanged(String sourceID, String Value);
}
