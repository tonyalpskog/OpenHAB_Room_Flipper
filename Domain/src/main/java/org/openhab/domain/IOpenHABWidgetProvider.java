package org.openhab.domain;

import org.openhab.domain.model.OpenHABWidget;

public interface IOpenHABWidgetProvider {
    OpenHABWidget getWidgetByItemName(String itemName);
}
