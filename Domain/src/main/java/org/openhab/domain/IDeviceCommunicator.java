package org.openhab.domain;

/**
 * Created by Tony Alpskog in 2015.
 */
public interface IDeviceCommunicator {
    void sendMessage(String path, String message, String nodeId);
    boolean isNodeOnline(String nodeId);
}
