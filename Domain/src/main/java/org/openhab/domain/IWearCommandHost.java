package org.openhab.domain;

/**
 * Created by Tony Alpskog in 2015.
 */
public interface IWearCommandHost {
    void registerReceivers();
    void unregisterReceivers();

    boolean isRegistered();
}
