package org.openhab.habwear.habbutler;

import android.support.annotation.Nullable;

/**
 * Created by Tony Alpskog in 2015.
 */
public interface IDeviceCommunicator {
    void resume();

    void sendMessage(String path, @Nullable byte[] data);
    boolean isNodeOnline(String nodeId);
    void dispose();
}
