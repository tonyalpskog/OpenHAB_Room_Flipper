package org.openhab.domain.command;

import org.openhab.domain.model.OpenHABWidget;

/**
 * Created by Tony Alpskog in 2014.
 */
public class WidgetPhraseMatchResult {
    private int matchPercent;
    private OpenHABWidget foundWidget;

    public WidgetPhraseMatchResult(int matchPercent, OpenHABWidget widget) {
        this.matchPercent = matchPercent;
        this.foundWidget = widget;
    }

    public int getMatchPercent() {
        return matchPercent;
    }

    public void setMatchPercent(int matchPercent) {
        this.matchPercent = matchPercent;
    }

    public OpenHABWidget getWidget() {
        return foundWidget;
    }

    public void setWidget(OpenHABWidget foundWidget) {
        this.foundWidget = foundWidget;
    }

    public String toString() {
        return "[" + getMatchPercent() + "%] " + getWidget().toString();
    }
}
