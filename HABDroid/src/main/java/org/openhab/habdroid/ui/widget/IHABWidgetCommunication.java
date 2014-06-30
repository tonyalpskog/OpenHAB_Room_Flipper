package org.openhab.habdroid.ui.widget;

import org.openhab.domain.model.OpenHABItem;

/**
 * Created by Tony Alpskog in 2014.
 */
public interface IHABWidgetCommunication {
    public void sendItemCommand(OpenHABItem item, String command);
    public String getNewValueAsFullText(String currentFullTextValue, float value);
    public String getOpenHABBaseUrl();
    public String getOpenHABUsername();
    public String getOpenHABPassword();
}
