package org.openhab.domain.wear;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface IWearCommandHost {
    String EXTRA_REPLY = "Command";

    void registerReceiver();

    void unregisterReceiver();

    void startSession(String title, String message);
}
