package org.openhab.domain;

/**
 * Created by Tony Alpskog in 2015.
 */
public interface INotificationReplyHandler {
    void handleReplyMessage(int conversationId, String replyText, long[] vibratePattern);
}
