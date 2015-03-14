package org.openhab.domain;

/**
 * Created by Tony Alpskog in 2015.
 */
public interface INotificationHost {
    void registerReceivers();
    void unregisterReceivers();

    boolean isRegistered();
}
