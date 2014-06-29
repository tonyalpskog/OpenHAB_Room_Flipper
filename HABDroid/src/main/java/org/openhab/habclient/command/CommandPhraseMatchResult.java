package org.openhab.habclient.command;

import org.openhab.domain.util.StringHandler;

/**
 * Created by Tony Alpskog in 2014.
 */
public class CommandPhraseMatchResult {
    protected OpenHABWidgetCommandType commandType;
    protected String[] tags;
    protected String[] tagPhrases;
    protected int point;

    public CommandPhraseMatchResult(OpenHABWidgetCommandType commandType, String[] tags, String[] tagPhrases, int point) {
        setCommandType(commandType);
        setTags(tags);
        setTagPhrases(tagPhrases);
        setPoint(point);
    }

    public OpenHABWidgetCommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(OpenHABWidgetCommandType commandType) {
        this.commandType = commandType;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String[] getTagPhrases() {
        return tagPhrases;
    }

    public void setTagPhrases(String[] tagPhrases) {
        this.tagPhrases = tagPhrases;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String toString() {
        return "[Point=" + getPoint() + ", Type=" + getCommandType().Name + ", Tags=" + StringHandler.getItemArrayAsString(getTags()) + ", Phrases=" + StringHandler.getItemArrayAsString(getTagPhrases()) + "]";
    }
}
