package org.openhab.habclient.auto;

import android.app.PendingIntent;
import android.support.v4.app.NotificationCompat;

import org.openhab.domain.SenderType;

/**
 * Created by Tony Alpskog in 2015.
 */
public interface IAutoUnreadConversationManager {
    int addMessageToUnreadConversations(SenderType senderType, String stringId, String message);
    void removeMessageFromUnreadConversations(int conversationId);
    NotificationCompat.CarExtender.UnreadConversation[] getUnreadConversations();
    boolean isOpenHABSystemConversation(int conversationId);
    int getConservationId(SenderType senderType, String stringId);
    void addMessageToUnreadConversations(int conversationId, String stringId, String message);
    PendingIntent getMessageReadPendingIntent(int conversationId);
}
