package org.openhab.habclient.auto;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.util.SparseArray;

import org.openhab.habclient.dagger.ApplicationContext;

import org.openhab.domain.SenderType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Tony Alpskog in 2015.
 */
@Singleton
public class AutoUnreadConversationManager implements IAutoUnreadConversationManager {
    public static final String AUTO_NOTIFICATION_CONVERSATION_ID_KEY = "Android_Auto_Conversation_Id";
    public static final String AUTO_NOTIFICATION_READ_ACTION = "Android_Auto_Conversation_Read_Action";
    public static final String AUTO_NOTIFICATION_REPLY_ACTION = "Android_Auto_Conversation_Reply_Action";
    public static final String AUTO_VOICE_REPLY_KEY = "Android_Auto_Voice_Reply";

    private final int OPENHAB_SYSTEM_CONVERSATION_ID = 0;

    private final Context mContext;
    private Map<String, Integer> mConversationsIdMap;
    private SparseArray<Conversation> mUnreadConversations;
    private int nextConversationId = OPENHAB_SYSTEM_CONVERSATION_ID + 1;

    long mLatestTimestamp = Calendar.getInstance().get(Calendar.SECOND);

    @Inject
    public AutoUnreadConversationManager(@ApplicationContext Context context) {
        mContext = context;
        mConversationsIdMap = new HashMap<String, Integer>();
        mUnreadConversations = new SparseArray<Conversation>();
    }

    @Override
    public int addMessageToUnreadConversations(SenderType senderType, String stringId, String message) {
        int conversationId = getConservationId(senderType, stringId);
        addMessageToUnreadConversations(conversationId, stringId, message);
        return conversationId;
    }

    @Override
    public void addMessageToUnreadConversations(int conversationId, String title, String message) {
        Conversation conversation = mUnreadConversations.get(conversationId);
        if(conversation == null) {
            conversation = new Conversation(conversationId, title, message);
            mUnreadConversations.put(conversationId, conversation);
        } else
            conversation.putMessage(message);
        setLatestTimestamp(conversation.getLatestTimestamp());
    }

    @Override
    public void removeMessageFromUnreadConversations(int conversationId) {
        if(mUnreadConversations.containsKey(conversationId)) {
            Conversation conversation = mUnreadConversations.get(conversationId);
            conversation.popMessage();
            if(!conversation.hasMessages())
                mUnreadConversations.remove(conversationId);
        }
    }

    @Override
    public NotificationCompat.CarExtender.UnreadConversation[] getUnreadConversations() {
        List<NotificationCompat.CarExtender.UnreadConversation> conversationsList = new ArrayList<NotificationCompat.CarExtender.UnreadConversation>();
        List<Integer> usedIdList = new ArrayList<Integer>();
        for(int id : mConversationsIdMap.values()) {
            if(usedIdList.contains(id))
                continue;
            usedIdList.add(id);
            NotificationCompat.CarExtender.UnreadConversation conversation = getUnreadConversation(id);
            if(conversation != null)
                conversationsList.add(conversation);
        }
        return conversationsList.toArray(new NotificationCompat.CarExtender.UnreadConversation[conversationsList.size()]);
    }

    @Override
    public boolean isOpenHABSystemConversation(int conversationId) {
        return conversationId == OPENHAB_SYSTEM_CONVERSATION_ID;
    }

    @Override
    public int getConservationId(SenderType senderType, String stringId) {
        if(!mConversationsIdMap.containsKey(stringId)) {
            int conversationId = OPENHAB_SYSTEM_CONVERSATION_ID;
            if(!senderType.equals(SenderType.System))
                conversationId = nextConversationId++;
            mConversationsIdMap.put(stringId, conversationId);
        }
        return mConversationsIdMap.get(stringId);
    }

    private NotificationCompat.CarExtender.UnreadConversation getUnreadConversation(int conversationId) {
        Conversation conversation = mUnreadConversations.get(conversationId);
        if(conversation == null)
            return null;

        NotificationCompat.CarExtender.UnreadConversation.Builder unreadConversationBuilder =
                new NotificationCompat.CarExtender.UnreadConversation.Builder(conversation.getTitle());

        unreadConversationBuilder
                .setReadPendingIntent(getMessageReadPendingIntent(conversationId))
                .setReplyAction(getMessageReplyPendingIntent(conversationId), getVoiceReplyRemoteInput())
                .setLatestTimestamp(getLatestTimestamp());

        for(String message : conversation.getMessages()) {
            unreadConversationBuilder.addMessage(message);
        }

        return unreadConversationBuilder.build();
    }

    @Override
    public PendingIntent getMessageReadPendingIntent(int conversationId) {
        return PendingIntent.getBroadcast(mContext,
                conversationId,
                getMessageReadIntent(conversationId),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Intent getMessageReadIntent(int conversationId) {
        return new Intent()
                .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                .setAction(AUTO_NOTIFICATION_READ_ACTION)
                .putExtra(AUTO_NOTIFICATION_CONVERSATION_ID_KEY, conversationId);
    }

    private PendingIntent getMessageReplyPendingIntent(int conversationId) {
        return PendingIntent.getBroadcast(mContext,
                conversationId,
                getMessageReplyIntent(conversationId),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Intent getMessageReplyIntent(int conversationId) {
        return new Intent()
                .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                .setAction(AUTO_NOTIFICATION_REPLY_ACTION)
                .putExtra(AUTO_NOTIFICATION_CONVERSATION_ID_KEY, conversationId);
    }

    private RemoteInput getVoiceReplyRemoteInput() {
        return new RemoteInput.Builder(AUTO_VOICE_REPLY_KEY)
                .setLabel("Reply")//TODO - Change label to "Command" if conversationId = OpenHAB system
                .build();
    }

    public long getLatestTimestamp() {
        return mLatestTimestamp;
    }

    public void setLatestTimestamp(long latestTimestamp) {
        mLatestTimestamp = latestTimestamp;
    }
}
