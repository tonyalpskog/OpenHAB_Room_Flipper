package org.openhab.domain;

/**
 * Created by Tony Alpskog in 2015.
 */
public interface INodeMessageHandler {
    void handleNodeMessage(IDeviceCommunicator nodeCommunicator, String path, String message, String senderNodeId);
}
