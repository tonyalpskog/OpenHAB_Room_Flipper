package org.openhab.habclient.auto;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.util.SparseArray;

import org.openhab.habclient.dagger.ApplicationContext;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Tony Alpskog in 2015.
 */
@Singleton
public class AutoUnreadConversationManager implements IAutoUnreadConversationManager {
    public static final String UNREAD_CONVERSATION_BUILDER_NAME = "OpenHabben";
    public static final String NOTIFICATION_CONVERSATION_ID_KEY = "Android_Auto_Conversation_Id";
    public static final String NOTIFICATION_READ_ACTION = "Android_Auto_Conversation_Read_Action";
    public static final String NOTIFICATION_REPLY_ACTION = "Android_Auto_Conversation_Reply_Action";
    public static final String AUTO_VOICE_REPLY_KEY = "Android_Auto_Voice_Reply";
    private static final int CONVERSATION_ID = 99;

    private final Context mContext;
    private Map<String, Integer> mConversationsIdMap;
    private SparseArray<Conversation> mUnreadConversations;

    long mLatestTimestamp = Calendar.getInstance().get(Calendar.SECOND);

    @Inject
    public AutoUnreadConversationManager(@ApplicationContext Context context) {
        mContext = context;
        mConversationsIdMap = new HashMap<String, Integer>();
        mUnreadConversations = new SparseArray<Conversation>();
    }

    @Override
    public int addMessageToUnreadConversations(String stringId, String message) {
        if(!mConversationsIdMap.containsKey(stringId))
            mConversationsIdMap.put(stringId, CONVERSATION_ID);//Hardcoded for now. Not sure yet if multiple id's really needed.
        int conversationId = mConversationsIdMap.get(stringId);
        return addMessageToUnreadConversations(conversationId, stringId, message);
    }

    private int addMessageToUnreadConversations(int conversationId, String title, String message) {
        Conversation conversation = mUnreadConversations.get(conversationId);
        if(conversation == null) {
            conversation = new Conversation(conversationId, title, message);
            mUnreadConversations.put(conversationId, conversation);
        } else
            conversation.putMessage(message);
        //setLatestTimestamp(Calendar.getInstance().get(Calendar.SECOND));
        return conversationId;
    }

    @Override
    public void removeMessageFromUnreadConversations(int conversationId) {
        Conversation conversation = mUnreadConversations.get(conversationId);
        if(conversation != null) {
            conversation.popMessage();
            if(!conversation.hasMessages())
                mUnreadConversations.remove(conversationId);
        }
    }

    @Override
    public NotificationCompat.CarExtender.UnreadConversation[] getUnreadConversations() {
        return new NotificationCompat.CarExtender.UnreadConversation[] {getUnreadConversation(CONVERSATION_ID)};//Hardcoded for now. Not sure yet if multiple id's really needed.
    }

    @Override
    public Conversation getConversation(String stringId) {
        return null;
    }

    private NotificationCompat.CarExtender.UnreadConversation getUnreadConversation(int conversationId) {
        Conversation conversation = mUnreadConversations.get(conversationId);
        if(conversation == null)
            return null;

        NotificationCompat.CarExtender.UnreadConversation.Builder unreadConversationBuilder =
                new NotificationCompat.CarExtender.UnreadConversation.Builder(conversation.getTitle());

        unreadConversationBuilder
                .setReadPendingIntent(getMessageReadPendingIntent(conversationId))
                .setReplyAction(getMessageReplyPendingIntent(), getVoiceReplyRemoteInput())
//                .putMessage("Warning. The living room sofa is occupied by cats!")
                .setLatestTimestamp(getLatestTimestamp());

        for(String message : conversation.getMessages()) {
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
                .setAction(NOTIFICATION_READ_ACTION)
                .putExtra(NOTIFICATION_CONVERSATION_ID_KEY, conversationId);
    }

    private PendingIntent getMessageReplyPendingIntent() {
        return PendingIntent.getBroadcast(mContext,
                1,
                getMessageReplyIntent(),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Intent getMessageReplyIntent() {
        return new Intent()
                .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                .setAction(NOTIFICATION_REPLY_ACTION)
                .putExtra(NOTIFICATION_CONVERSATION_ID_KEY, 1);
    }

    private RemoteInput getVoiceReplyRemoteInput() {
        return new RemoteInput.Builder(AUTO_VOICE_REPLY_KEY)
                .setLabel("Reply")
                .build();
    }

    public long getLatestTimestamp() {
        return mLatestTimestamp;
    }

    public void setLatestTimestamp(long latestTimestamp) {
        mLatestTimestamp = latestTimestamp;
    }
}
