package com.zenit.habclient.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Tony Alpskog in 2014.
 */
public class WidgetPhraseParentMatchResultList {
    private List<WidgetPhraseMatchResult> widgetMatchList;
    private int matchPercent;

    public WidgetPhraseParentMatchResultList() {
        this.widgetMatchList = new ArrayList<WidgetPhraseMatchResult>();
        this.matchPercent = 0;
    }

    public WidgetPhraseParentMatchResultList(List<WidgetPhraseMatchResult> widgetMatchList, int matchPercent) {
        this.widgetMatchList = widgetMatchList;
        this.matchPercent = matchPercent;
    }

    public List<WidgetPhraseMatchResult> getWidgetMatchList() {
        return widgetMatchList;
    }

    public void setWidgetMatchList(List<WidgetPhraseMatchResult> widgetMatchList) {
        this.widgetMatchList = widgetMatchList;
    }

    public int getMatchPercent() {
        int result = 0;
        Iterator<WidgetPhraseMatchResult> iterator = widgetMatchList.iterator();
        while (iterator.hasNext()) {
            result += iterator.next().getMatchPercent();
        }
        return result;
    }

    public String getTruncatedWidgetName() {
        StringBuilder sb = new StringBuilder();
        Iterator<WidgetPhraseMatchResult> iterator = widgetMatchList.iterator();
        while (iterator.hasNext()) {
            if(sb.length() > 0)
                sb.insert(0, " / ");
            sb.insert(0, iterator.next().getWidget().getLabelValue());
        }
        return sb.toString();
    }

    public String toString() {
        return "[" + getMatchPercent() + "%] " + widgetMatchList.get(0).getWidget().toString();
    }
}
