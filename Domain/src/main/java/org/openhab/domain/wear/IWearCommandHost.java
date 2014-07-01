package org.openhab.domain.wear;

/**
 * Created by Tony Alpskog on 2014-07-01.
 */
public interface IWearCommandHost {
    String EXTRA_REPLY = "Command";

    void registerReceiver();

    void unregisterReceiver();

    void startSession(String title, String message);
}
