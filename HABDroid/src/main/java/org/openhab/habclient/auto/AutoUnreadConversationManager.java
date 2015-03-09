package org.openhab.habclient.auto;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by Tony Alpskog in 2015.
 */
public class AutoUnreadConversationManager implements IAutoUnreadConversationManager {
    public static final String UNREAD_CONVERSATION_BUILDER_NAME = "OpenHab";
    public static final String MESSAGE_CONVERSATION_ID_KEY = "Android_Auto_Conversation_Id";
    public static final String MESSAGE_READ_ACTION = "Android_Auto_Conversation_Read_Action";

    private final Context mContext;
    private Map<Integer, List<String>> mUnreadConversations;
    private int nextConversationId = 0;
    private final int CONVERSATION_ID = 0;

    long mLatestTimestamp = Calendar.getInstance().get(Calendar.SECOND);

    @Inject
    public AutoUnreadConversationManager(Context context) {
        mContext = context;
        mUnreadConversations = new HashMap<Integer, List<String>>();
    }

    @Override
    public int addMessageToUnreadConversations(String message) {
        return addMessageToUnreadConversations(message, CONVERSATION_ID);
    }

    private int addMessageToUnreadConversations(String message, int conversationId) {
        List<String> messageList = mUnreadConversations.get(Integer.valueOf(conversationId));
        if(messageList == null)
            messageList = new ArrayList<String>();
        messageList.add(message);
        mUnreadConversations.put(conversationId, messageList);
        setLatestTimestamp(Calendar.getInstance().get(Calendar.SECOND));
        return conversationId;
    }

    @Override
    public void removeMessageFromUnreadConversations(int conversationId) {
        if(mUnreadConversations.containsKey(Integer.valueOf(conversationId))) {
            List<String> conversationMessages = mUnreadConversations.get(Integer.valueOf(conversationId));
            conversationMessages.remove(0);
        }
    }

    @Override
    public NotificationCompat.CarExtender.UnreadConversation getUnreadConversation() {
        return getUnreadConversation(CONVERSATION_ID);
    }

    private NotificationCompat.CarExtender.UnreadConversation getUnreadConversation(int conversationId) {
        NotificationCompat.CarExtender.UnreadConversation.Builder unreadConversationBuilder =
                new NotificationCompat.CarExtender.UnreadConversation.Builder( UNREAD_CONVERSATION_BUILDER_NAME );

        unreadConversationBuilder
                .setReadPendingIntent(getMessageReadPendingIntent(conversationId))
//                .setReplyAction(getMessageReplyPendingIntent(), getVoiceReplyRemoteInput())
//                .addMessage("Warning. The living room sofa is occupied by cats!")
                .setLatestTimestamp(getLatestTimestamp());

        for(String message : mUnreadConversations.get(conversationId)) {
            unreadConversationBuilder.addMessage(message);
        }

        return unreadConversationBuilder.build();
    }

    private PendingIntent getMessageReadPendingIntent(int conversationId) {
        return PendingIntent.getBroadcast(mContext,
                1,
                getMessageReadIntent(conversationId),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Intent getMessageReadIntent(int conversationId) {
        return new Intent()
                .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                .setAction(MESSAGE_READ_ACTION)
                .putExtra(MESSAGE_CONVERSATION_ID_KEY, conversationId);
    }

//    private PendingIntent getMessageReplyPendingIntent() {
//        return PendingIntent.getBroadcast(mContext,
//                1,
//                getMessageReplyIntent(),
//                PendingIntent.FLAG_UPDATE_CURRENT);
//    }
//
//    private Intent getMessageReplyIntent() {
//        return new Intent()
//                .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
//                .setAction(MESSAGE_REPLY_ACTION)
//                .putExtra(MESSAGE_CONVERSATION_ID_KEY, 1);
//    }
//
//    private RemoteInput getVoiceReplyRemoteInput() {
//        return new RemoteInput.Builder(VOICE_REPLY_KEY)
//                .setLabel("Reply")
//                .build();
//    }

    public long getLatestTimestamp() {
        return mLatestTimestamp;
    }

    public void setLatestTimestamp(long latestTimestamp) {
        mLatestTimestamp = latestTimestamp;
    }
}
