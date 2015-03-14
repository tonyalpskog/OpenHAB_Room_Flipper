package org.openhab.domain;

/**
 * Created by Tony Alpskog in 2015.
 */
public interface INotificationReplyHandler {
    void handleReplyMessage(String replyText, long[] vibratePattern);
}
