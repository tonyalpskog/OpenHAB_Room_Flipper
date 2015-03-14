package org.openhab.habclient.auto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony Alpskog in 2015.
 */
public class Conversation {
    private int mId;
    private String mStringId;
    private String mTitle;
    private long mLatestTimestamp;
    private List<String> mMessages;

    private final int FIRST_LOCATION_INDEX = 0;

    public Conversation() {
        mStringId = "HARD_CODED_ID";
        mMessages = new ArrayList<String>();
    }

    public Conversation(int conversationId, String title) {
        this();
        mId = conversationId;
        mTitle = title;
    }

    public Conversation(int conversationId, String title, String message) {
        this(conversationId, title);
        putMessage(message);
    }

    public void putMessage(String message) {
        mMessages.add(message);
        mLatestTimestamp = System.currentTimeMillis();
    }

    public boolean popMessage() {
        if(mMessages.isEmpty())
            return false;
        mMessages.remove(FIRST_LOCATION_INDEX);
        return true;
    }

    public boolean hasMessages() {
        return mMessages.size() > 0;
    }

    public int getId() {
        return mId;
    }

    public String getStringId() {
        return mStringId;
    }

    public void setStringId(String stringId) {
        mStringId = stringId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public long getLatestTimestamp() {
        return mLatestTimestamp;
    }

    public List<String> getMessages() {
        return mMessages;
    }
}
