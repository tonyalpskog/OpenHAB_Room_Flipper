package org.openhab.domain.model;

import org.openhab.domain.model.OpenHABWidget;

/**
 * Created by Tony Alpskog in 2014.
 */
public class OpenHABWidgetEvent {
    private OpenHABWidget mOpenHABWidget;

    public OpenHABWidgetEvent(OpenHABWidget openHABWidget) {
        mOpenHABWidget = openHABWidget;
    }

    public OpenHABWidget getOpenHABWidget() {
        return mOpenHABWidget;
    }
}
