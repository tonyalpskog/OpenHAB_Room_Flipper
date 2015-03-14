package org.openhab.habclient.auto;

import android.support.v4.app.NotificationCompat;

/**
 * Created by Tony Alpskog in 2015.
 */
public interface IAutoUnreadConversationManager {
    int addMessageToUnreadConversations(String stringId, String message);
    void removeMessageFromUnreadConversations(int conversationId);
    NotificationCompat.CarExtender.UnreadConversation[] getUnreadConversations();
    Conversation getConversation(String stringId);
}
